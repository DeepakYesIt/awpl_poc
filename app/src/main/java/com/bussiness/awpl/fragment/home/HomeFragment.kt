package com.bussiness.awpl.fragment.home

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bussiness.awpl.HealthDataStore
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.VideoCallActivity
import com.bussiness.awpl.adapter.BrowseVideoAdapter
import com.bussiness.awpl.adapter.HealthJourneyAdapter1
import com.bussiness.awpl.adapter.OrganListAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentHomeBinding
import com.bussiness.awpl.model.HealthListModel
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.DiseaseModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var organListAdapter: OrganListAdapter
    private var countdownJob: Job? = null
    private lateinit var healthJourneyAdapter: HealthJourneyAdapter1
    private lateinit var browseVideoAdapter: BrowseVideoAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var diseaseList : MutableList<DiseaseModel>
    private var appoitmentId :Int =0
    private var startAppointment :Int =0
    private var doctorName :String =""

    private val healthJourneyList = listOf(
        HealthListModel("Begin Your Health\nJourney with a \nFree Consultation!", R.drawable.women_doctor),
        HealthListModel("Bringing Doctors\n to Your Door – \nVirtually.", R.drawable.ic_rename_doctor),
        HealthListModel("Upload Symptoms \nfor Minor Issues \nand Major Concerns", R.drawable.ic_little_girl)
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        binding.textView45.setOnClickListener {
            findNavController().navigate(R.id.appointmentBooking)
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        clickListener()
        callingHomeApi()

//        binding.swipeRefreshLayout.setOnRefreshListener {
//            // Reload data here
//            fetchData()
//        }

        binding.startAppointmentBtn.setOnClickListener {

            if(startAppointment !=0) {
                lifecycleScope.launch {
                    LoadingUtils.showDialog(requireContext(),false)
                    homeViewModel.createChannel(startAppointment).collect {
                        when(it){
                            is NetworkResult.Success ->{
                                it.data?.let {
                                    val intent =
                                        Intent(requireContext(), VideoCallActivity::class.java)
                                    intent.putExtra(AppConstant.APPID, it.appId)
                                    intent.putExtra(AppConstant.AuthToken, it.token)
                                    intent.putExtra(AppConstant.CHANNEL_NAME, it.channelName)
                                    intent.putExtra(AppConstant.uid, it.uid)
                                    intent.putExtra(AppConstant.DOCTOR, doctorName)

                                    callingCallJoinedApi(startAppointment,intent)
                                }


                            }
                            is NetworkResult.Error ->{
                                LoadingUtils.hideDialog()
                                LoadingUtils.showErrorDialog(requireContext(),it.message.toString())

                            }
                            else ->{

                            }
                        }
                    }
                }
            }
        }

        setupSwipeToRefresh()
       // callingDiseaseApi()

    }

    private fun callingCallJoinedApi(startAppointment: Int, intent: Intent) {

        lifecycleScope.launch {
            homeViewModel.callJoined(startAppointment).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()

                        startActivity(intent)

                    }
                    is NetworkResult.Error ->{

                        LoadingUtils.hideDialog()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun setupSwipeToRefresh() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            loadFaqs()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadFaqs() {

//        binding.swipeRefreshLayout.isRefreshing = true
//        callingHomeApi()

    }

    private fun fetchData() {
        // Load data here (API, DB, etc.)
      //  callingHomeApi()
        // After data is loaded
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callingHomeApi(){
       LoadingUtils.showDialog(requireActivity(),false)
        lifecycleScope.launch {
              homeViewModel.getHomeData().collect{
                  when(it){
                      is NetworkResult.Success ->{
                          LoadingUtils.hideDialog()
                          settingDataToUi(it.data)
                      }
                      is NetworkResult.Error ->{

                          LoadingUtils.hideDialog()
                      }
                      else ->{
                      }
                  }
              }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun settingDataToUi(data: HomeModel?) {

        if(data?.startAppointDetails == null){
            binding.llTop.visibility =View.GONE
        }

        data?.startAppointDetails?.let {
            startAppointment = it.id
           binding.llTop.visibility = View.VISIBLE
           binding.stDoctorName.setText(it.doctorName.toString())
            doctorName = it.doctorName
           binding.tvDate.setText(it.date)
           binding.tvTime.setText(it.time)
            startCountdown(it.time)
            Log.d("TESTING_URL",AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage))
           Glide.with(this).load(AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage)).into(binding.stDoctorImage)
         //  var time = MultipartUtil.getMinutesUntilStart(it.time.split("-")[0].trim())
       } ?: {
           binding.llTop.visibility =View.GONE
        }
        if(data?.upcomingAppointDetails == null){
            binding.scheduleCardAppointment.visibility = View.VISIBLE
            binding.cardView4.visibility = View.GONE
        }

        data?.upcomingAppointDetails?.let {
            appoitmentId=  it.id
            binding.scheduleCardAppointment.visibility = View.GONE
            binding.cardView4.visibility = View.VISIBLE
            binding.doctorName.setText(it.doctorName)
            binding.tvDateUpCom.setText(it.date)
            binding.tvTimeUpCom.setText(it.time)
            Log.d("TESTING_URL",AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage))
            Glide.with(this).load(AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage)).into(binding.upDoctorImage)
        } ?: {
            binding.scheduleCardAppointment.visibility = View.VISIBLE
            binding.cardView4.visibility = View.GONE
        }

        data?.healthNeeds?.let {
            diseaseList = it.toMutableList()
            organListAdapter.updateAdapter(it)
            HealthDataStore.saveHealthNeeds(it)
        }

        data?.videos?.let {
             browseVideoAdapter.updateAdapter(it)
        }

    }

    private fun tempData(selectedDisease: DiseaseModel) {
//        val result = if((0..1).random() == 0)"one" else " two"


        var bundle = Bundle().apply {
            putInt(AppConstant.DISEASE_ID, selectedDisease.id)
        }

        if (selectedDisease.category == "major"){
            findNavController().navigate( R.id.doctorConsultationFragment,bundle)
        }
        else{
            findNavController().navigate( R.id.onlineConsultationFragment,bundle)
        }

    }

    private fun setupRecyclerViews() {

        // Organ List RecyclerView
        binding.deptRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            organListAdapter = OrganListAdapter(mutableListOf()){ selectedDisease ->
                              tempData(selectedDisease)
            }
            adapter = organListAdapter
        }

        // Health Journey RecyclerView
        binding.bannerRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            healthJourneyAdapter = HealthJourneyAdapter1(healthJourneyList) { item ->
                var bundle = Bundle().apply {
                        putString("type","schedule")
                       // putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                    }
                    findNavController().navigate(R.id.diseasesBottomFragment,bundle)


            }

            adapter = healthJourneyAdapter

        }

        // Browse Video RecyclerView
        binding.videoRecyclerView.apply {
            browseVideoAdapter = BrowseVideoAdapter(mutableListOf()) { item ->
               
            openVideo(item.video_link)
            }
            adapter = browseVideoAdapter
        }

    }
    private fun openVideo(videoUrl :String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(videoUrl), "video/*")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun clickListener() {
        binding.apply {

            scheduleButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                }
                findNavController().navigate(R.id.diseasesBottomFragment)
            }

            txtSeeAllDisease.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                }
                findNavController().navigate(R.id.diseasesBottomFragment)
            }

            symptomUploadBtn.setOnClickListener {

                val bundle = Bundle().apply {
                  //  putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                    putString("type","symptom")
                }
                findNavController().navigate(R.id.diseasesBottomFragment,bundle)
            }

            seeAllVideos.setOnClickListener{
                findNavController().navigate(R.id.videoGalleryFragment)
            }

            scheduleCallBtn.setOnClickListener  {
                var bundle = Bundle().apply {
                    putString("type","schedule")
                  //  putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                }
               findNavController().navigate(R.id.diseasesBottomFragment,bundle)
            }

            upcomingSeeAll.setOnClickListener   {
                findNavController().navigate(R.id.scheduleFragment)
            }

            rescheduleButton.setOnClickListener {

                if(appoitmentId !=0) {
                    var bundle = Bundle().apply {
                        putInt(AppConstant.AppoitmentId, appoitmentId)
                    }
                    findNavController().navigate(R.id.reschedule_call,bundle)
                }
            }


            cancelBtn.setOnClickListener{
                cancelDialog()
            }
        }
    }

    private fun cancelDialog() {
        val dialog = Dialog(requireContext())
        val binding = DialogCancelAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNo.setOnClickListener { dialog.dismiss() }
            btnYes.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.appointmentBooking)
            }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

      //  timeObserver?.let { viewModel.timeLeft.removeObserver(it) }
    }

    private fun updateAdapter(){

    }


    private fun startCountdown(timeRange: String) {
        countdownJob?.cancel()

        countdownJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val parts = timeRange.trim().split("-")
                if (parts.size != 2) {
                    showCountdownMessage("Invalid time range")
                    return@launch
                }

                val startTimeRaw = parts[0].trim()              // "08:00"
                val endTimePart = parts[1].trim()               // "08:15 AM"
                val amPm = endTimePart.takeLast(2).uppercase(Locale.getDefault()) // "AM" or "PM"
                val fullStartTime = "$startTimeRaw $amPm"       // "08:00 AM"

                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val parsedStartTime = try {
                    sdf.parse(fullStartTime)
                } catch (e: Exception) {
                    null
                }

                if (parsedStartTime == null) {
                    showCountdownMessage("Invalid time format")
                    return@launch
                }

                val calendarNow = Calendar.getInstance()
                val calendarTarget = Calendar.getInstance().apply {
                    time = parsedStartTime
                    set(Calendar.YEAR, calendarNow.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendarNow.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendarNow.get(Calendar.DAY_OF_MONTH))
                    if (before(calendarNow)) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }

                while (true) {
                    val now = Calendar.getInstance()
                    val millisLeft = calendarTarget.timeInMillis - now.timeInMillis

                    if (millisLeft <= 0) {
                        withContext(Dispatchers.Main) {
                            binding.startAppointmentBtn.text= "Start Appointment"
                        }
                        break
                    }

                    val minutes = (millisLeft / 1000) / 60
                    val seconds = (millisLeft / 1000) % 60

                    withContext(Dispatchers.Main) {
                        binding.startAppointmentBtn.text =
                            String.format("Start appointment in %02d:%02d min", minutes, seconds)
                    }

                    delay(1000)
                }
            } catch (e: Exception) {
                Log.e("COUNTDOWN", "Unexpected error: ${e.message}")
                showCountdownMessage("Something went wrong")
            }
        }
    }

    private suspend fun showCountdownMessage(message: String) {
        withContext(Dispatchers.Main) {
            binding.startAppointmentBtn.text = message
        }
    }



}


//
//private fun callingDiseaseApi(){
//    lifecycleScope.launch(Dispatchers.IO) {
//        viewModel.getDiseaseList().collect { result ->
//            when (result) {
//                is NetworkResult.Success -> {
//                    result.data?.let { DiseaseStore.setDiseases(it) }
//                }
//                is NetworkResult.Error -> {
//
//                }
//                else -> {
//
//                }
//            }
//        }
//    }
//}

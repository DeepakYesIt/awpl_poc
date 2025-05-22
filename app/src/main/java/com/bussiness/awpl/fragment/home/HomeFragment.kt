package com.bussiness.awpl.fragment.home

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.BrowseVideoAdapter
import com.bussiness.awpl.adapter.HealthJourneyAdapter
import com.bussiness.awpl.adapter.HealthJourneyAdapter1
import com.bussiness.awpl.adapter.OrganListAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentHomeBinding
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.HealthListModel
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.DiseaseModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var organListAdapter: OrganListAdapter
    private lateinit var healthJourneyAdapter: HealthJourneyAdapter1
    private lateinit var browseVideoAdapter: BrowseVideoAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var diseaseList : MutableList<DiseaseModel>

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
       // callingDiseaseApi()


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

        data?.startAppointDetails?.let {
           binding.llTop.visibility = View.VISIBLE
           binding.stDoctorName.setText(it.doctorName.toString())
           binding.tvDate.setText(it.date)
           binding.tvTime.setText(it.time)
            Log.d("TESTING_URL",AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage))
           Glide.with(this).load(AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it.doctorImage)).into(binding.stDoctorImage)
         //  var time = MultipartUtil.getMinutesUntilStart(it.time.split("-")[0].trim())
       } ?: {
           binding.llTop.visibility =View.GONE
        }

        data?.upcomingAppointDetails?.let {
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
                        putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                    }
                    findNavController().navigate(R.id.diseasesBottomFragment,bundle)


            }

            adapter = healthJourneyAdapter

        }

        // Browse Video RecyclerView
        binding.videoRecyclerView.apply {
            browseVideoAdapter = BrowseVideoAdapter(mutableListOf()) { item ->
                Toast.makeText(requireContext(), "Playing Video: ${item.title}", Toast.LENGTH_SHORT).show()
            }
            adapter = browseVideoAdapter
        }

    }

    private fun clickListener() {
        binding.apply {

            txtSeeAllDisease.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                }
                findNavController().navigate(R.id.diseasesBottomFragment,bundle)
            }

            symptomUploadBtn.setOnClickListener {

                val bundle = Bundle().apply {
                    putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
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
                    putSerializable(AppConstant.DISEASE_LIST , ArrayList(diseaseList))
                }
                findNavController().navigate(R.id.diseasesBottomFragment,bundle)
            }

            upcomingSeeAll.setOnClickListener   {
                findNavController().navigate(R.id.scheduleFragment)
            }

            rescheduleButton.setOnClickListener {
                findNavController().navigate(R.id.appointmentBooking)
            }

            startAppointmentBtn.setOnClickListener{
                findNavController().navigate(R.id.videoCallFragment)
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

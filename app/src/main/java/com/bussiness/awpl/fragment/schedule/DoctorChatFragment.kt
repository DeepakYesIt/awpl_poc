package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bussiness.awpl.ChatItem
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.adapter.ChatAdapter

import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentDoctorChatBinding
import com.bussiness.awpl.model.ChatAppotmentDetails
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.repository.ChatRepository
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.DownloadWorker
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.ChatViewModel
import com.bussiness.awpl.viewmodel.ChattingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DoctorChatFragment : Fragment() {

    private var _binding: FragmentDoctorChatBinding? = null
    private val binding get() = _binding!!
    private var mediaUploadDialog: MediaUtils? = null
    private var currentType: String = ""
    private val mediaList = mutableListOf<MediaItem>()
    private lateinit var mediaAdapter: MediaAdapter
    lateinit var chatAdapter: ChatAdapter
    var currentUserId ="12"
    var receiverId ="11"
    var chatId ="105_11_12"
    var hashMap =HashMap<Uri,Boolean>()

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var chattingViewModel :ChattingViewModel

    private  var messageList = mutableListOf<ChatMessage>()

    private var appoitnmentId :Int= 54

    private var pdfUrl = ""

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    // Show in dialog first
//                    if(!hashMap.containsKey(it)) {
//                        hashMap.put(it,true)
//                        chatViewModel.sendImage(it)
                        mediaUploadDialog?.handleSelectedFile(it)

                }
            }
   }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDoctorChatBinding.inflate(inflater, container, false)

        chatViewModel =ViewModelProvider(this)[ChatViewModel::class.java]

        arguments?.let {
            if(it.containsKey(AppConstant.Chat) && it.getString(AppConstant.Chat) != null){
                var cc = it.getString(AppConstant.Chat)
                var arr = cc?.split("_")
                currentUserId  = arr?.get(2) ?: "0"
                receiverId = arr?.get(1)?:"0"
                if (cc != null) {
                    chatId = cc
                }
            }
            chatAdapter = ChatAdapter(mutableListOf(),currentUserId)
            binding.chatRecyclerView.layoutManager =LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            binding.chatRecyclerView.adapter =chatAdapter

            if(it.containsKey(AppConstant.AppoitmentId)){
                appoitnmentId = it.getInt(AppConstant.AppoitmentId)
                Log.d("TESTING_ID",appoitnmentId.toString())
            }

        }

        chattingViewModel = ViewModelProvider(this)[ChattingViewModel::class.java]

        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(groupMessagesByDate(messages))
            messageList =messages.toMutableList()
            binding.chatRecyclerView.scrollToPosition(messages.size)
        }

         callingChatApi()

        binding.downloadPrescriptionBtn.setOnClickListener {
            if(pdfUrl == null||pdfUrl.length ==0 ){
              LoadingUtils.showErrorDialog(requireContext(),"The prescription hasn't been uploaded yet.")
            }
            else {
                DownloadWorker().downloadPdfWithNotification(
                    requireContext(),
                    pdfUrl,
                    "Presception" + System.currentTimeMillis()
                )
                Toast.makeText(requireContext(),"Download Started!",Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun callingChatApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            chattingViewModel.checkAppoitmentDetails(appoitnmentId).collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        settingDataToUi(it.data)
                        Log.d("TESTING_CHAT_API","INSIDE SUCESS")
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        Log.d("TESTING_CHAT_API","INSIDE Failure")
                    }
                    else->{

                    }
                }
            }
        }
    }


    fun isDate7DaysOld(input: String): Boolean {
        try {
            // Normalize input: remove commas and trim spaces
            val cleanInput = input.trim().replace(",", "")
            // cleanInput becomes "Tue Jun 17"

            // Extract the date part (MMM dd)
            val parts = cleanInput.split(" ")
            if (parts.size < 3) return false

            val month = parts[1]
            val day = parts[2]
            val year = Calendar.getInstance().get(Calendar.YEAR)

            // Create a new date string with year: e.g., "Jun 17 2025"
            val dateString = "$month $day $year"

            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
            formatter.isLenient = false
            val parsedDate = formatter.parse(dateString) ?: return false

            // Set both dates to midnight (ignore time)
            val parsedCalendar = Calendar.getInstance().apply {
                time = parsedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If parsed date is in future (e.g., Dec 31 and today is Jan 1), assume previous year
            if (parsedCalendar.after(today)) {
                parsedCalendar.add(Calendar.YEAR, -1)
            }

            // Check difference in days
            val diffMillis = today.timeInMillis - parsedCalendar.timeInMillis
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)

            return diffDays >= 7

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun settingDataToUi(data: ChatAppotmentDetails?) {
        data?.let {

            binding.doctorName.setText(data.doctor_name.toString())

            data.doctor_profile_path?.let {
                Glide.with(requireContext()).load(AppConstant.Base_URL+it).into(binding.doctorImage)
            }

            it.date?.let {
                binding.dateTxt.setText(it)
                Log.d("TESTING_DATE",it +" "+isDate7DaysOld(it))
                if(isDate7DaysOld(it)){
                    binding.messageInputLayout.visibility =View.GONE
                }
                else{
                    binding.messageEditText.visibility =View.VISIBLE
                }
            }

            it.time?.let {
                binding.timeTxt.setText(it)
            }

            it.prescription?.let {
                pdfUrl = AppConstant.Base_URL+ it
            }

            it.doctor_profile_path?.let {
                chatAdapter.receiverImageUrl = AppConstant.Base_URL+ it
                Log.d("PROFILE_IAMGE",AppConstant.Base_URL+ it)
            }

            it.patient_profile_path?.let {
                chatAdapter.senderImageUrl = AppConstant.Base_URL+it
                Log.d("PROFILE_IAMGE",AppConstant.Base_URL+ it)
            }

        }
    }

    fun groupMessagesByDate(messages: List<ChatMessage>): List<ChatItem> {
        val grouped = messages.sortedBy { it.timestamp }
            .groupBy {
                val msgDate = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                val today = Calendar.getInstance()
                val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

                when {
                    msgDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            msgDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> {
                        "Today"
                    }
                    msgDate.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                            msgDate.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> {
                        "Yesterday"
                    }
                    else -> {
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
                    }
                }
            }

        val chatItems = mutableListOf<ChatItem>()
        for ((date, items) in grouped) {
            chatItems.add(ChatItem.DateHeader(date))
            chatItems.addAll(items.map { ChatItem.MessageItem(it) })
        }

        return chatItems
    }

//old solution
//    fun groupMessagesByDate(messages: List<ChatMessage>): List<ChatItem> {
//        val grouped = messages.sortedBy { it.timestamp }
//            .groupBy {
//                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
//            }
//
//        val chatItems = mutableListOf<ChatItem>()
//        for ((date, items) in grouped) {
//            chatItems.add(ChatItem.DateHeader(date))
//            chatItems.addAll(items.map { ChatItem.MessageItem(it) })
//        }
//        Log.d("Testing_chat_size",chatItems.size.toString())
//        chatItems.forEach {
//
//            when (val item = it) {
//                is ChatItem.DateHeader -> {
//                    Log.d("testing_date", item.date)
//                }
//
//                is ChatItem.MessageItem -> {
//                    Log.d("testing_date", item.message.message.toString())
//                }
//
//            }
//        }
//
//        return chatItems
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
        chatViewModel.initChat(receiverId,currentUserId,chatId, ChatRepository(FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance()))

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickListeners() {

        binding.apply {
            attachmentIcon.setOnClickListener {
                Log.d("TESTING","I AM INSIDE THE ON CLICK")
                openMediaDialog("image")
            }
            sendMessageButton.setOnClickListener{
                if(messageEditText.text.length ==0){
                    return@setOnClickListener
                }
                chatViewModel.sendTextMessage(messageEditText.text.toString())
                messageEditText.setText("")
                val message = ChatMessage(

                    senderId = currentUserId,
                    receiverId = receiverId,
                    message = messageEditText.text.toString()
                )
            }
        }

    }

    private fun openMediaDialog(type: String) {
        currentType = type
        mediaUploadDialog = MediaUtils(
            requireContext(), type, onFileSelected = { selectedFiles ->
                selectedFiles.forEach {
                    chatViewModel.sendImage(it)
                //addMediaItem(it, type)
                }
            }, onBrowseClicked = {
                openImagePicker(type) // this will launch intent from fragment
            },
            "Send"
        )
        mediaUploadDialog?.show()

    }

    private fun openImagePicker(type: String) {
        val intent = when (type) {
            "image" -> Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            else -> {
                Toast.makeText(requireContext(), "Unknown media type: $type", Toast.LENGTH_SHORT).show()
                return
            }
        }
        imagePickerLauncher.launch(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMediaItem(uri: Uri, type: String) {
        val mediaType = when (type.lowercase()) {
            "image" -> MediaType.IMAGE
            else -> throw IllegalArgumentException("Unknown media type: $type")
        }

        val mediaItem = MediaItem(mediaType, uri)  // Convert Uri to String
        mediaList.add(mediaItem)
      //  mediaAdapter.notifyItemInserted(mediaList.size - 1)

//        binding.viewImage.visibility = if (mediaList.any { it.type == MediaType.IMAGE }) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

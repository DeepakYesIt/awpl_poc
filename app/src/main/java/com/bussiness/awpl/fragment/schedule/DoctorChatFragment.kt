package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.ChatItem
import com.bussiness.awpl.ChatMessage
import com.bussiness.awpl.adapter.ChatAdapter

import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentDoctorChatBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.repository.ChatRepository
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.viewmodel.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class DoctorChatFragment : Fragment() {

    private var _binding: FragmentDoctorChatBinding? = null
    private val binding get() = _binding!!
    private var mediaUploadDialog: MediaUtils? = null
    private var currentType: String = ""
    private val mediaList = mutableListOf<MediaItem>()
    private lateinit var mediaAdapter: MediaAdapter
    lateinit var chatAdapter: ChatAdapter
    var currentUserId ="6"
    var receiverId ="11"
    var chatId ="13_11_6"
    private lateinit var chatViewModel: ChatViewModel
    private  var messageList = mutableListOf<ChatMessage>()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    // Show in dialog first
                    mediaUploadDialog?.handleSelectedFile(it)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorChatBinding.inflate(inflater, container, false)
        chatAdapter = ChatAdapter(mutableListOf(),"6")
        chatViewModel =ViewModelProvider(this)[ChatViewModel::class.java]
        binding.chatRecyclerView.layoutManager =LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.chatRecyclerView.adapter =chatAdapter

        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->

            chatAdapter.submitList(groupMessagesByDate(messages))
            messageList =messages.toMutableList()
             binding.chatRecyclerView.scrollToPosition(messages.size - 1)
        }
        return binding.root
    }

    fun groupMessagesByDate(messages: List<ChatMessage>): List<ChatItem> {
        val grouped = messages.sortedBy { it.timestamp }
            .groupBy {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
            }

        val chatItems = mutableListOf<ChatItem>()
        for ((date, items) in grouped) {
            chatItems.add(ChatItem.DateHeader(date))
            chatItems.addAll(items.map { ChatItem.MessageItem(it) })
        }
        Log.d("Testing_chat_size",chatItems.size.toString())
        chatItems.forEach {

            when (val item = it) {
                is ChatItem.DateHeader -> {
                    Log.d("testing_date", item.date)
                }

                is ChatItem.MessageItem -> {
                    Log.d("testing_date", item.message.message.toString())
                }

            }
        }

        return chatItems
    }

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
            }
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

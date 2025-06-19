package com.bussiness.awpl.activities

import android.graphics.Color
import android.Manifest
import android.provider.Settings
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.sax.RootElement
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView

import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bussiness.awpl.BuildConfig
import com.bussiness.awpl.DialogStartAppointment
import com.bussiness.awpl.DownloadDialog
import com.bussiness.awpl.DownloadRescheduleAppointment
import com.bussiness.awpl.R
import com.bussiness.awpl.base.CommonUtils
import com.bussiness.awpl.databinding.ActivityHomeBinding
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.fragment.home.HomeViewModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.DownloadWorker

import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private var fileUrl :String =""
    private var date :String =""
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHostFragment: NavHostFragment
    private var sessionManager: SessionManager? = null
    private lateinit var navController: NavController
    private var isArrowUp = false
    private var notificationPermissionThere = false
    private lateinit var img:de.hdodenhof.circleimageview.CircleImageView
    lateinit var notification :SwitchCompat

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.WHITE

        BuildConfig.BASE_URL
        sessionManager = SessionManager(this)
        sessionManager?.applySavedLanguage()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController

        binding.chatFab.visibility =View.GONE
        binding.ivBell.setOnClickListener { navigate(R.id.notificationFragment) }

        val fragmentToLoad = intent.getStringExtra("LOAD_HOME_FRAGMENT")
        if (fragmentToLoad == "HomeFragment") {
            navController.navigate(R.id.homeFragment)
        }

        val fragmentToLoadNotification = intent.getStringExtra("LOAD_FRAGMENT")
        if (fragmentToLoadNotification == "notification") {
            navController.navigate(R.id.notificationFragment)
        }

        setupBottomNav()
        setUpDrawer()
        askNotificationPermissionForCall()
        updateBottomNavSelection("home")

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.apply {
                        toolbar.visibility = View.VISIBLE
                        customBottomNav.visibility = View.VISIBLE
                        toolbarTitle.text = ""
                        ivBell.visibility = View.VISIBLE
                        imgBackProfile.visibility =View.GONE
                        profileIcon.visibility =View.VISIBLE
                        Glide.with(this@HomeActivity).load(SessionManager(this@HomeActivity).getUserImage()).into(profileIcon)

                        profileIcon.setOnClickListener {
                            updateDrawerContent()
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                        chatFab.visibility = View.GONE
                        toolbarTitle.visibility = View.GONE
                        icon.visibility = View.VISIBLE
                    }
                    updateBottomNavSelection("home")
                }
                R.id.notificationFragment -> {
                    setToolbar("Notifications", showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.privacyPolicyFragment -> {
                    setToolbar("Privacy Policy", showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.termsAndConditionFragment -> {
                    setToolbar("Terms & Conditions", showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.appointmentPolicyFragment -> {
                    setToolbar("Appointment Policy", showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.refundPolicyFragment -> {
                    setToolbar("Refund Policy", showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                }
                R.id.profileFragment ->{ setToolbar("My Profile",showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.videoGalleryFragment ->{ setToolbar("Video Gallery", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.FAQFragment3 ->{ setToolbar("FAQ", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.scheduleFragment ->{ setToolbar("My Appointments", fab = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                    updateBottomNavSelection("schedule")
                }
                R.id.resourceFragment ->{ setToolbar("Resources", fab = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                    updateBottomNavSelection("resource")
                }
                R.id.yourDoctorFragment ->{ setToolbar("Your Doctors", fab = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                    updateBottomNavSelection("doctor")
                }
                R.id.appointmentBooking ->{ setToolbar("Book Appointment", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.summaryScreen ->{ setToolbar("Summary", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.paymentScreen -> {setToolbar("Payment Method", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                }
                R.id.homeScheduleCallFragment -> {
                    setToolbar(
                        "Scheduled Call\nConsultations",
                        showBottomNav = false,
                        showBell = false
                    )
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.GONE
                }
                R.id.doctorChatFragment ->{ setToolbar("My Appointments", showBottomNav = false)
                    binding.profileIcon.visibility =View.GONE
                }
                R.id.prescription_frgament ->{ setToolbar("My Prescriptions", fab = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.reschedule_call ->{ setToolbar("Reschedule Appointment", fab = false, showBottomNav = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.incompleteAppointmentFragment ->{ setToolbar("Incomplete Appointment",showBottomNav = false, fab = false, showBell = false)
                    binding.profileIcon.visibility =View.GONE
                    binding.imgBackProfile.visibility =View.VISIBLE
                }
                R.id.symptomUpload,
                R.id.onlineConsultationFragment,
                R.id.doctorConsultationFragment,
                R.id.basicInfoScreen2,
                R.id.scheduledCallConsultation2 -> {
                    binding.toolbar.visibility = View.GONE
                       binding.customBottomNav.visibility = View.GONE
                }
                R.id.videoCallFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.customBottomNav.visibility = View.GONE
                    binding.chatFab.visibility = View.GONE
                }
                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.customBottomNav.visibility = View.VISIBLE
                    binding.ivBell.visibility = View.VISIBLE
                }
            }
        }

        intent?.let {
            if(it.hasExtra("fileUrl") && it.hasExtra("date")){
                fileUrl = intent.getStringExtra("fileUrl").toString()
                date = intent.getStringExtra("date").toString()
                if(fileUrl != null && date != null &&fileUrl.isNotEmpty() && date.isNotEmpty()) {
                    downloadReportDialog()
                }
            }
            else if(it.hasExtra("doctor_name")&& it.hasExtra("date")) {
                Log.d("TESTING_NAME","inside activity "+intent.getStringExtra("doctor_name").toString())
                val dialog = DialogStartAppointment(this,intent.getStringExtra("doctor_name").toString() ,
                    intent.getStringExtra("date").toString())
                dialog.show()
            }

            else if (it.hasExtra("doctor_name") &&
                it.hasExtra("when_time") &&
                it.hasExtra("new_doctor") &&
                it.hasExtra("new_date") &&
                it.hasExtra("new_time")) {

                val doctorName = it.getStringExtra("doctor_name")
                val whenTime = it.getStringExtra("when_time")
                val newDoctor = it.getStringExtra("new_doctor")
                val newDate = it.getStringExtra("new_date")
                val newTime = it.getStringExtra("new_time")

                // Use these values as needed
                Log.d("IntentData", "Old: $doctorName @ $whenTime | New: $newDoctor on $newDate at $newTime")

                // Optional: Show dialog
                if (!doctorName.isNullOrEmpty()) {
                    if (!whenTime.isNullOrEmpty()) {
                        if (!newDoctor.isNullOrEmpty()) {
                            if (!newDate.isNullOrEmpty()) {
                                if (!newTime.isNullOrEmpty()) {
                                    DownloadRescheduleAppointment(this, doctorName, whenTime, newDoctor, newDate, newTime).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadReportDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogReportDownloadBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        // Set width with margin
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)

        // Apply width to the dialog window
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.description4.text =date

        binding.btnOkay.setOnClickListener {
            dialog.dismiss()
            var intent = Intent(this,HomeActivity::class.java)
            DownloadWorker().downloadPdfWithNotification(this,fileUrl,"Presription/${UUID.randomUUID()}")
            Toast.makeText(this,"Download Started!",Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateDrawerContent() {
        val navigationView: NavigationView = binding.root.findViewById(R.id.navigation_side_nav_bar)
        val img = navigationView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileIcon)
        val nameTv = navigationView.findViewById<TextView>(R.id.tv_user_name)
        val notification = navigationView.findViewById<SwitchCompat>(R.id.switchNotification)

        nameTv.text = SessionManager(this).getUserName()?:""
        Glide.with(this)
            .load(SessionManager(this).getUserImage())
            .placeholder(R.drawable.ic_profile_new_opt)
            .into(img)
        notification.isChecked = SessionManager(this).isNotificationPermissionGranted(this)
    }

    private fun setupBottomNav() {
        binding.homeFragment.setOnClickListener {
            navController.navigate(R.id.homeFragment)
            updateBottomNavSelection("home")
        }

        binding.scheduleFragment.setOnClickListener {
            navController.navigate(R.id.scheduleFragment)
            updateBottomNavSelection("schedule")
        }

        binding.yourDoctorFragment.setOnClickListener {
            val destinationId = R.id.yourDoctorFragment
            if(destinationId != navController.currentDestination?.id) {
                navController.navigate(R.id.yourDoctorFragment)
                updateBottomNavSelection("doctor")
            }
        }

        binding.resourceFragment.setOnClickListener {
            navController.navigate(R.id.resourceFragment)
            updateBottomNavSelection("resource")
        }
    }

    private fun updateBottomNavSelection(selected: String) {
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, if (selected == "home") R.color.blueColor else R.color.greyColor))
        binding.textHome.setTextColor(ContextCompat.getColor(this, if (selected == "home") R.color.blueColor else R.color.greyColor))
        binding.indicatorHome.visibility = if (selected == "home") View.VISIBLE else View.GONE

        binding.iconSchedule.setColorFilter(ContextCompat.getColor(this, if (selected == "schedule") R.color.blueColor else R.color.greyColor))
        binding.textSchedule.setTextColor(ContextCompat.getColor(this, if (selected == "schedule") R.color.blueColor else R.color.greyColor))
        binding.indicatorSchedule.visibility = if (selected == "schedule") View.VISIBLE else View.GONE

        binding.iconDoctor.setColorFilter(ContextCompat.getColor(this, if (selected == "doctor") R.color.blueColor else R.color.greyColor))
        binding.textDoctor.setTextColor(ContextCompat.getColor(this, if (selected == "doctor") R.color.blueColor else R.color.greyColor))
        binding.indicatorDoctor.visibility = if (selected == "doctor") View.VISIBLE else View.GONE

        binding.iconResource.setColorFilter(ContextCompat.getColor(this, if (selected == "resource") R.color.blueColor else R.color.greyColor))
        binding.textResource.setTextColor(ContextCompat.getColor(this, if (selected == "resource") R.color.blueColor else R.color.greyColor))
        binding.indicatorResource.visibility = if (selected == "resource") View.VISIBLE else View.GONE
    }

    private fun setToolbar(
        title: String,
        showBottomNav: Boolean = true,
        showBell: Boolean = true,
        fab: Boolean = false
    ) {
        binding.apply {
            toolbar.visibility = View.VISIBLE
            customBottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE
            toolbarTitle.text = title
            ivBell.visibility = if (showBell) View.VISIBLE else View.GONE
            imgBackProfile.setImageResource(R.drawable.back_icon)

            imgBackProfile.setOnClickListener {
                val goHomeTitles = listOf("Resources", "Your Doctors", "My Appointments")
                if (title in goHomeTitles) {
                    findNavController(R.id.nav_host_fragment_home).navigate(R.id.homeFragment)
                } else {
                    findNavController(R.id.nav_host_fragment_home).navigateUp()
                }
            }
            chatFab.visibility = if (fab) View.VISIBLE else View.GONE
            toolbarTitle.visibility = View.VISIBLE
            icon.visibility = View.GONE
        }
    }

    private fun navigate(destinationId: Int) {
        findNavController(R.id.nav_host_fragment_home).navigate(destinationId)
    }

    private fun setUpDrawer() {
        val navigationView: NavigationView = binding.root.findViewById(R.id.navigation_side_nav_bar)
        val notification = navigationView.findViewById<SwitchCompat>(R.id.switchNotification)
        val privacyPolicy = navigationView.findViewById<LinearLayout>(R.id.llPrivacyPolicy)
        val termsOfServices = navigationView.findViewById<LinearLayout>(R.id.llTermsOfServices)
        val logout = navigationView.findViewById<LinearLayout>(R.id.llLogout)
        val selectedLanguages = navigationView.findViewById<LinearLayout>(R.id.llSelectedLanguages)
        val dismiss = navigationView.findViewById<ImageButton>(R.id.dismiss)
        val englishLang = navigationView.findViewById<CardView>(R.id.englishLang)
        val hindiLang = navigationView.findViewById<CardView>(R.id.hindiLang)
        val imageView = navigationView.findViewById<ImageView>(R.id.iconLanguage)
        val viewProfile = navigationView.findViewById<TextView>(R.id.viewMyProfile)
        val perception = navigationView.findViewById<LinearLayout>(R.id.llpreceptions)
        val doctor = navigationView.findViewById<LinearLayout>(R.id.lldoctor)
        val resources = navigationView.findViewById<LinearLayout>(R.id.llresources)
        val forMe = navigationView.findViewById<LinearLayout>(R.id.for_me_ll)
        val forOther = navigationView.findViewById<LinearLayout>(R.id.ll_for_other)
        val scheduleLayout = navigationView.findViewById<LinearLayout>(R.id.llschedule)
        val userName = navigationView.findViewById<TextView>(R.id.tv_user_name)
        val refundPolicy = navigationView.findViewById<LinearLayout>(R.id.ll_refund)
        img = navigationView.findViewById(R.id.profileIcon)
        val incompleteAppointment = navigationView.findViewById<LinearLayout>(R.id.llIncomplete)
        userName.text = SessionManager(this).getUserName() ?: ""
        val imgIcon = navigationView.findViewById<ImageView>(R.id.arr_sch)
        val scheduleMain = navigationView.findViewById<LinearLayout>(R.id.ll_schedule_main)

        Log.d("TESTING_IMAGE",SessionManager(this).getUserImage())

        if(SessionManager(this).isNotificationPermissionGranted(this)){
          Log.d("Testing_notification","YES NOTIFICATION_ENABLE")
           notification.isChecked = true
        }else{
            Log.d("Testing_notification","NO NOTIFICATION_ENABLE")
            notification.isChecked = false
        }
        Glide.with(this).load(SessionManager(this).getUserImage()).placeholder(R.drawable.ic_profile_new_opt).into(img)

        refundPolicy.setOnClickListener {
            navController.navigate(R.id.refundPolicyFragment)
            closeDrawer()
        }
        scheduleLayout.setOnClickListener {
            if (scheduleMain.isVisible) {
                scheduleMain.visibility = View.GONE
                imgIcon.setImageResource(R.drawable.down_ic)
            } else {
                scheduleMain.visibility = View.VISIBLE
                imgIcon.setImageResource(R.drawable.up_ic)
            }
        }

        forMe.setOnClickListener {
            val bundle =Bundle().apply {
                putString("type",AppConstant.FOR_ME)
            }
            navController.navigate(R.id.diseasesBottomFragment,bundle)
            closeDrawer()
        }

        forOther.setOnClickListener {
            val bundle  = Bundle().apply {
                putString("type",AppConstant.OTHERS)
            }
            navController.navigate(R.id.diseasesBottomFragment,bundle)
            closeDrawer()
        }

        doctor.setOnClickListener {
            navController.navigate(R.id.yourDoctorFragment)
            updateBottomNavSelection("doctor")
            closeDrawer()
        }

        resources.setOnClickListener {

            navController.navigate(R.id.resourceFragment)
            updateBottomNavSelection("resource")
            closeDrawer()
        }

        incompleteAppointment.setOnClickListener {
            navController.navigate(R.id.incompleteAppointmentFragment)
            closeDrawer()
        }

        perception.setOnClickListener {
            closeDrawer()
            navController.navigate(R.id.prescription_frgament)
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        notification.thumbTintList =
            ColorStateList.valueOf(android.graphics.Color.WHITE)

        notification.trackTintList =
            ContextCompat.getColorStateList(this, R.color.switch_track_color)

        notification.setOnCheckedChangeListener { _, isChecked ->
            // Notification logic
            if (isChecked) {
                // Enable notifications
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            }
            else {
                // Disable notifications
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }

        privacyPolicy.setOnClickListener {
            navigate(R.id.privacyPolicyFragment)
            closeDrawer()
        }

        termsOfServices.setOnClickListener {
            navigate(R.id.termsAndConditionFragment)
            closeDrawer()
        }

        logout.setOnClickListener {
            closeDrawer()
            CommonUtils.logoutDialog(this)
        }

        selectedLanguages.setOnClickListener {
            isArrowUp = !isArrowUp
            imageView.setImageResource(if (isArrowUp) R.drawable.up_ic else R.drawable.down_ic)
            englishLang.visibility = if (isArrowUp) View.VISIBLE else View.GONE
            hindiLang.visibility = if (isArrowUp) View.VISIBLE else View.GONE
        }

        navigationView.findViewById<LinearLayout>(R.id.llEnglishLang).setOnClickListener {
            Toast.makeText(this, "English", Toast.LENGTH_SHORT).show()
            sessionManager?.setLanguage("en")
            sessionManager?.changeLanguage(this, "en")
            recreate()
        }

        navigationView.findViewById<LinearLayout>(R.id.llHindiLang).setOnClickListener {
            Toast.makeText(this, "Hindi", Toast.LENGTH_SHORT).show()
            sessionManager?.setLanguage("hi")
            sessionManager?.changeLanguage(this, "hi")
            recreate()
        }

        dismiss.setOnClickListener {
            closeDrawer()
        }

        viewProfile.setOnClickListener {
            navigate(R.id.profileFragment)
            closeDrawer()
        }
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission is granted by default below Android 13
            true
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                notificationPermissionThere = true
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    showDeniedDialog()
                }
            }
        }

    private fun askNotificationPermissionForCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // Already granted
                    notificationPermissionThere = true
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showCallRationaleDialog()
                }

                else -> {
                    // First-time or normal request
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            notificationPermissionThere = true;
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showCallRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Allow Notifications for Video Calls")
            .setMessage("To receive incoming video calls, allow notification access so we can alert you when Doctor calls.")
            .setPositiveButton("Allow") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications Disabled")
            .setMessage("Without notification access, you won't receive video calls. You can enable it from app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

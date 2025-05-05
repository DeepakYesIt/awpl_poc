package com.bussiness.awpl.activities

import android.Manifest
import android.provider.Settings
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
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
import com.bussiness.awpl.R
import com.bussiness.awpl.base.CommonUtils
import com.bussiness.awpl.databinding.ActivityHomeBinding
import com.bussiness.awpl.fragment.home.HomeViewModel
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHostFragment: NavHostFragment
    private var sessionManager: SessionManager? = null
    private lateinit var navController: NavController
    private var isArrowUp = false
    private var notificationPermissionThere = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BuildConfig.BASE_URL

        sessionManager = SessionManager(this)
        sessionManager?.applySavedLanguage()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController

//        binding.customBottomNav.setupWithNavController(navController)

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
                        imgBackProfile.setImageResource(R.drawable.profile_icons)
                        imgBackProfile.setOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                        chatFab.visibility = View.VISIBLE
                    }
                    updateBottomNavSelection("home")
                }

                R.id.notificationFragment -> setToolbar(
                    "Notifications",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.privacyPolicyFragment -> setToolbar(
                    "Privacy Policy",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.termsAndConditionFragment -> setToolbar(
                    "Terms & Conditions",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.appointmentPolicyFragment -> setToolbar(
                    "Appointment Policy",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.refundPolicyFragment -> setToolbar(
                    "Refund Policy",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.profileFragment -> setToolbar("My Profile")
                R.id.videoGalleryFragment -> setToolbar("Video Gallery", showBottomNav = false)
                R.id.FAQFragment3 -> setToolbar("FAQ", showBottomNav = false)
                R.id.scheduleFragment -> setToolbar("My Appointments", fab = true)
                R.id.resourceFragment -> setToolbar("Resources", fab = true)
                R.id.yourDoctorFragment -> setToolbar("Your Doctors", fab = true)
                R.id.appointmentBooking -> setToolbar("Book Appointment", showBottomNav = false)
                R.id.summaryScreen -> setToolbar("Summary", showBottomNav = false)
                R.id.paymentScreen -> setToolbar("Payment Method", showBottomNav = false)
                R.id.homeScheduleCallFragment -> setToolbar(
                    "Scheduled Call\nConsultations",
                    showBottomNav = false,
                    showBell = false
                )

                R.id.doctorChatFragment -> setToolbar("My Appointments", showBottomNav = false)
                R.id.prescription_frgament -> setToolbar("My Prescriptions", fab = false)
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
            navController.navigate(R.id.yourDoctorFragment)
            updateBottomNavSelection("doctor")
        }

        binding.resourceFragment.setOnClickListener {
            navController.navigate(R.id.resourceFragment)
            updateBottomNavSelection("resource")
        }
    }

    private fun updateBottomNavSelection(selected: String) {
        binding.iconHome.setColorFilter(
            ContextCompat.getColor(
                this,
                if (selected == "home") R.color.blueColor else R.color.greyColor
            )
        )
        binding.textHome.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected == "home") R.color.blueColor else R.color.greyColor
            )
        )
        binding.indicatorHome.visibility = if (selected == "home") View.VISIBLE else View.GONE

        // Schedule
        binding.iconSchedule.setColorFilter(
            ContextCompat.getColor(
                this,
                if (selected == "schedule") R.color.blueColor else R.color.greyColor
            )
        )
        binding.textSchedule.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected == "schedule") R.color.blueColor else R.color.greyColor
            )
        )
        binding.indicatorSchedule.visibility =
            if (selected == "schedule") View.VISIBLE else View.GONE

        // Doctor
        binding.iconDoctor.setColorFilter(
            ContextCompat.getColor(
                this,
                if (selected == "doctor") R.color.blueColor else R.color.greyColor
            )
        )
        binding.textDoctor.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected == "doctor") R.color.blueColor else R.color.greyColor
            )
        )
        binding.indicatorDoctor.visibility = if (selected == "doctor") View.VISIBLE else View.GONE

        // Resource
        binding.iconResource.setColorFilter(
            ContextCompat.getColor(
                this,
                if (selected == "resource") R.color.blueColor else R.color.greyColor
            )
        )
        binding.textResource.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected == "resource") R.color.blueColor else R.color.greyColor
            )
        )
        binding.indicatorResource.visibility =
            if (selected == "resource") View.VISIBLE else View.GONE
    }

    private fun setToolbar(
        title: String, showBottomNav: Boolean = true, showBell: Boolean = true, fab: Boolean = false) {
        binding.apply {
            toolbar.visibility = View.VISIBLE
            customBottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE
            toolbarTitle.text = title
            ivBell.visibility = if (showBell) View.VISIBLE else View.GONE
            imgBackProfile.setImageResource(R.drawable.back_icon)

            imgBackProfile.setOnClickListener {
                findNavController(R.id.nav_host_fragment_home).navigateUp()
            }
            chatFab.visibility = if (fab) View.VISIBLE else View.GONE
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
        var perception = navigationView.findViewById<LinearLayout>(R.id.llpreceptions)
        var doctor = navigationView.findViewById<LinearLayout>(R.id.lldoctor)
        var resources = navigationView.findViewById<LinearLayout>(R.id.llresources)
        var forMe = navigationView.findViewById<LinearLayout>(R.id.for_me_ll)
        var forOther = navigationView.findViewById<LinearLayout>(R.id.ll_for_other)
        var scheduleLayout = navigationView.findViewById<LinearLayout>(R.id.llschedule)
        var userName = navigationView.findViewById<TextView>(R.id.tv_user_name)
        var img = navigationView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileIcon)
        userName.setText(SessionManager(this).getUserName() ?: "")
        var imgIcon = navigationView.findViewById<ImageView>(R.id.arr_sch)
        var scheduleMain = navigationView.findViewById<LinearLayout>(R.id.ll_schedule_main)

        Log.d("TESTING_IMAGE",SessionManager(this).getUserImage())

        if(SessionManager(this).isNotificationPermissionGranted(this)){
          Log.d("Testing_notification","YES NOTIFICATION_ENABLE")
           notification.isChecked = true
        }else{
            Log.d("Testing_notification","NO NOTIFICATION_ENABLE")
            notification.isChecked = false
        }
        Glide.with(this).load(SessionManager(this).getUserImage()).placeholder(R.drawable.ic_profile_new_opt).into(img)

        scheduleLayout.setOnClickListener {
            if (scheduleMain.isVisible) {
                scheduleMain.visibility = View.GONE
                imgIcon.setImageResource(R.drawable.down_ic)
            } else {
                scheduleMain.visibility = View.VISIBLE
                imgIcon.setImageResource(R.drawable.up_ic)
            }
        }

        forOther.setOnClickListener {
            navController.navigate(R.id.diseasesBottomFragment)
            closeDrawer()
        }

        forMe.setOnClickListener {
            navController.navigate(R.id.diseasesBottomFragment)
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

        perception.setOnClickListener {
            closeDrawer()
            navController.navigate(R.id.prescription_frgament)

        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        notification.thumbTintList = ColorStateList.valueOf(android.graphics.Color.WHITE)

        notification.trackTintList =
            ContextCompat.getColorStateList(this, R.color.switch_track_color)

        notification.setOnCheckedChangeListener { _, isChecked ->
            // Notification logic
            if (isChecked) {
                // Enable notifications
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
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

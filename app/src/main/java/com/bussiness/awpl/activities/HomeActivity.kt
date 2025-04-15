package com.bussiness.awpl.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.base.CommonUtils
import com.bussiness.awpl.databinding.ActivityHomeBinding
import com.bussiness.awpl.utils.SessionManager
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHostFragment: NavHostFragment
    private var sessionManager: SessionManager? = null
    private var isArrowUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        sessionManager?.applySavedLanguage()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        val navController = navHostFragment.navController

        binding.homeBottomNav.setupWithNavController(navController)

        binding.ivBell.setOnClickListener { navigate(R.id.notificationFragment) }

        val fragmentToLoad = intent.getStringExtra("LOAD_HOME_FRAGMENT")
        if (fragmentToLoad == "HomeFragment") {
            navController.navigate(R.id.homeFragment)
        }

        val fragmentToLoadNotification = intent.getStringExtra("LOAD_FRAGMENT")
        if (fragmentToLoadNotification == "notification") {
            navController.navigate(R.id.notificationFragment)
        }

        setUpDrawer()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.apply {
                        toolbar.visibility = View.VISIBLE
                        homeBottomNav.visibility = View.VISIBLE
                        toolbarTitle.text = ""
                        ivBell.visibility = View.VISIBLE
                        imgBackProfile.setImageResource(R.drawable.profile_icons)
                        imgBackProfile.setOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                        chatFab.visibility = View.VISIBLE
                    }
                }

                R.id.notificationFragment -> setToolbar("Notifications", showBottomNav = false, showBell = false)
                R.id.privacyPolicyFragment -> setToolbar("Privacy Policy", showBottomNav = false, showBell = false)
                R.id.termsAndConditionFragment -> setToolbar("Terms & Conditions", showBottomNav = false, showBell = false)
                R.id.appointmentPolicyFragment -> setToolbar("Appointment Policy", showBottomNav = false, showBell = false)
                R.id.refundPolicyFragment -> setToolbar("Refund Policy", showBottomNav = false, showBell = false)
                R.id.profileFragment -> setToolbar("My Profile")
                R.id.videoGalleryFragment -> setToolbar("Video Gallery", showBottomNav = false)
                R.id.FAQFragment3 -> setToolbar("FAQ", showBottomNav = false)
                R.id.scheduleFragment -> setToolbar("My Appointments",fab = true)
                R.id.resourceFragment -> setToolbar("Resources",fab = true)
                R.id.yourDoctorFragment -> setToolbar("Your Doctors",fab = true)
                R.id.appointmentBooking -> setToolbar("Book Appointment", showBottomNav = false)
                R.id.summaryScreen -> setToolbar("Summary", showBottomNav = false)
                R.id.paymentScreen -> setToolbar("Payment Method", showBottomNav = false)
                R.id.homeScheduleCallFragment -> setToolbar("Scheduled Call\nConsultations", showBottomNav = false)
                R.id.doctorChatFragment -> setToolbar("My Appointments", showBottomNav = false)
                R.id.symptomUpload,
                R.id.onlineConsultationFragment,
                R.id.doctorConsultationFragment,
                R.id.basicInfoScreen2,
                R.id.scheduledCallConsultation2 -> {
                    binding.toolbar.visibility = View.GONE
                    binding.homeBottomNav.visibility = View.GONE
                }

                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.homeBottomNav.visibility = View.VISIBLE
                    binding.ivBell.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setToolbar(title: String, showBottomNav: Boolean = true, showBell: Boolean = true,fab : Boolean = false) {
        binding.apply {
            toolbar.visibility = View.VISIBLE
            homeBottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE
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
}

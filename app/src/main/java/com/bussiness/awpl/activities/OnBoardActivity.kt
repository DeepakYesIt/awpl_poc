package com.bussiness.awpl.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ActivityOnBoardBinding
import com.bussiness.awpl.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Apply saved language when the activity is created
        val sessionManager = SessionManager(this)
        sessionManager.applySavedLanguage()

        val loadFragment = intent.getStringExtra("LOAD_FRAGMENT")
        val type = intent.getStringExtra("TYPE")
        val bundle = Bundle().apply {
            putString("TYPE", type)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (navController.currentDestination?.id != R.id.welcomeScreen2) {
            navController.navigate(R.id.welcomeScreen2)
        }

        when (loadFragment) {
            "SymptomUploadFragment" -> navController.navigate(R.id.symptomUpload)
            "schedule" -> navController.navigate(R.id.appointmentBooking)
            "login" -> navController.navigate(R.id.welcomeScreen1)
            "basicInfo" -> navController.navigate(R.id.basicInfoScreen, bundle)
            "online_consultation" -> navController.navigate(R.id.symptomUpload)
        }
    }
}

package com.example.alzcare.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        subscribeToLiveData()
        Handler(Looper.getMainLooper()).postDelayed({
//          startActivity()
            viewModel.redirect()
        }, 2000)
    }

    private fun subscribeToLiveData() {
        viewModel.events.observe(this) {
            when (it) {
                SplashViewEvent.NavigateToLogin -> {
                    startLoginActivity()
                }

                SplashViewEvent.NavigateToHome -> {
                    startHomeActivity()
                }
            }
        }
    }

    private fun startHomeActivity() {
        val intent:Intent
        when (SessionProvider.user?.role) {
            "Doctor" -> {
                intent = Intent(this, DoctorHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            "Patient" -> {
                intent = Intent(this, PatientHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
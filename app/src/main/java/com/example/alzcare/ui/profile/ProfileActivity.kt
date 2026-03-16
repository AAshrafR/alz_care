package com.example.alzcare.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivityProfileBinding
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        viewBinding.vm=viewModel
        viewBinding.lifecycleOwner=this
        viewModel.events.observe(this, ::handleEvents)
    }

    private fun handleEvents(profileViewEvent: ProfileViewEvent?) {

        when (profileViewEvent) {
            ProfileViewEvent.NavigateToDoctorHome -> navigateToDoctorHome()
            ProfileViewEvent.NavigateToPatientHome -> navigateToPatientHome()
            null -> TODO()
        }

    }

    private fun navigateToDoctorHome() {
        val intent = Intent(this, DoctorHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToPatientHome() {
        val intent = Intent(this, PatientHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

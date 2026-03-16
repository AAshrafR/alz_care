package com.example.alzcare.ui.about_us

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivityAboutUsBinding
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity

class AboutUsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAboutUsBinding
    private val viewModel: AboutUsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this,R.layout.activity_about_us)
        viewBinding.vm=viewModel
        viewBinding.lifecycleOwner=this
        viewModel.events.observe(this, ::handleEvents)

        viewBinding.aboutUsTv.text = Content.aboutUsContent
    }
    private fun handleEvents(aboutUsViewEvent: AboutUsViewEvent?) {

        when (aboutUsViewEvent) {
            AboutUsViewEvent.NavigateToDoctorHome -> navigateToDoctorHome()
            AboutUsViewEvent.NavigateToPatientHome -> navigateToPatientHome()
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
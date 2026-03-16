package com.example.alzcare.ui.doctor.my_patients.patient_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivityMyPatientProfileBinding
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.model.User
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity

class MyPatientProfileActivity : AppCompatActivity() {

    private val viewModel: DoctorPatientsViewModel by viewModels()
    private lateinit var viewBinding: ActivityMyPatientProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initParams()
    }

    private fun initParams() {
        val patient = intent.getParcelableExtra(Constants.EXTRA_DOCTOR_PATIENTS) as? Appointment
        val patientId = patient?.patientId
        UsersDao.getUser(patientId) { documentSnapshot ->
            val user = documentSnapshot.result.toObject(User::class.java)
            val patientName = user?.userName
            val patientNumber = user?.phoneNumber
            val patientEmail = user?.email
            val name= viewBinding.patientNameTextView
            val phNumber=viewBinding.patientNumberTextView
            val email= viewBinding.patientEmailTextView
            name.text =patientName
            phNumber.text =patientNumber
            email.text =patientEmail

            viewBinding.imgCall.setOnClickListener {
                // Call the patient's number
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", patientNumber, null))
                startActivity(intent)

            }

            viewBinding.imgSendEmail.setOnClickListener {
                // Send email to the patient's email address
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto:$patientEmail")
                startActivity(emailIntent)
            }
        }
    }

    private fun initViews() {

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_patient_profile)
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel

        viewBinding.backButton.setOnClickListener {
            val intent = Intent(this, DoctorHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.example.alzcare.ui.patient.my_doctors.doctor_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivityMyDoctorProfileBinding
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.model.User
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.patient.home.PatientHomeActivity

class MyDoctorProfileActivity : AppCompatActivity() {

    private val viewModel: PatientDoctorsViewModel by viewModels()
    private lateinit var viewBinding: ActivityMyDoctorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initParams()
    }

    private fun initParams() {
        val doctor = intent.getParcelableExtra(Constants.EXTRA_PATIENT_DOCTORS) as? Appointment
        val docId = doctor?.doctorId
        UsersDao.getUser(docId) { documentSnapshot ->
                val user = documentSnapshot.result.toObject(User::class.java)
                val doctorName = user?.userName
                val doctorNumber = user?.phoneNumber
                val doctorEmail = user?.email
            var name= viewBinding.doctorNameTextView
            var phNumber=viewBinding.doctorNumberTextView
            var email= viewBinding.doctorEmailTextView
            name.text =doctorName
            phNumber.text =doctorNumber
            email.text =doctorEmail

            viewBinding.imgCall.setOnClickListener {
                // Call the patient's number
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", doctorNumber, null))
                startActivity(intent)

            }

            viewBinding.imgSendEmail.setOnClickListener {
                // Send email to the patient's email address
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto:$doctorEmail")
                startActivity(emailIntent)
            }


        }
    }

    private fun initViews() {

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_doctor_profile)
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel

        viewBinding.backButton.setOnClickListener {
            val intent = Intent(this, PatientHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
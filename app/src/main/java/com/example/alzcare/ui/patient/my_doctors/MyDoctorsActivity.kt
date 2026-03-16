package com.example.alzcare.ui.patient.my_doctors

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityMyDoctorsBinding
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.patient.my_doctors.doctor_profile.MyDoctorProfileActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.showMessage

class MyDoctorsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMyDoctorsBinding
    private val viewModel: MyDoctorsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_doctors)

        initViews()
        subscribeToLiveData()
    }

    private fun initViews() {

        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this

        val myRecyclerView = viewBinding.myDoctorsRecyclerView
        val patientAppointmentService = AppointmentDao
        val patientId = SessionProvider.user?.id ?: "" // Ensure doctorId is not null

        // Fetch appointments for the doctor
        patientAppointmentService.getMyDoctors(patientId) { appointments ->
            Log.d(Appointment.CollectionName, appointments.toString())
            val adapter = MyDoctorsRecyclerAdapter(this, appointments)
            myRecyclerView.adapter = adapter

            adapter.onItemClickListener = MyDoctorsRecyclerAdapter.OnItemClickListener { position, doctor ->
                navigateToDoctorProfile(doctor)
            }
        }
    }

    private fun navigateToDoctorProfile(doctor: Appointment) {
        val intent = Intent(this, MyDoctorProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_PATIENT_DOCTORS, doctor)
        startActivity(intent)
        finish()
    }

    private fun subscribeToLiveData() {

        viewModel.messageLiveData.observe(this) {
            showMessage(
                it.message ?: "",
                posActionName = it.posActionName,
                posAction = it.onPosActionClick,
                negActionName = it.negActionName,
                negAction = it.onNegActionClick,
                isCancelable = it.isCancelable
            )
        }
        viewModel.events.observe(this, ::handleEvents)
    }

    private fun handleEvents(myDoctorsViewEvent: MyDoctorsViewEvent?) {

        when (myDoctorsViewEvent) {
            MyDoctorsViewEvent.NavigateToHome -> {
                navigateToHome()
            }
            null -> TODO()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, PatientHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

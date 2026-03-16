package com.example.alzcare.ui.doctor.my_patients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityMyPatientsBinding
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.doctor.my_patients.patient_profile.MyPatientProfileActivity
import com.example.alzcare.ui.patient.my_doctors.doctor_profile.MyDoctorProfileActivity
import com.example.alzcare.ui.patient.my_doctors.MyDoctorsRecyclerAdapter
import com.example.alzcare.ui.showMessage

class MyPatientsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMyPatientsBinding
    private val viewModel: MyPatientsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_patients)

        initViews()
        subscribeToLiveData()
    }

    private fun initViews() {

        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this

        val myRecyclerView = viewBinding.myPatientsRecyclerView
        val doctorAppointmentService = AppointmentDao
        val doctorId = SessionProvider.user?.id ?: "" // Ensure doctorId is not null

        // Fetch appointments for the doctor
        doctorAppointmentService.getMyPatients(doctorId) { appointments ->
            Log.d(Appointment.CollectionName, appointments.toString())
            val adapter = MyPatientsRecyclerAdapter(this, appointments)
            myRecyclerView.adapter = adapter
            adapter.onItemClickListener = MyPatientsRecyclerAdapter.OnItemClickListener { position, patient ->
                navigateToPatientProfile(patient)
            }
        }
    }

    private fun navigateToPatientProfile(patient: Appointment) {
        val intent = Intent(this, MyPatientProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_DOCTOR_PATIENTS, patient)
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

    private fun handleEvents(myPatientsViewEvent: MyPatientsViewEvent?) {

        when (myPatientsViewEvent) {
            MyPatientsViewEvent.NavigateToHome -> {
                navigateToHome()
            }
            null -> TODO()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, DoctorHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

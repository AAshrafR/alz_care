package com.example.alzcare.ui.patient.my_appointments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityMyAppointmentsBinding
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.showMessage

class MyAppointmentsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMyAppointmentsBinding
    private val viewModel: MyAppointmentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_appointments)

        initViews()
        subscribeToLiveData()
    }

    private fun initViews() {

        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this

        val myRecyclerView = viewBinding.recyclerViewAppointments
        val patientMyAppointmentService = AppointmentDao
        val patientId = SessionProvider.user?.id ?: "" // Ensure doctorId is not null

        // Fetch appointments for the patient
        patientMyAppointmentService.getAcceptedAppointmentsForPatient(patientId) { appointments ->
            Log.d(Appointment.CollectionName, appointments.toString())
            val sortedAppointments = appointments.sortedByDescending { it.appointmentDateTime?.toDate() }
            val adapter = MyAppointmentsRecyclerAdapter(this, sortedAppointments)
            myRecyclerView.adapter = adapter
        }
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

    private fun handleEvents(myAppointmentsViewEvent: MyAppointmentsViewEvent?) {

        when (myAppointmentsViewEvent) {
            MyAppointmentsViewEvent.NavigateToHome-> {
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
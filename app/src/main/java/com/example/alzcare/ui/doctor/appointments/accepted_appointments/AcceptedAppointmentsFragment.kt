package com.example.alzcare.ui.doctor.appointments.accepted_appointments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.FragmentAcceptedAppointmentsBinding
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.showMessage

class AcceptedAppointmentsFragment : Fragment() {

    private lateinit var binding: FragmentAcceptedAppointmentsBinding
    private val viewModel: AcceptedAppointmentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptedAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribeToLiveData()
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val recyclerView = binding.myPatientsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val doctorId = SessionProvider.user?.id ?: ""

        // Fetch accepted appointments for the doctor
        AppointmentDao.getAcceptedAppointmentsForDoctor(doctorId) { appointments ->
            Log.d(Appointment.CollectionName, appointments.toString())
            val sortedAppointments = appointments.sortedByDescending { it.appointmentDateTime?.toDate() }
            val adapter = AcceptedAppointmentsAdapter(requireActivity(), sortedAppointments)
            recyclerView.adapter = adapter
        }
    }

    private fun subscribeToLiveData() {
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            showMessage(
                it.message ?: "",
                posActionName = it.posActionName,
                posAction = it.onPosActionClick,
                negActionName = it.negActionName,
                negAction = it.onNegActionClick
            )
        }
        viewModel.events.observe(viewLifecycleOwner) { handleEvents(it) }
    }

    private fun handleEvents(acceptedAppointmentsViewEvent: AcceptedAppointmentsViewEvent?) {
        when (acceptedAppointmentsViewEvent) {
            AcceptedAppointmentsViewEvent.NavigateToHome -> navigateToHome()
            null -> TODO()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), DoctorHomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}

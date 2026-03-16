package com.example.alzcare.ui.doctor.appointments.waiting_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.FragmentWaitinglistBinding
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.model.Appointment
import com.example.alzcare.ui.doctor.appointments.accepted_appointments.AcceptedAppointmentsViewEvent
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.showMessage

class WaitingListFragment : Fragment() {

    private lateinit var binding: FragmentWaitinglistBinding
    private val viewModel: WaitingListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaitinglistBinding.inflate(inflater, container, false)
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

        val recyclerView = binding.recyclerViewWaitingAppointments
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val doctorAppointmentService = AppointmentDao
        val doctorId = SessionProvider.user?.id ?: ""

        doctorAppointmentService.getWaitingAppointmentsForDoctor(doctorId) { appointments ->
            Log.d(Appointment.CollectionName, appointments.toString())
            val adapter = WaitingListRecyclerAdapter(requireActivity(), appointments)
            adapter.onAcceptClickListener = object : WaitingListRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, appointment: Appointment) {
                    acceptAppointment(appointment)
                }
            }

            adapter.onRefuseClickListener = object : WaitingListRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, appointment: Appointment) {
                    refuseAppointment(appointment)
                }
            }

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

    private fun handleEvents(waitingListViewEvent: WaitingListViewEvent?) {
        when (waitingListViewEvent) {
            WaitingListViewEvent.NavigateToHome-> navigateToHome()
            null -> TODO()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), DoctorHomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun acceptAppointment(appointment: Appointment) {
        val doctorId = SessionProvider.user?.id ?: ""
        val appointmentId = appointment.appointmentId
        val patientId = appointment.patientId
        if (appointmentId != null && patientId != null) {
            AppointmentDao.acceptAppointment(doctorId, patientId, appointmentId) { task ->
                if (task.isSuccessful) {
                    // Update UI
                    showMessage("Appointment accepted successfully")
                    // Remove the accepted appointment from the list
                    val updatedAppointments = (binding.recyclerViewWaitingAppointments.adapter as? WaitingListRecyclerAdapter)?.appointments.orEmpty().toMutableList()
                    updatedAppointments.remove(appointment)
                    // Update the adapter with the new list of appointments
                    (binding.recyclerViewWaitingAppointments.adapter as? WaitingListRecyclerAdapter)?.updateAppointments(updatedAppointments)
                } else {
                    // Handle failure
                    showMessage("Failed to accept appointment. Please try again later.")
                }
            }
        }
    }

    private fun refuseAppointment(appointment: Appointment) {
        val doctorId = SessionProvider.user?.id ?: ""
        val appointmentId = appointment.appointmentId
        if (appointmentId != null) {
            AppointmentDao.refuseAppointment(doctorId, appointmentId) { task ->
                if (task.isSuccessful) {
                    // Update UI
                    showMessage("Appointment refused successfully")
                    // Remove the refused appointment from the list
                    val updatedAppointments = (binding.recyclerViewWaitingAppointments.adapter as? WaitingListRecyclerAdapter)?.appointments.orEmpty().toMutableList()
                    updatedAppointments.remove(appointment)
                    // Update the adapter with the new list of appointments
                    (binding.recyclerViewWaitingAppointments.adapter as? WaitingListRecyclerAdapter)?.updateAppointments(updatedAppointments)
                } else {
                    // Handle failure
                    showMessage("Failed to refuse appointment. Please try again later.")
                }
            }
        }
    }
}

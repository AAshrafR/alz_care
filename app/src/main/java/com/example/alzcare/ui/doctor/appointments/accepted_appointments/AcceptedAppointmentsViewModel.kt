package com.example.alzcare.ui.doctor.appointments.accepted_appointments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message

class AcceptedAppointmentsViewModel : ViewModel() {


    val messageLiveData = MutableLiveData<Message>()
    val events = SingleLiveEvent<AcceptedAppointmentsViewEvent>()

    fun goHome() {
        events.postValue(AcceptedAppointmentsViewEvent.NavigateToHome)
    }
}

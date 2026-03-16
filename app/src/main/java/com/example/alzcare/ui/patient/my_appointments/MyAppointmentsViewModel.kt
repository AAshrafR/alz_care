package com.example.alzcare.ui.patient.my_appointments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message

class MyAppointmentsViewModel : ViewModel() {

    val messageLiveData = MutableLiveData<Message>()
    val events = SingleLiveEvent<MyAppointmentsViewEvent>()

    fun goHome() {
        events.postValue(MyAppointmentsViewEvent.NavigateToHome)
    }
}
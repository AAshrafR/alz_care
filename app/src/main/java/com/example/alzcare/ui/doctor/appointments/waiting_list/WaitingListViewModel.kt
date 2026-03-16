package com.example.alzcare.ui.doctor.appointments.waiting_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message

class WaitingListViewModel : ViewModel() {


    val messageLiveData = MutableLiveData<Message>()
    val events = SingleLiveEvent<WaitingListViewEvent>()

    fun goHome() {
        events.postValue(WaitingListViewEvent.NavigateToHome)
    }
}
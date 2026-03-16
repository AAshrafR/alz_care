package com.example.alzcare.ui.doctor.my_patients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message

class MyPatientsViewModel : ViewModel() {

    val messageLiveData = MutableLiveData<Message>()
    val events = SingleLiveEvent<MyPatientsViewEvent>()

    fun goHome() {
        events.postValue(MyPatientsViewEvent.NavigateToHome)
    }
}

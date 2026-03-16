package com.example.alzcare.ui.patient.my_doctors


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message

class MyDoctorsViewModel : ViewModel() {


    val messageLiveData = MutableLiveData<Message>()
    val events = SingleLiveEvent<MyDoctorsViewEvent>()

    fun goHome() {
        events.postValue(MyDoctorsViewEvent.NavigateToHome)
    }
}

package com.example.alzcare.ui.doctor.home

import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DoctorHomeViewModel : ViewModel() {
    val events = SingleLiveEvent<DoctorHomeViewEvent>()
    val messageLiveData = SingleLiveEvent<Message>()

    fun doctorAppointments(){
        events.postValue(DoctorHomeViewEvent.NavigateToAppointments)
    }

    fun myPatients(){
        events.postValue(DoctorHomeViewEvent.NavigateToMyPatients)
    }

    fun navigateToProfile() {
        events.postValue(DoctorHomeViewEvent.NavigateToProfile)
    }

    fun logout() {
        messageLiveData.postValue(
            Message(
                message = "Do you want to logout",
                posActionName = "ok",
                onPosActionClick = {
                    Firebase.auth.signOut()
                    SessionProvider.user = null
                    events.postValue(DoctorHomeViewEvent.NavigateToLogin)
                },
                negActionName = "cancel"
            )
        )
    }

    fun navigateToAboutAlzCare() {
        events.postValue(DoctorHomeViewEvent.NavigateToAboutAlzCare)
    }
}

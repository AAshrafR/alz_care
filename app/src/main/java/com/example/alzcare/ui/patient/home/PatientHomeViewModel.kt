package com.example.alzcare.ui.patient.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.ui.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PatientHomeViewModel : ViewModel() {
    val events = SingleLiveEvent<PatientHomeViewEvent>()
    val messageLiveData = SingleLiveEvent<Message>()

    private val adviceList = Advice.adviceList // Use the Advice class directly
    private val advice = MutableLiveData<String>()

    init {
        getRandomAdvice()
    }

    private fun getRandomAdvice() {
        if (adviceList.isNotEmpty()) {
            val randomIndex = adviceList.indices.random()
            advice.value = adviceList[randomIndex]
        }
    }
    fun navigateToSearch() {
        events.postValue(PatientHomeViewEvent.NavigateToSearch)
    }
    fun navigateToMyAppointments(){
        events.postValue(PatientHomeViewEvent.NavigateToMyAppointments)
    }
    fun navigateToMyDoctors(){
        events.postValue(PatientHomeViewEvent.NavigateToMyDoctors)
    }
    fun openModelPage(){
        events.postValue(PatientHomeViewEvent.NavigateToModelPage)
    }
    fun navigateToProfile() {
        events.postValue(PatientHomeViewEvent.NavigateToProfile)
    }
    fun logout() {
        messageLiveData.postValue(
            Message(
                message = "Do you want to logout",
                posActionName = "ok",
                onPosActionClick = {
                    Firebase.auth.signOut()
                    SessionProvider.user = null
                    events.postValue(PatientHomeViewEvent.NavigateToLogin)
                },
                negActionName = "cancel"
            )
        )
    }
    fun navigateToAboutAlzCare() {
        events.postValue(PatientHomeViewEvent.NavigateToAboutAlzCare)
    }
}
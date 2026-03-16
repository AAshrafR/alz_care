package com.example.alzcare.ui.about_us

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider

class AboutUsViewModel : ViewModel() {
    val events = MutableLiveData<AboutUsViewEvent>()

    private val user= SessionProvider.user

    fun onBackButtonPressed() {
        // Check the user's role
        when (SessionProvider.user?.role) {
            "Doctor" -> events.postValue(AboutUsViewEvent.NavigateToDoctorHome)
            "Patient" -> events.postValue(AboutUsViewEvent.NavigateToPatientHome)
        }
    }
}
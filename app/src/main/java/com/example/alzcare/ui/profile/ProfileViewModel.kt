package com.example.alzcare.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider

class ProfileViewModel : ViewModel() {
    val events = MutableLiveData<ProfileViewEvent>()

    private val user=SessionProvider.user

    val userName = user?.userName ?: ""

    val email = user?.email ?: ""

    val phoneNumber = user?.phoneNumber ?: ""

    fun onBackButtonPressed() {
        // Check the user's role
        when (SessionProvider.user?.role) {
            "Doctor" -> events.postValue(ProfileViewEvent.NavigateToDoctorHome)
            "Patient" -> events.postValue(ProfileViewEvent.NavigateToPatientHome)
        }
    }
}

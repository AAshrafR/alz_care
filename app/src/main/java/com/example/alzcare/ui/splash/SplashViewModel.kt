package com.example.alzcare.ui.splash

import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {
    val events = SingleLiveEvent<SplashViewEvent>()
    fun redirect() {
        if (Firebase.auth.currentUser == null) {
            events.postValue(SplashViewEvent.NavigateToLogin)
            return
        }
        UsersDao.getUser(
            Firebase.auth.currentUser?.uid ?: ""
        ) { task ->
            if (!task.isSuccessful) {
                SplashViewEvent.NavigateToLogin
                return@getUser
            }
            val user = task.result.toObject(User::class.java)
            SessionProvider.user = user
            events.postValue(SplashViewEvent.NavigateToHome)
        }
    }
}
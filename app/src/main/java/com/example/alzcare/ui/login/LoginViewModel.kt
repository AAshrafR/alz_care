package com.example.alzcare.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.User
import com.example.alzcare.ui.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val messageLiveData = SingleLiveEvent<Message>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    val events = SingleLiveEvent<LoginViewEvent>()

    val auth = Firebase.auth

    fun login() {
        if (!validForm()) return;
        isLoading.value = true
        auth.signInWithEmailAndPassword(
            email.value!!,
            password.value!!
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                getUserFromFirestore(task.result.user?.uid)
            } else {
                isLoading.value = false
                messageLiveData.postValue(
                    Message(
                        message = task.exception?.localizedMessage
                    )
                )
            }
        }
    }

    private fun getUserFromFirestore(uid: String?) {
        UsersDao.getUser(uid) { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                val user = task.result.toObject(User::class.java)
                SessionProvider.user = user
                messageLiveData.postValue(
                    Message(
                        message = "Logged in successfully",
                        posActionName = "ok",
                        onPosActionClick = {
                            //go to home
                            events.postValue(LoginViewEvent.NavigateToHome)
                        },
                        isCancelable = false
                    )
                )
            } else {
                messageLiveData.postValue(
                    Message(
                        message = task.exception?.localizedMessage
                    )
                )
            }

        }
    }

    private fun validForm(): Boolean {
        var isValid = true
        if (email.value.isNullOrBlank()) {
            emailError.postValue("Please enter user E-mail")
            isValid = false
        } else {
            emailError.postValue(null)
        }

        if (password.value.isNullOrBlank()) {
            passwordError.postValue("Please enter a password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }

        return isValid
    }

    fun navigateToRegister() {
        events.postValue(LoginViewEvent.NavigateToRegister)
    }
}
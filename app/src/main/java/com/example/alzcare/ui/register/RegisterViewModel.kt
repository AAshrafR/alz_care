package com.example.alzcare.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.SessionProvider
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.User
import com.example.alzcare.ui.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {

    val messageLiveData = SingleLiveEvent<Message>()

    val userName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirmation = MutableLiveData<String>()
    val selectedRole = MutableLiveData<String>()

    val userNameError = MutableLiveData<String?>()
    val phoneNumberError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmError = MutableLiveData<String?>()

    val events = SingleLiveEvent<RegisterViewEvent>()

    val auth = Firebase.auth
    fun register() {
        if (!validForm()) return;
        auth.createUserWithEmailAndPassword(
            email.value!!,
            password.value!!
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                insertUserToFireStore(task.result?.user?.uid)
            } else {
                messageLiveData.postValue(
                    Message(
                        message = task.exception?.localizedMessage
                    )
                )
            }
        }
    }

    private fun insertUserToFireStore(uid: String?) {
        val user = User(
            id = uid,
            userName = userName.value,
            phoneNumber = phoneNumber.value,
            email = email.value,
            role = selectedRole.value
        )
        UsersDao.createUser(user) { task ->
            if (task.isSuccessful) {
                messageLiveData.postValue(
                    Message(
                        message = "User Registered Successfully",
                        posActionName = "ok",
                        onPosActionClick = {
                            //save user
                            SessionProvider.user = user

                            //go to home
                            events.postValue(RegisterViewEvent.NavigateToHome)
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

    fun patientRegistration() {
        selectedRole.value = "Patient"
    }

    fun doctorRegistration() {
        selectedRole.value = "Doctor"
    }

    private fun validForm(): Boolean {

        var isValid = true

        if (userName.value.isNullOrBlank()) {
            userNameError.postValue("Please enter user name")
            isValid = false
        } else {
            userNameError.postValue(null)
        }
        if (phoneNumber.value.isNullOrBlank()) {
            phoneNumberError.postValue("Please enter phone number")
            isValid = false
        } else {
            userNameError.postValue(null)
        }

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

        if (selectedRole.value.isNullOrBlank()) {
            messageLiveData.postValue(
                Message(
                    message = "Please select a role"
                )
            )
            isValid = false
        }

        if (passwordConfirmation.value.isNullOrBlank() || passwordConfirmation.value != password.value) {
            passwordConfirmError.postValue("password isn't match")
            isValid = false
        } else {
            passwordConfirmError.postValue(null)
        }

        return isValid
    }

    fun navigateToLogin() {
        events.postValue(RegisterViewEvent.NavigateToLogin)
    }
}
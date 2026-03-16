package com.example.alzcare.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityLoginBinding
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.register.RegisterActivity
import com.example.alzcare.ui.showMessage

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        viewModel.messageLiveData.observe(this) { message ->

            showMessage(
                message = message.message ?: "something went wrong",
                posActionName = "ok",
                posAction = message.onPosActionClick,
                negActionName = message.negActionName,
                negAction = message.onNegActionClick,
                isCancelable = message.isCancelable
            )
        }
        viewModel.events.observe(this, ::hadelEvents)
    }

    private fun hadelEvents(loginViewEvent: LoginViewEvent?) {

        when (loginViewEvent) {
            LoginViewEvent.NavigateToHome -> {
                navigateToHome()
            }

            LoginViewEvent.NavigateToRegister -> {
                navigateToRegister()
            }

            null -> TODO()
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
            val intent:Intent
            when (SessionProvider.user?.role) {
                "Doctor" -> {
                    intent = Intent(this, DoctorHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Patient" -> {
                    intent = Intent(this, PatientHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

    private fun initViews() {
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
    }
}
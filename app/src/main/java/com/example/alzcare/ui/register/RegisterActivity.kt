package com.example.alzcare.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityRegisterBinding
import com.example.alzcare.ui.doctor.home.DoctorHomeActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.login.LoginActivity
import com.example.alzcare.ui.showMessage

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
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

    private fun hadelEvents(registerViewEvent: RegisterViewEvent?) {

        when (registerViewEvent) {
            RegisterViewEvent.NavigateToHome -> {
                navigateToHome()
            }

            RegisterViewEvent.NavigateToLogin -> {
                navigateToLogin()
            }

            null -> TODO()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
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
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
    }
}
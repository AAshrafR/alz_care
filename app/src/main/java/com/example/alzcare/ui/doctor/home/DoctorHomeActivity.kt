package com.example.alzcare.ui.doctor.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityDoctorHomeBinding
import com.example.alzcare.ui.about_us.AboutUsActivity
import com.example.alzcare.ui.doctor.appointments.AppointmentFragmentContainerActivity
import com.example.alzcare.ui.doctor.my_patients.MyPatientsActivity
import com.example.alzcare.ui.login.LoginActivity
import com.example.alzcare.ui.patient.home.PatientHomeViewEvent
import com.example.alzcare.ui.profile.ProfileActivity
import com.example.alzcare.ui.showMessage

class DoctorHomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDoctorHomeBinding
    private val viewModel: DoctorHomeViewModel by viewModels()
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_home)

        initViews()
        subscribeToLiveData()
        setupNavigationDrawer()

        // Set up toolbar
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Set the menu icon

        // Set the toolbar title font and gravity
        val title = viewBinding.toolbar.getChildAt(0) as? TextView
        title?.let {
            it.gravity = Gravity.CENTER // Center the title
            it.typeface = ResourcesCompat.getFont(this, R.font.montserrat_bold) // Set Montserrat-Bold font
        }
    }

    private fun initViews() {
        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this
        val navigationViewHeaderUserName = viewBinding.navView
            .getHeaderView(0).findViewById<TextView>(R.id.per_user_name)
        navigationViewHeaderUserName.text=SessionProvider.user?.userName
    }

    private fun subscribeToLiveData() {
        viewModel.events.observe(this, ::handleEvents)
        viewModel.messageLiveData.observe(this) {
            showMessage(
                it.message ?: "",
                posActionName = it.posActionName,
                posAction = it.onPosActionClick,
                negActionName = it.negActionName,
                negAction = it.onNegActionClick,
                isCancelable = it.isCancelable
            )
        }
    }

    private fun handleEvents(doctorHomeViewEvent: DoctorHomeViewEvent?) {
        when (doctorHomeViewEvent) {
            DoctorHomeViewEvent.NavigateToLogin -> navigateToLogin()
            DoctorHomeViewEvent.NavigateToAppointments -> navigateToDoctorAppointments()
            DoctorHomeViewEvent.NavigateToMyPatients -> navigateToDoctorPatients()
            DoctorHomeViewEvent.NavigateToProfile -> navigateToProfile()
            DoctorHomeViewEvent.NavigateToAboutAlzCare -> navigateToAboutAlzCare()

            null -> TODO()
        }
    }

    private fun navigateToAboutAlzCare() {
        val intent = Intent(this, AboutUsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDoctorPatients() {
        val intent = Intent(this, MyPatientsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDoctorAppointments() {
        val intent = Intent(this, AppointmentFragmentContainerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupNavigationDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, viewBinding.drawerLayout, viewBinding.toolbar, R.string.open, R.string.close)
        viewBinding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        viewBinding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    // Handle profile click here
                    viewModel.navigateToProfile()
                    true
                }
                R.id.action_logout -> {
                    // Handle logout click here
                    viewModel.logout()
                    true
                }
                R.id.action_about_app -> {
                    // Handle about app click here
                    viewModel.navigateToAboutAlzCare()
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewBinding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

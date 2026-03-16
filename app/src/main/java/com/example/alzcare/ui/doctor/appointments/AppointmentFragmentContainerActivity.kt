package com.example.alzcare.ui.doctor.appointments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivityAppointmentFragmentContainerBinding
import com.example.alzcare.ui.doctor.appointments.accepted_appointments.AcceptedAppointmentsFragment
import com.example.alzcare.ui.doctor.appointments.waiting_list.WaitingListFragment

class AppointmentFragmentContainerActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityAppointmentFragmentContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAppointmentFragmentContainerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_waiting_list -> {
                    showTabFragment(WaitingListFragment())
                }

                R.id.navigation_accepted_appointment -> {
                    showTabFragment(AcceptedAppointmentsFragment())
                }
            }
            true
        }
        // set initial selected button
        viewBinding.bottomNav.selectedItemId = R.id.navigation_waiting_list
    }

    private fun showTabFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }
}
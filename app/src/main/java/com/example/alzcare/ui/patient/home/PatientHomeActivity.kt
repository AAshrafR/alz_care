package com.example.alzcare.ui.patient.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityPatientHomeBinding
import com.example.alzcare.ui.about_us.AboutUsActivity
import com.example.alzcare.ui.login.LoginActivity
import com.example.alzcare.ui.patient.ml.DLModelActivity
import com.example.alzcare.ui.patient.my_appointments.MyAppointmentsActivity
import com.example.alzcare.ui.patient.my_doctors.MyDoctorsActivity
import com.example.alzcare.ui.patient.search.SearchActivity
import com.example.alzcare.ui.profile.ProfileActivity
import com.example.alzcare.ui.showMessage
import java.util.*

class PatientHomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPatientHomeBinding
    private val viewModel: PatientHomeViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var adviceAdapter: AdviceAdapter
    private var currentPage: Int = 0
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_patient_home)

        initViews()
        subscribeToLiveData()

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

        // Set user name
        viewBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.per_user_name).text = SessionProvider.user?.userName

        // Set up navigation drawer
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

    private fun initViews() {
        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this

        // Initialize ViewPager
        viewPager = viewBinding.adviceViewPager
        viewPager.offscreenPageLimit = 3

        // Get a list of 5 random advices (modify number as needed)
        val randomAdvices = List(5) { Advice.adviceList.random() }  // Use List constructor

        // Create and set adapter for ViewPager
        adviceAdapter = AdviceAdapter(this, randomAdvices)
        viewPager.adapter = adviceAdapter

        // Automatically switch between advices with slower scroll speed
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    // If the ViewPager has stopped scrolling, check if we need to loop to the first or last item
                    if (currentPage == 0) {
                        // If currently at the first item, navigate to the last item
                        viewPager.setCurrentItem(adviceAdapter.itemCount - 1, false)
                    } else if (currentPage == adviceAdapter.itemCount - 1) {
                        // If currently at the last item, navigate to the first item
                        viewPager.setCurrentItem(0, false)
                    }
                }
            }
        })

        // Start auto-scrolling
        startAutoScroll()
    }

    private fun startAutoScroll() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (currentPage < adviceAdapter.itemCount - 1) {
                        viewPager.setCurrentItem(currentPage + 1, true)
                    } else {
                        viewPager.setCurrentItem(0, true)
                    }
                }
            }
        }, 5000, 5000) // Delay: 5 seconds, Period: 5 seconds
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

    private fun handleEvents(patientHomeViewEvent: PatientHomeViewEvent?) {
        when (patientHomeViewEvent) {
            PatientHomeViewEvent.NavigateToLogin -> {
                navigateToLogin()
            }
            PatientHomeViewEvent.NavigateToSearch -> {
                navigateToSearch()
            }
            PatientHomeViewEvent.NavigateToModelPage ->{
                openModelPage()
            }
            PatientHomeViewEvent.NavigateToMyAppointments ->{
                navigateToMyAppointments()
            }
            PatientHomeViewEvent.NavigateToMyDoctors -> {
                navigateToMyDoctors()
            }
            PatientHomeViewEvent.NavigateToProfile -> {
                navigateToProfile()
            }
            PatientHomeViewEvent.NavigateToAboutAlzCare -> {
                navigateToAboutAlzCare()
            }

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


    private fun navigateToMyDoctors() {
        val intent = Intent(this, MyDoctorsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMyAppointments() {
        val intent = Intent(this, MyAppointmentsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openModelPage(){
        val intent = Intent(this, DLModelActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the auto-scrolling timer to prevent memory leaks
        timer?.cancel()
    }
}
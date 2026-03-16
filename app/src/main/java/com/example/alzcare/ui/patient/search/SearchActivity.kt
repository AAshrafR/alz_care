package com.example.alzcare.ui.patient.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.databinding.ActivitySearchBinding
import com.example.alzcare.model.User
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.patient.make_appointment.PatientAppointmentActivity
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import com.example.alzcare.ui.showMessage

class SearchActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        initViews()
        subscribeToLiveData()
        viewModel.loadDocs()
    }


    private fun subscribeToLiveData() {

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

        viewModel.docsLiveData.observe(this) {
            adapter.updateData(it) // Use updateData to notify adapter about changes
        }

        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    viewModel.docsLiveData.value?.let { docsList ->
                        val searchResult = viewModel.searchList(docsList, text)
                        adapter.updateData(searchResult) // Use updateData for changes
                    }
                }
                return true
            }
        })

        viewModel.events.observe(this, ::handelEvents)
    }

    private fun handelEvents(searchViewEvent: SearchViewEvent?) {

        when (searchViewEvent) {
            SearchViewEvent.NavigateToHome -> {
                navigateToHome()
            }
            null -> TODO()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, PatientHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val adapter = DoctorRecyclerAdapter()
    private fun initViews() {
        viewBinding.vm = viewModel
        viewBinding.lifecycleOwner = this
        viewBinding.recyclerViewDoctorUsers.adapter = adapter
        adapter.onItemClickListener = DoctorRecyclerAdapter.OnItemClickListener { position, doctor ->
            navigateToDoctorAppointment(doctor)
        }
    }

    private fun navigateToDoctorAppointment(doctor: User) {
        val intent = Intent(this, PatientAppointmentActivity::class.java)
        intent.putExtra(Constants.EXTRA_DOCTOR_ID, doctor)
        startActivity(intent)
        finish()
    }
}

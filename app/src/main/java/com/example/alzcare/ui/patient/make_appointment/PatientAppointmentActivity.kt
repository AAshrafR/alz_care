package com.example.alzcare.ui.patient.make_appointment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alzcare.R
import com.example.alzcare.SessionProvider
import com.example.alzcare.databinding.ActivityPatientAppointmentBinding
import com.example.alzcare.model.User
import com.example.alzcare.ui.Constants
import com.example.alzcare.ui.patient.search.SearchActivity
import com.example.alzcare.ui.showMessage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class PatientAppointmentActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPatientAppointmentBinding
    private val viewModel: PatientAppointmentViewModel by viewModels()
    private lateinit var selectDateButton: Button
    private lateinit var makeAppointmentButton: Button
    private lateinit var timeSpinner: Spinner
    private var selectedDate: LocalDate? = null
    private var availableTimes: List<LocalTime>? = null
    private var doctorId: String? = null // Store the doctor's ID
    private var doctorName: String? = null // Store the doctor's name

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initParams()
        subscribeToLiveData()
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_patient_appointment)
        selectDateButton = viewBinding.checkAvailabilityButton
        makeAppointmentButton = viewBinding.confirmAppointmentButton
        timeSpinner = viewBinding.timeSpinner

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        makeAppointmentButton.setOnClickListener {
            makeAppointment()
        }

        viewBinding.backButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initParams() {
        val doctor = intent.getParcelableExtra(Constants.EXTRA_DOCTOR_ID) as? User
        doctorId = doctor?.id
        doctorName = doctor?.userName
        viewBinding.doctorNameTextView.text = doctorName
        viewBinding.doctorNumberTextView.text = doctor?.phoneNumber
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                selectDateButton.text = selectedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)

                // Check availability for the selected date and doctor
                selectedDate?.let { date ->
                    doctorId?.let { doctorId ->
                        viewModel.checkAvailability(
                            date,
                            doctorId,
                            onSuccess = { times ->
                                updateAvailableTimes(times)
                            },
                            onFailure = { exception ->
                                // Handle failure case here
                                Toast.makeText(this, "Failed to check availability: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            },
            year, month, dayOfMonth
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateAvailableTimes(times: List<LocalTime>) {
        availableTimes = times.distinct() // Filter out duplicate times
        if (availableTimes.isNullOrEmpty()) {
            // No available times for the selected date
            timeSpinner.visibility = View.GONE
            makeAppointmentButton.visibility = View.GONE
            Toast.makeText(this, "No available times for selected date", Toast.LENGTH_SHORT).show()
        } else {
            // Populate spinner with available times
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableTimes!!)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeSpinner.adapter = adapter
            timeSpinner.visibility = View.VISIBLE
            makeAppointmentButton.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeAppointment() {
        val selectedTime = timeSpinner.selectedItem as? LocalTime
        if (selectedDate != null && selectedTime != null) {
            val currentDateTime = LocalDateTime.now()
            val selectedDateTime = LocalDateTime.of(selectedDate!!, selectedTime)

            if (selectedDateTime.isAfter(currentDateTime)) {
                val patientId = SessionProvider.user?.id ?: "" // Get the patient ID from the SessionProvider
                val patientName = SessionProvider.user?.userName ?: "" // Get the patient name from the SessionProvider
                doctorId?.let { doctorId ->
                    doctorName?.let { doctorName ->
                        viewModel.makeAppointment(selectedTime, patientId, patientName, doctorId, doctorName,
                            onSuccess = {
                                Toast.makeText(this, "Appointment successfully scheduled!", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception ->
                                Toast.makeText(this, "Failed to schedule make_appointment: ${exception.message}", Toast.LENGTH_SHORT).show()
                            })
                    }
                }
            } else {
                Toast.makeText(this, "Please select a future date for the make_appointment", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please select a date and time for the make_appointment", Toast.LENGTH_SHORT).show()
        }
    }
}

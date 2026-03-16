package com.example.alzcare.ui.patient.make_appointment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.firestore.AppointmentDao
import com.example.alzcare.ui.Message
import java.time.LocalDate
import java.time.LocalTime

class PatientAppointmentViewModel : ViewModel() {

    private var selectedDate: LocalDate? = null
    val messageLiveData = SingleLiveEvent<Message>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun makeAppointment(
        selectedTime: LocalTime,
        patientId: String,
        patientName: String,
        doctorId: String,
        doctorName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val appointmentDateTime = selectedDate?.atTime(selectedTime)
        if (appointmentDateTime != null) {
            // Convert the LocalDateTime to UTC by subtracting the UTC+2 timezone offset
            val appointmentUTCDateTime = appointmentDateTime.minusHours(2)
            AppointmentDao.makeAppointment(
                patientId,
                patientName,
                doctorId,
                doctorName,
                appointmentUTCDateTime,
                "waiting",
                onCompleteListener = { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception ?: Exception("Unknown error"))
                    }
                })
        } else {
            onFailure(Exception("Invalid make_appointment date/time"))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAvailability(
        date: LocalDate,
        doctorId: String,
        onSuccess: (List<LocalTime>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        selectedDate = date
        AppointmentDao.checkTimeSlotAvailability(date, doctorId,
            onSuccess = { availableTimes ->
                onSuccess(availableTimes)
            },
            onFailure = { exception ->
                onFailure(exception)
            })
    }
}

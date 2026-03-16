package com.example.alzcare.firestore

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.alzcare.model.Appointment
import com.example.alzcare.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

object AppointmentDao {

    private val dp = FirebaseFirestore.getInstance()

    fun getMyDoctors(
        patientId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        dp.collection(User.CollectionName)
            .document(patientId)
            .collection(Appointment.CollectionName)
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsMap = mutableMapOf<String, Appointment>()
                documents.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    val doctorId = appointment.doctorId
                    if (doctorId != null && !appointmentsMap.containsKey(doctorId)) {
                        // Add appointment to the map if doctor ID is not already present
                        appointmentsMap[doctorId] = appointment.copy(appointmentId = document.id)
                    }
                }
                val appointmentsList = appointmentsMap.values.toList()
                callback(appointmentsList)
            }
    }


      fun getMyPatients(
        doctorId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        dp.collection(User.CollectionName)
            .document(doctorId)
            .collection(Appointment.CollectionName)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsMap = mutableMapOf<String, Appointment>()
                documents.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    val patientId = appointment.patientId
                    if (patientId != null && !appointmentsMap.containsKey(patientId)) {
                        // Add appointment to the map if patient ID is not already present
                        appointmentsMap[patientId] = appointment.copy(appointmentId = document.id)
                    }
                }
            val appointmentsList = appointmentsMap.values.toList()
            callback(appointmentsList)
        }
    }

    fun getAcceptedAppointmentsForDoctor(
        doctorId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        dp.collection(User.CollectionName)
            .document(doctorId)
            .collection(Appointment.CollectionName)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointment?.copy(appointmentId = document.id)
                }
            callback(appointmentsList)
        }
    }

    fun getAcceptedAppointmentsForPatient(
        patientId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        dp.collection(User.CollectionName)
            .document(patientId)
            .collection(Appointment.CollectionName)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointment?.copy(appointmentId = document.id)
                }
            callback(appointmentsList)
        }
    }

    fun getWaitingAppointmentsForDoctor(
        doctorId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        dp.collection(User.CollectionName)
            .document(doctorId)
            .collection(Appointment.CollectionName)
            .whereEqualTo("status", "waiting")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointment?.copy(appointmentId = document.id)
                }
                callback(appointmentsList)
            }
    }


    private fun getAppointmentsCollection(doctorId: String): CollectionReference {
        return UsersDao.getUserCollection().document(doctorId).collection(Appointment.CollectionName)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun makeAppointment(
        patientId: String,
        patientName: String,
        doctorId: String,
        doctorName: String,
        appointmentDateTime: LocalDateTime,
        status: String,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        val collection = getAppointmentsCollection(doctorId)
        val appointmentDoc = collection.document()
        val timestamp = Timestamp(appointmentDateTime.toEpochSecond(ZoneOffset.UTC), 0)
        val appointment = Appointment(
            appointmentId = appointmentDoc.id,
            patientId = patientId,
            patientName = patientName,
            doctorId = doctorId,
            doctorName = doctorName,
            appointmentDateTime = timestamp,
            status = "waiting",
            ended = false
        )
        appointmentDoc.set(appointment).addOnCompleteListener(onCompleteListener)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkTimeSlotAvailability(
        date: LocalDate,
        doctorId: String,
        onSuccess: (List<LocalTime>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val bookedTimeSlots = mutableSetOf<LocalTime>()
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()

        getAppointmentsCollection(doctorId)
            .whereGreaterThanOrEqualTo("appointmentDateTime", startOfDay)
            .whereLessThan("appointmentDateTime", endOfDay)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot?.let {
                    for (document in it.documents) {
                        val appointmentDateTime = document["appointmentDateTime"] as? Timestamp
                        val appointmentTime = appointmentDateTime?.toDate()?.toInstant()?.atZone(
                            ZoneId.systemDefault())?.toLocalTime()
                        appointmentTime?.let {
                            bookedTimeSlots.add(it)
                        }
                    }
                }
                val availableTimes = mutableListOf<LocalTime>()
                val startHour = 14
                val endHour = 23
                for (hour in startHour..endHour) {
                    val timeSlot = LocalTime.of(hour, 0)
                    if (!bookedTimeSlots.contains(timeSlot)) {
                        availableTimes.add(timeSlot)
                    }
                    val halfHourSlot = LocalTime.of(hour, 30)
                    if (!bookedTimeSlots.contains(halfHourSlot)) {
                        availableTimes.add(halfHourSlot)
                    }
                }
                onSuccess(availableTimes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun acceptAppointment(
        doctorId: String,
        patientId: String,
        appointmentId: String,
        onCompleteListener: OnCompleteListener<DocumentReference>
    ) {
        // Get the make_appointment document reference for the doctor
        val doctorAppointmentDocRef = getAppointmentsCollection(doctorId).document(appointmentId)

        // Update the status of the make_appointment to "accepted"
        doctorAppointmentDocRef.update("status", "accepted")
            .addOnCompleteListener { doctorUpdateTask ->
                if (doctorUpdateTask.isSuccessful) {
                    // Retrieve the make_appointment data for the doctor
                    doctorAppointmentDocRef.get().addOnSuccessListener { appointmentDoc ->
                        val appointmentData = appointmentDoc.toObject(Appointment::class.java)

                        // Make a copy of the make_appointment for the patient
                        if (appointmentData != null) {
                            getAppointmentsCollection(patientId).add(appointmentData)
                                .addOnCompleteListener(onCompleteListener)
                        } else {
                            // If make_appointment data is unexpectedly null, complete with failure
                            onCompleteListener.onComplete(TaskCompletionSource<DocumentReference>().task)
                        }
                    }
                } else {
                    // If the update fails, complete with failure
                    onCompleteListener.onComplete(TaskCompletionSource<DocumentReference>().task)
                }
            }
    }

    fun refuseAppointment(
        doctorId: String,
        appointmentId: String,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        getAppointmentsCollection(doctorId).document(appointmentId)
            .update("status", "refused")
            .addOnCompleteListener(onCompleteListener)
    }
}

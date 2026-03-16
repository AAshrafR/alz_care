// Appointment.kt
package com.example.alzcare.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Appointment(
    var appointmentId: String? = null,
    var patientId: String? = null,
    var patientName: String? = null,
    var doctorId: String? = null,
    var doctorName: String? = null,
    val appointmentDateTime: Timestamp? = null,
    var status: String? = null,
    val ended: Boolean = false
): Parcelable {
    companion object {
        const val CollectionName = "appointments"
    }
}

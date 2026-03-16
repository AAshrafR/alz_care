package com.example.alzcare.ui.doctor.appointments.accepted_appointments

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzcare.R
import com.example.alzcare.model.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class AcceptedAppointmentsAdapter(private val context: Activity, private val list: List<Appointment>) :
    RecyclerView.Adapter<AcceptedAppointmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_accepted_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = list[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView = itemView.findViewById(R.id.patientName_tv)
        private val appointmentTime: TextView = itemView.findViewById(R.id.date_time_tv)

        fun bind(appointment: Appointment) {
            patientName.text = appointment.patientName

            // Format the Timestamp to display only day, month, and time
            val dateFormat = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
            val formattedDateTime = dateFormat.format(appointment.appointmentDateTime?.toDate())
            appointmentTime.text = formattedDateTime
        }
    }
}

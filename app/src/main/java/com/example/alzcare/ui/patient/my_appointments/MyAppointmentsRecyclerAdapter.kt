package com.example.alzcare.ui.patient.my_appointments

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

class MyAppointmentsRecyclerAdapter(private val context: Activity, private val list: List<Appointment>) :
    RecyclerView.Adapter<MyAppointmentsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_appointment, parent, false)
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
        private val doctorName: TextView = itemView.findViewById(R.id.doctor_name_text_view)
        private val appointmentTime: TextView = itemView.findViewById(R.id.appointment_time_text_view)

        fun bind(appointment: Appointment) {
            doctorName.text = appointment.doctorName

            // Format the Timestamp to display only day, month, and time
            val dateFormat = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
            val formattedDateTime = dateFormat.format(appointment.appointmentDateTime?.toDate())
            appointmentTime.text = formattedDateTime
        }
    }
}

package com.example.alzcare.ui.doctor.appointments.waiting_list

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzcare.R
import com.example.alzcare.model.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class WaitingListRecyclerAdapter(private val context: Activity, var appointments: List<Appointment>)
    : RecyclerView.Adapter<WaitingListRecyclerAdapter.ViewHolder>() {

    var onAcceptClickListener: OnItemClickListener? = null
    var onRefuseClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView = itemView.findViewById(R.id.patientNameTextView)
        private val appointmentTime: TextView = itemView.findViewById(R.id.appointment_time)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val refuseButton: Button = itemView.findViewById(R.id.refuseButton)

        fun bind(appointment: Appointment) {
            patientName.text = appointment.patientName
            // Format the Timestamp to display only day, month, and time
            val dateFormat = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
            val formattedDateTime = dateFormat.format(appointment.appointmentDateTime?.toDate())
            appointmentTime.text = formattedDateTime

            acceptButton.setOnClickListener {
                onAcceptClickListener?.onItemClick(adapterPosition, appointment)
            }

            refuseButton.setOnClickListener {
                onRefuseClickListener?.onItemClick(adapterPosition, appointment)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, appointment: Appointment)
    }
}

package com.example.alzcare.ui.doctor.my_patients

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzcare.R
import com.example.alzcare.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

class MyPatientsRecyclerAdapter(private val context: Activity, private val list: List<Appointment>) :
    RecyclerView.Adapter<MyPatientsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = list[position]
        holder.bind(appointment)
        onItemClickListener?.let {
            holder.itemView.setOnClickListener { view ->
                it.onItemClick(
                    position,
                    list!![position] // Ensure list is not null here
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView = itemView.findViewById(R.id.patientName_txt)

        fun bind(appointment: Appointment) {
            patientName.text = appointment.patientName
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, patAccount: Appointment)
    }

}

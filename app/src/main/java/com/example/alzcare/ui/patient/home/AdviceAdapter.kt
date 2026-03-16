package com.example.alzcare.ui.patient.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzcare.R

class AdviceAdapter(context: Context, private val advices: List<String>) :
    RecyclerView.Adapter<AdviceAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_advice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.adviceText.text = advices[position]
    }

    override fun getItemCount(): Int = advices.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adviceText: TextView = itemView.findViewById(R.id.advice_text)
    }
}

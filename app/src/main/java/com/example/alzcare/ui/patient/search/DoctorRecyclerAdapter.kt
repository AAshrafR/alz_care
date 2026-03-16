package com.example.alzcare.ui.patient.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alzcare.databinding.ItemDoctorBinding
import com.example.alzcare.model.User

class DoctorRecyclerAdapter(var docsList: List<User>? = listOf()) :
    RecyclerView.Adapter<DoctorRecyclerAdapter.ViewHolder>() {

    class ViewHolder(private val itemBinding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(doc: User?) {
            itemBinding.doctorNameTextView.text = doc?.userName
            itemBinding.doctorNumberTextView.text = doc?.phoneNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemDoctorBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int = docsList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(docsList?.get(position))

        onItemClickListener?.let {
            holder.itemView.setOnClickListener { view ->
                it.onItemClick(
                    position,
                    docsList!![position] // Ensure docsList is not null here
                )
            }
        }
    }

    // Function to notify adapter about data set changes
    fun updateData(newList: List<User>) {
        this.docsList = newList
        notifyDataSetChanged()
    }

    var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int, doc: User)
    }
}
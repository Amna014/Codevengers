package com.example.jobvengers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.databinding.ItemJobBinding

class JobListingAdapter(
    private val dataList: List<Jobs>,
) : RecyclerView.Adapter<JobListingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemJobBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.binding.apply {
            textViewJobType.text = data.jobType
            textViewLocation.text = data.location
            jobDescription.text = data.description
            textViewSalary.text = data.salary
        }


    }


    override fun getItemCount() = dataList.size
}
package com.example.jobvengers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.databinding.ItemJobBinding
import com.example.jobvengers.databinding.ItemUserBinding

class UserListingAdapter(
    private val dataList: List<ApiResponse>,
) : RecyclerView.Adapter<UserListingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.binding.apply {
           /* textViewJobType.text = data.jobType
            textViewLocation.text = data.location
            jobDescription.text = data.description
            textViewSalary.text = data.salary*/
        }


    }


    override fun getItemCount() = dataList.size
}
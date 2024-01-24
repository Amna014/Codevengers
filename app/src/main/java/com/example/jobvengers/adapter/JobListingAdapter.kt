package com.example.jobvengers.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobvengers.AppliedApplicantsActivity
import com.example.jobvengers.ApplyJobActivity
import com.example.jobvengers.JobDetails
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.databinding.ItemJobBinding

class JobListingAdapter(
    private val userType:String,
    private val userId:Int,
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

            if (userType == "employer"){
                applyNow.visibility = View.GONE
                cardViewNoOfApplicants.visibility = View.VISIBLE
                textViewNoOfApplicants.text = "No of Applicants: " + data.applied_count?.toString()
            }

            textViewJobType.text = data.title
            textViewLocation.text = data.location
            jobDescription.text = data.description
            textViewSalary.text = data.salary

            cardViewJob.setOnClickListener {
                val intent = Intent(cardViewJob.context, JobDetails::class.java)
                intent.putExtra("Jobs", data)
                cardViewJob.context.startActivity(intent)
            }

            applyNow.setOnClickListener {
                val intent = Intent(cardViewJob.context, ApplyJobActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("JobID", data.job_id)
                cardViewJob.context.startActivity(intent)
            }

            seeAllApplicants.setOnClickListener {
                val intent = Intent(seeAllApplicants.context, AppliedApplicantsActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("JobID", data.job_id)
                seeAllApplicants.context.startActivity(intent)
            }

        }


    }


    override fun getItemCount() = dataList.size
}
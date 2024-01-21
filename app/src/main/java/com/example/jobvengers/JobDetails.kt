package com.example.jobvengers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.databinding.ActivityJobDetailsBinding

@Suppress("DEPRECATION")
class JobDetails : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding
    private var jobDetail: Jobs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobDetail = intent.getParcelableExtra("Jobs")

        binding.apply {
            JobRole.text = jobDetail?.title
            JobDesignation.text = jobDetail?.designation
            Salary.text = jobDetail?.salary
            editJobDetails.text = jobDetail?.description

            btnApplyNowFragment.setOnClickListener {

            }
        }

    }
}
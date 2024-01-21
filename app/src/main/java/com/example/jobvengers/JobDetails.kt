package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.databinding.ActivityJobDetailsBinding

@Suppress("DEPRECATION")
class JobDetails : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding
    private var jobDetail: Jobs? = null
    private lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)

        jobDetail = intent.getParcelableExtra("Jobs")

        binding.apply {


            if (appPreferences.getUserType() == "employer"){
                btnApplyNowFragment.visibility = View.GONE
            }

            JobRole.text = jobDetail?.title
            JobDesignation.text = jobDetail?.designation
            Salary.text = jobDetail?.salary
            editJobDetails.text = jobDetail?.description

            btnApplyNowFragment.setOnClickListener {
                val intent = Intent(this@JobDetails, ApplyJobActivity::class.java)
                intent.putExtra("userId", appPreferences.getUserId().toInt())
                intent.putExtra("JobID", jobDetail?.job_id)
                startActivity(intent)
            }
        }

    }
}
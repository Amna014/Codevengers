package com.example.jobvengers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivityApplyJobBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplyJobActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityApplyJobBinding
    private lateinit var appPreferences: AppPreferences
    private var jobId: String? = ""
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplyJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobId = intent.getStringExtra("JobID")
        appPreferences = AppPreferences(this)

        showToast(jobId.toString())
        showToast(appPreferences.getUserId().toString())

        binding.apply {

           btnApplyNowFragment.setOnClickListener {

                val editEmail = editEmail.text.toString()
                val editPhone = editPhoneNO.text.toString()
                val experience = editTextExperience.text.toString()
                val salary = editTextExpectedSalary.text.toString()

                if (editEmail.isEmpty()) {
                    showToast("Email cannot be empty")
                } else if (editPhone.isEmpty()) {
                    showToast("Experience cannot be empty")
                } else if (experience.isEmpty()) {
                    showToast("Designation cannot be empty")
                } else if (salary.isEmpty()) {
                    showToast("Salary cannot be empty")
                } else {
                    applyJob(editEmail, experience, editPhone, salary)
                }
            }
        }


    }

    private fun applyJob(
        editEmail: String,
        experience: String,
        editPhone: String,
        salary: String
    ) {
        val data = ApiRequest(
            action = "APPLY_JOB",
            job_seeker_id = appPreferences.getUserId().toInt(),
            job_id = jobId?.toInt(),
            email = editEmail,
            experience = experience,
            phone_no = editPhone,
            expected_salary = salary,
            cv = "path/to/cv.pdf"
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@ApplyJobActivity)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        Log.d("JobVengerLog", response.body()?.message.toString())
        Log.d("JobVengerLog", response.body()?.responseCode.toString())
        if (response.body()?.responseCode == 200) {
            finish()
        } else {
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}
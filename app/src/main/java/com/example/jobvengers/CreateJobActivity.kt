package com.example.jobvengers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivityCreateJobBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class CreateJobActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityCreateJobBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)


        binding.apply {

            binding.btnCreateJobFragment.setOnClickListener {

                val editField = editField.text.toString()
                val experience = editTextExperience.text.toString()
                val designation = editTextDesignation.text.toString()
                val location = editTextLocation.text.toString()
                val salary = editTextSalary.text.toString()
                val jobDescription = editTextJobDescription.text.toString()

                if (editField.isEmpty()) {
                    showToast("Field cannot be empty")
                } else if (experience.isEmpty()) {
                    showToast("Experience cannot be empty")
                } else if (designation.isEmpty()) {
                    showToast("Designation cannot be empty")
                } else if (location.isEmpty()) {
                    showToast("Location cannot be empty")
                } else if (salary.isEmpty()) {
                    showToast("Salary cannot be empty")
                } else if (jobDescription.isEmpty()) {
                    showToast("Job Description cannot be empty")
                } else {
                    createJob(editField, experience, designation, location, salary, jobDescription)
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createJob(
        editField: String,
        experience: String,
        designation: String,
        location: String,
        salary: String,
        jobDescription: String
    ) {
        val data = ApiRequest(
            action = "CREATE_JOB",
            employer_id = appPreferences.getUserId().toInt(),
            title = editField,
            designation = designation,
            experience_required = experience,
            description = jobDescription,
            salary = salary.toDouble(),
            location = location
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@CreateJobActivity)
    }


    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        Log.d("JobVengerLog",response.body()?.message.toString())
        Log.d("JobVengerLog",response.body()?.responseCode.toString())
        if (response.body()?.responseCode == 200){
            finish()
        }else{
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }

}
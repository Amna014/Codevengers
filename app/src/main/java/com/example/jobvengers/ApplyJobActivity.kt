package com.example.jobvengers

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
    private val PICK_FILE_REQUEST_CODE = 123


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

               imageViewPlusSign.setOnClickListener{
                   openFilePicker()
               }
            }
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileType = getFileType(uri)
                showToast("Selected file type: $fileType")
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }
    private fun getFileType(uri: Uri): String {
        val contentResolver = contentResolver
        val mimeType = contentResolver.getType(uri)

        return when {
            mimeType?.contains("pdf") == true -> "PDF"
            mimeType?.contains("spreadsheetml") == true -> "Excel"
            mimeType?.contains("wordprocessingml") == true -> "Word"
            mimeType?.contains("presentationml") == true -> "PowerPoint"
            else -> "Unknown"
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
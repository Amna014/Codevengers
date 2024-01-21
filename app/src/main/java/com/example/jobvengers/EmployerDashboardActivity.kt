package com.example.jobvengers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobvengers.adapter.JobListingAdapter
import com.example.jobvengers.adapter.UserListingAdapter
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.data.Jobs
import com.example.jobvengers.data.User
import com.example.jobvengers.databinding.ActivityEmployeeDashboardBinding
import com.example.jobvengers.databinding.ActivityEmployerDashboardBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployerDashboardActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityEmployerDashboardBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private lateinit var appPreferences: AppPreferences


    private lateinit var jobAdapter: JobListingAdapter
    private lateinit var userAdapter: UserListingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)

        getAllOurJobs()

        binding.apply {
            btnJobs.setOnClickListener {
                btnUser.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployerDashboardActivity,
                        R.color.gray
                    )
                )
                btnJobs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployerDashboardActivity,
                        R.color.app_color
                    )
                )
                getAllOurJobs()
            }
            btnUser.setOnClickListener {
                btnJobs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployerDashboardActivity,
                        R.color.gray
                    )
                )
                btnUser.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployerDashboardActivity,
                        R.color.app_color
                    )
                )
                getAllApplicants()
            }
            btnHome.setOnClickListener {
            }
            btnChat.setOnClickListener {
                openWhatsAppSendToScreen()
            }
            btnProfile.setOnClickListener {
                appPreferences.clearUserId()
                appPreferences.clearUserType()
                val intent = Intent(this@EmployerDashboardActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            more.setOnClickListener {

            }
            search.doOnTextChanged { text, _, _, _ ->

            }
            btnCreateJob.setOnClickListener {
                val intent = Intent(this@EmployerDashboardActivity, CreateJobActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getAllOurJobs()
    }

    private fun getAllOurJobs() {
        val data = ApiRequest(
            action = "MY_JOBS",
            employer_id = appPreferences.getUserId().toInt()
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@EmployerDashboardActivity)
    }


    private fun getAllApplicants() {
        val data = ApiRequest(
            action = "GET_APPLIED_USERS",
            employer_id = appPreferences.getUserId().toInt()
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@EmployerDashboardActivity)
    }

    private fun setJobRecyclerView(dataList: List<Jobs>?) {
        jobAdapter = JobListingAdapter(dataList ?: arrayListOf())
        binding.recyclerView.adapter = jobAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUserRecyclerView(dataList: List<User>?) {
        userAdapter = UserListingAdapter(dataList ?: arrayListOf())
        binding.recyclerView.adapter = userAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun openWhatsAppSendToScreen() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Whatsapp Not Found", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        Log.d("JobVengerLog", response.body()?.message.toString())
        Log.d("JobVengerLog", response.body()?.responseCode.toString())
        Log.d("JobVengerLog", response.body()?.data.toString())
        if (response.body()?.responseCode == 200) {
            if (response.body()?.jobs?.isNotEmpty() == true) {
                setJobRecyclerView(response.body()?.jobs)
            }
            if (response.body()?.data?.isNotEmpty() == true) {
                setUserRecyclerView(response.body()?.data)
            }
        } else {
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}
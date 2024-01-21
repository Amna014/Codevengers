package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeDashboardActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityEmployeeDashboardBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private lateinit var appPreferences: AppPreferences


    private lateinit var jobAdapter: JobListingAdapter
    private lateinit var userAdapter: UserListingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)


        //setJobRecyclerView(getAllJobs(10))
        getAllJobs()

        binding.apply {
            btnJobs.setOnClickListener {
                btnUser.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployeeDashboardActivity,
                        R.color.gray
                    )
                )
                btnJobs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployeeDashboardActivity,
                        R.color.app_color
                    )
                )
                getAllJobs()
            }
            btnUser.setOnClickListener {
                btnJobs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployeeDashboardActivity,
                        R.color.gray
                    )
                )
                btnUser.setBackgroundColor(
                    ContextCompat.getColor(
                        this@EmployeeDashboardActivity,
                        R.color.app_color
                    )
                )
                getAllUser()
            }
            btnHome.setOnClickListener {
            }
            btnChat.setOnClickListener {
                openWhatsAppSendToScreen()
            }
            btnProfile.setOnClickListener {
                appPreferences.clearUserId()
                appPreferences.clearUserType()
                val intent = Intent(this@EmployeeDashboardActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            more.setOnClickListener {

            }
            search.doOnTextChanged { text, _, _, _ ->

            }
        }

    }

    private fun getAllJobs() {
        val data = ApiRequest(
            action = "GET_ALL_JOBS",
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@EmployeeDashboardActivity)
    }


    private fun getAllUser() {
        val data = ApiRequest(
            action = "GET_ALL_JOB_SEEKERS",
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@EmployeeDashboardActivity)
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
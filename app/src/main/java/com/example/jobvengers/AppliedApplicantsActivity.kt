package com.example.jobvengers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.jobvengers.adapter.UserListingAdapter
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.data.User
import com.example.jobvengers.databinding.ActivityAppliedApplicantsBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppliedApplicantsActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityAppliedApplicantsBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private var jobId: String? = ""
    private lateinit var appPreferences: AppPreferences
    private lateinit var userAdapter: UserListingAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppliedApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobId = intent.getStringExtra("JobID")
        appPreferences = AppPreferences(this)

        getAllAppliedApplicants()

    }

    private fun getAllAppliedApplicants() {
        val data = ApiRequest(
            action = "GET_APPLIED_USERS",
            employer_id = appPreferences.getUserId().toInt(),
            job_id = jobId?.toInt(),
        )
        val response = requestContract.makeApiCall(data)
        response.enqueue(this@AppliedApplicantsActivity)
    }

    private fun setUserRecyclerView(dataList: List<User>?) {
        userAdapter = UserListingAdapter(dataList ?: arrayListOf())
        binding.recyclerView.adapter = userAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        Log.d("JobVengerLog", response.body()?.message.toString())
        Log.d("JobVengerLog", response.body()?.responseCode.toString())
        Log.d("JobVengerLog", response.body()?.applied_users.toString())
        if (response.body()?.responseCode == 200) {
            if (response.body()?.applied_users?.users?.isNotEmpty() == true) {
                setUserRecyclerView(response.body()?.applied_users?.users)
            }
        } else {
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}
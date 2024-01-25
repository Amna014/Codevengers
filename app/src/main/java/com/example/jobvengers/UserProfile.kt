package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivityUserProfileBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfile : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var appPreferences: AppPreferences
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private lateinit var binding: ActivityUserProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPreferences = AppPreferences(this)

        getUserById()

        binding.btnLogoutFragment.setOnClickListener {
            appPreferences.clearUserId()
            appPreferences.clearUserType()
            val intent = Intent(this@UserProfile, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserById() {
        if (appPreferences.getUserType() == "job_seeker"){
            val data = ApiRequest(
                action = "GET_USER_BY_ID",
                user_id = appPreferences.getUserId().toInt(),

                )
            val response = requestContract.makeApiCall(data)
            response.enqueue(this@UserProfile)
        }else{
            binding.Position.visibility = View.GONE
            val data = ApiRequest(
                action = "GET_EMPLOYER_BY_ID",
                user_id = appPreferences.getUserId().toInt(),

                )
            val response = requestContract.makeApiCall(data)
            response.enqueue(this@UserProfile)
        }

    }


    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        Log.d("JobVengerLog", response.body()?.message.toString())
        Log.d("JobVengerLog", response.body()?.responseCode.toString())
        Log.d("JobVengerLog", response.body()?.employee.toString())
        if (response.body()?.responseCode == 200) {
            binding.apply {
                if (response.body()?.employee != null){
                    Name.text = response.body()?.employee?.username
                    Email.text = response.body()?.employee?.email
                }
                if (response.body()?.user != null){
                    Name.text = response.body()?.user?.username
                    Email.text = response.body()?.user?.email
                    Position.text = response.body()?.user?.field_of_interest ?: "Android"
                }


            }
        } else {
            if (!isFinishing) {
                Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        if (!isFinishing) {
            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
        }
    }
}
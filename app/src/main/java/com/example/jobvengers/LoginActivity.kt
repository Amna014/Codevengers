package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivityLoginBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class LoginActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivityLoginBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)

        binding.apply {
            btnLogin.setOnClickListener {
                val enteredEmail = editTextEmail.text.toString()
                val enteredPassword = editTextPassword.text.toString()

                if (enteredEmail.isNotEmpty() && enteredPassword.isNotEmpty()) {
                    val data = ApiRequest(
                        action = "LOGIN",
                        email = enteredEmail,
                        password = enteredPassword
                    )
                    val response = requestContract.makeApiCall(data)
                    response.enqueue(this@LoginActivity)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter both username and password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            signupNow.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpOptionActivity::class.java)
                startActivity(intent)
            }
        }}

     override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
         Log.d("JobVengerLog",response.body()?.message.toString())
         Log.d("JobVengerLog",response.body()?.responseCode.toString())
         if (response.body()?.responseCode == 200){
             appPreferences.saveUserId(response.body()?.user?.id)
             appPreferences.saveUserType(response.body()?.user?.userType.toString())
             Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
             if (appPreferences.getUserId().toInt() > 0 && appPreferences.getUserType() == "job_seeker") {
                 val intent = Intent(this, EmployeeDashboardActivity::class.java)
                 startActivity(intent)
                 finish()
             }
             if (appPreferences.getUserId().toInt() > 0 && appPreferences.getUserType() == "employer") {
                 val intent = Intent(this, EmployerDashboardActivity::class.java)
                 startActivity(intent)
                 finish()
             }
         }else{
             Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
         }
     }

     override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
         Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
     }
 }

package com.example.jobvengers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivitySignUpEmployeeBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpEmployeeActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivitySignUpEmployeeBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnSignupFragment.setOnClickListener {
                if (isValidInput(
                        editTextName.text.toString(),
                        editTextPassword.text.toString(),
                        editTextEmail.text.toString(),
                        editTextInterest.text.toString(),
                        editTetPhoneNumber.text.toString()
                    )
                ) {
                    val data = ApiRequest(
                        action = "REGISTER_JOBSEEKER",
                        username = editTextName.text.toString(),
                        password = editTextPassword.text.toString(),
                        email = editTextEmail.text.toString(),
                        field_Of_interest = editTextInterest.text.toString(),
                        phone_no = editTetPhoneNumber.text.toString(),
                    )
                    val response = requestContract.makeApiCall(data)
                    response.enqueue(this@SignUpEmployeeActivity)
                } else {
                    Toast.makeText(
                        this@SignUpEmployeeActivity,
                        "Invalid input. Please check your entries.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            loginNow.setOnClickListener {
                val intent = Intent(this@SignUpEmployeeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isValidInput(
        username: String,
        password: String,
        email: String,
        fieldOfInterest: String,
        phNo: String
    ): Boolean {
        return username.isNotEmpty() && password.isNotEmpty() && fieldOfInterest.isNotEmpty()  && phNo.isNotEmpty() && isEmailValid(email)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    override fun onResponse(
        call: Call<ApiResponse>, response: Response<ApiResponse>
    ) {
        Log.d("JobVengerLog",response.body()?.message.toString())
        Log.d("JobVengerLog",response.body()?.responseCode.toString())
        if (response.body()?.responseCode == 200){
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignUpEmployeeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}
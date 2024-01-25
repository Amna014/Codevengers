package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import com.example.jobvengers.databinding.ActivitySignUpEmployeerBinding
import com.example.jobvengers.network.IRequestContact
import com.example.jobvengers.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpEmployeerActivity : AppCompatActivity(), Callback<ApiResponse> {

    private lateinit var binding: ActivitySignUpEmployeerBinding
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract: IRequestContact =
        retrofitClient.create(IRequestContact::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpEmployeerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnSignupEmployeerFragment.setOnClickListener {
                if (isValidInput(
                        editTextName.text.toString(),
                        editTextPassword.text.toString(),
                        editTextEmail.text.toString(),
                    )
                ) {
                    val data = ApiRequest(
                        action = "REGISTER_EMPLOYER",
                        username = editTextName.text.toString(),
                        password = editTextPassword.text.toString(),
                        email = editTextEmail.text.toString(),
                    )
                    val response = requestContract.makeApiCall(data)
                    response.enqueue(this@SignUpEmployeerActivity)
                } else {
                    Toast.makeText(
                        this@SignUpEmployeerActivity,
                        "Invalid input. Please check your entries.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            loginNow.setOnClickListener {
                val intent = Intent(this@SignUpEmployeerActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isValidInput(
        username: String,
        password: String,
        email: String,

    ): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()  && isEmailValid(email)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    override fun onResponse(
        call: Call<ApiResponse>, response: Response<ApiResponse>
    ) { Log.d("JobVengerLog",response.body()?.message.toString())
        Log.d("JobVengerLog",response.body()?.responseCode.toString())
        if (response.body()?.responseCode == 200){
            Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignUpEmployeerActivity, LoginActivity::class.java)
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
package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupNow.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.signupNow.id -> {
                val intent = Intent(this, SignUpOptionActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
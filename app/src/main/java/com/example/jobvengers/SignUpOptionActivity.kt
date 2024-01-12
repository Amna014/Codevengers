package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jobvengers.databinding.ActivitySignUpOptionBinding

class SignUpOptionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.employeeCard.setOnClickListener(this)
        binding.employeerCard.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.employeeCard.id -> {
                val intent = Intent(this, SignUpEmployeeActivity::class.java)
                startActivity(intent)
            }
            binding.employeerCard.id -> {
                val intent = Intent(this, SignUpEmployeerActivity::class.java)
                startActivity(intent)
            }
                }
        }

}
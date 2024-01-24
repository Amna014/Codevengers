package com.example.jobvengers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appPreferences = AppPreferences(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (appPreferences.getUserId()
                .toInt() > 0 && appPreferences.getUserType() == "job_seeker"
            ) {
                val intent = Intent(this, EmployeeDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (appPreferences.getUserId()
                .toInt() > 0 && appPreferences.getUserType() == "employer"
            ) {
                val intent = Intent(this, EmployerDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (appPreferences.getUserId().toInt() == 0) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)

    }
}
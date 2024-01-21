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

        Toast.makeText(this, appPreferences.getUserId().toString(), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (appPreferences.getUserId()
                .toInt() > 0 && appPreferences.getUserType() == "job_seeker"
            ) {
                Toast.makeText(this, "Job_seeker", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EmployeeDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (appPreferences.getUserId()
                .toInt() > 0 && appPreferences.getUserType() == "employer"
            ) {
                Toast.makeText(this, "employer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EmployerDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (appPreferences.getUserId().toInt() == 0) {
                Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)

    }
}
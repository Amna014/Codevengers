package com.example.jobvengers


    import android.content.Context
    import android.content.SharedPreferences

    class AppPreferences(context: Context) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        fun saveUserId(userId: String) {
            val editor = sharedPreferences.edit()
            editor.putString("userId", userId)
            editor.apply()
        }

        fun getUserId(): String? {
            return sharedPreferences.getString("userId", null)
        }

        fun clearUserId() {
            val editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.apply()
        }
    }

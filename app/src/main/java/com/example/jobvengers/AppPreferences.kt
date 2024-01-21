package com.example.jobvengers


    import android.content.Context
    import android.content.SharedPreferences

    class AppPreferences(context: Context) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        fun saveUserId(userId: Long?) {
            val editor = sharedPreferences.edit()
            userId?.let { editor.putLong("userId", it) }
            editor.apply()
        }
        fun saveUserType(userType: String) {
            val editor = sharedPreferences.edit()
            editor.putString("userType", userType)
            editor.apply()
        }

        fun getUserId(): Long {
            return sharedPreferences.getLong("userId", 0)
        }

        fun getUserType(): String? {
            return sharedPreferences.getString("userType", null)
        }

        fun clearUserId() {
            val editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.apply()
        }

        fun clearUserType() {
            val editor = sharedPreferences.edit()
            editor.remove("userType")
            editor.apply()
        }
    }

package com.example.jobvengers.data

data class ApiRequest(
    val action: String,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val field_Of_interest: ArrayList<String>? = null,
    val phone_no: String? = null,
)

data class ApiResponse(
    val status: Boolean,
    val responseCode: Int,
    val message: String,
    val userID: Long? = null,
    val username: String? = null,
    val email: String? = null,
)
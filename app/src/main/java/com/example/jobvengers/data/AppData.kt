package com.example.jobvengers.data

data class ApiRequest(
    val action: String,
    val userName: String? = null,
    val password: String? = null,
    val email: String? = null,
    val fieldOfInterest: String? = null,
    val phoneNo: String? = null,
)

data class ApiResponse(
    val status: Boolean,
    val responseCode: Int,
    val message: String,
    val userID: Long? = null,
    val username: String? = null,
    val email: String? = null,
)
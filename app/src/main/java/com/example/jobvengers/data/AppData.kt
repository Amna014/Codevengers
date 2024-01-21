package com.example.jobvengers.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ApiRequest(
    val action: String,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val employer_id: Int? = null,
    val field_Of_interest: ArrayList<String>? = null,
    val phone_no: String? = null,
    val title: String? = null,
    val designation: String? = null,
    val experience_required: String? = null,
    val description: String? = null,
    val location: String? = null,
    val salary: Double? = null,
)

data class ApiResponse(
    val status: Boolean,
    val responseCode: Int,
    val message: String,
    val user: User? = null,
    val jobs: ArrayList<Jobs>? = null,
    val data: ArrayList<User>? = null,
)

@Parcelize
data class Jobs(
    val jobId: String? = null,
    val title: String? = null,
    val location: String? = null,
    val description: String? = null,
    val designation: String? = null,
    val salary: String? = null
) : Parcelable

data class User(
    val id: Long? = null,
    val userType: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone_no: String? = null,
    val field_Of_interest: ArrayList<String>? = null,
)
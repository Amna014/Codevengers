package com.example.jobvengers.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ApiRequest(
    val action: String,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val user_id: Int? = null,
    val employer_id: Int? = null,
    val job_seeker_id: Int? = null,
    val job_id: Int? = null,
    val field_Of_interest: String? = null,
    val phone_no: String? = null,
    val sender_id: Int? = null,
    val receiver_id: Int? = null,
    val title: String? = null,
    val cv: String? = null,
    val designation: String? = null,
    val experience_required: String? = null,
    val experience: String? = null,
    val description: String? = null,
    val location: String? = null,
    val salary: Double? = null,
    val expected_salary: String? = null,
)

data class ApiResponse(
    val status: Boolean,
    val responseCode: Int,
    val message: String? = "Error",
    val user: User? = null,
    val jobs: ArrayList<Jobs>? = null,
    val data: ArrayList<User>? = null,
    val employee: User? = null,
    val applied_users: AppliedUserData,
)

data class AppliedUserData(
    val appliedUsersCount: Int,
    val users: ArrayList<User>? = null,
)

@Parcelize
data class Jobs(
    val job_id: String? = null,
    val title: String? = null,
    val location: String? = null,
    val description: String? = null,
    val designation: String? = null,
    val salary: String? = null,
    val applied_count: Int? = null,
) : Parcelable

data class User(
    val id: Long? = null,
    val job_seeker_id: Long? = null,
    val userType: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone_no: String? = null,
    val field_of_interest: String? = null,
    val connection_status: String? = null,
)
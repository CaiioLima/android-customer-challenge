package com.example.customerchallenge.data.remote.customer.dto

import com.google.gson.annotations.SerializedName

data class CustomerDTO(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("profileImage")
    val profileImage: String?,

    @SerializedName("profileLink")
    val profileLink: String?
)
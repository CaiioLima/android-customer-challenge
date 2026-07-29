package com.example.customerchallenge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CustomersResponseDTO(
    @SerializedName("customers")
    val customers: List<CustomerDTO>?
)
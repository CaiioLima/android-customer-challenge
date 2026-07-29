package com.example.customerchallenge.data.remote.customer.dto

import com.google.gson.annotations.SerializedName

data class CustomersResponseDTO(
    @SerializedName("customers")
    val customers: List<com.example.customerchallenge.data.remote.customer.dto.CustomerDTO>?
)
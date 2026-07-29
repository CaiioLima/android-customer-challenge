package com.example.customerchallenge.data.remote.api

import com.example.customerchallenge.data.remote.dto.CustomersResponseDTO
import retrofit2.http.GET

interface CustomerApi {

    @GET("service.json")
    suspend fun getCustomers(): CustomersResponseDTO
}
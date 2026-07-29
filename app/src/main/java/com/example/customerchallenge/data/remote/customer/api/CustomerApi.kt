package com.example.customerchallenge.data.remote.customer.api

import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import retrofit2.http.GET

interface CustomerApi {

    @GET("service.json")
    suspend fun getCustomers(): com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
}
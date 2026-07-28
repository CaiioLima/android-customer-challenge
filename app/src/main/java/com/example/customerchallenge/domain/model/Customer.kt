package com.example.customerchallenge.domain.model

data class Customer(
    val id: String,
    val name: String,
    val status: String,
    val email: String,
    val phone: String?,
    val profileImage: String?,
    val profileLink: String?
)
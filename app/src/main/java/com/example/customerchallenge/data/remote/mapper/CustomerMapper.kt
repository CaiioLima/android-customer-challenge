package com.example.customerchallenge.data.remote.mapper

import com.example.customerchallenge.data.remote.dto.CustomerDTO
import com.example.customerchallenge.data.remote.dto.CustomersResponseDTO
import com.example.customerchallenge.domain.model.Customer

internal fun CustomerDTO.toDomain(): Customer {
    return Customer(
        id = id.orEmpty(),
        name = name.orEmpty(),
        status = status.orEmpty(),
        email = email.orEmpty(),
        phone = phone,
        profileImage = profileImage,
        profileLink = profileLink
    )
}

internal fun CustomersResponseDTO.toDomain(): List<Customer> {
    return customers
        .orEmpty()
        .map(CustomerDTO::toDomain)
}
package com.example.customerchallenge.data.remote.customer.mapper

import com.example.customerchallenge.data.remote.customer.dto.CustomerDTO
import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import com.example.customerchallenge.domain.model.Customer

internal fun CustomerDTO.toDomain(): Customer {
    return Customer(
        id = id.orEmpty(),
        name = name
            ?.takeIf { it.isNotBlank() }
            ?: UNKNOWN_CUSTOMER_NAME,
        status = status.orEmpty(),
        email = email.orEmpty(),
        phone = phone,
        profileImage = profileImage?.replace(
            oldValue = "/testApp/",
            newValue = "/testApp2026/"
        ),
        profileLink = profileLink
    )
}

internal fun CustomersResponseDTO.toDomain(): List<Customer> {
    return customers
        .orEmpty()
        .map(CustomerDTO::toDomain)
}

private const val UNKNOWN_CUSTOMER_NAME = "Unknown"
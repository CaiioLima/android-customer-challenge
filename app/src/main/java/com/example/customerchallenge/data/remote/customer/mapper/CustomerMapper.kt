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
        profileImage = profileImage.normalizeProfileImageUrl(),
        profileLink = profileLink
    )
}

private fun String?.normalizeProfileImageUrl(): String? {
    if (this == null) return null

    val normalizedRepositoryPath = replace(
        oldValue = "/testApp/",
        newValue = "/testApp2026/"
    )

    return JPEG_IMAGE_NAMES.fold(
        initial = normalizedRepositoryPath
    ) { currentUrl, imageName ->
        currentUrl.replace(
            oldValue = "$imageName.jpg",
            newValue = "$imageName.jpeg"
        )
    }
}

private val JPEG_IMAGE_NAMES = setOf(
    "macaco2",
    "macaco3",
    "macaco4",
    "macaco5",
    "macaco7",
    "macaco8",
    "macaco9"
)

internal fun CustomersResponseDTO.toDomain(): List<Customer> {
    return customers
        .orEmpty()
        .map(CustomerDTO::toDomain)
}

private const val UNKNOWN_CUSTOMER_NAME = "Unknown"
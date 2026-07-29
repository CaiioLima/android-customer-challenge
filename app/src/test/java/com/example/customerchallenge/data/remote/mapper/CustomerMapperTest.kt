package com.example.customerchallenge.data.remote.mapper


import com.example.customerchallenge.data.remote.customer.dto.CustomerDTO
import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.customer.mapper.toDomain
import com.example.customerchallenge.domain.model.Customer
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CustomerMapperTest {

    @Test
    fun `toDomain should map all customer fields`() {
        val dto = CustomerDTO(
            id = "1",
            name = "Caio Lima",
            status = "Active",
            email = "caio@example.com",
            phone = "+55 91 99999-9999",
            profileImage = "https://github.com/newloran2/testApp/imagens/user.jpg",
            profileLink = "https://github.com/caio"
        )

        val result = dto.toDomain()

        val expected = Customer(
            id = "1",
            name = "Caio Lima",
            status = "Active",
            email = "caio@example.com",
            phone = "+55 91 99999-9999",
            profileImage = "https://github.com/newloran2/testApp2026/imagens/user.jpg",
            profileLink = "https://github.com/caio"
        )

        assertEquals(expected, result)
    }

    @Test
    fun `toDomain should use unknown when name is null`() {
        val dto = createCustomerDTO(
            name = null
        )

        val result = dto.toDomain()

        assertEquals("Unknown", result.name)
    }

    @Test
    fun `toDomain should use unknown when name is blank`() {
        val dto = createCustomerDTO(
            name = "   "
        )

        val result = dto.toDomain()

        assertEquals("Unknown", result.name)
    }

    @Test
    fun `toDomain should map nullable required fields to empty string`() {
        val dto = CustomerDTO(
            id = null,
            name = "Customer",
            status = null,
            email = null,
            phone = null,
            profileImage = null,
            profileLink = null
        )

        val result = dto.toDomain()

        assertEquals("", result.id)
        assertEquals("", result.status)
        assertEquals("", result.email)
    }

    @Test
    fun `toDomain should preserve nullable optional fields`() {
        val dto = CustomerDTO(
            id = "1",
            name = "Customer",
            status = "Active",
            email = "customer@example.com",
            phone = null,
            profileImage = null,
            profileLink = null
        )

        val result = dto.toDomain()

        assertEquals(null, result.phone)
        assertEquals(null, result.profileImage)
        assertEquals(null, result.profileLink)
    }

    @Test
    fun `response toDomain should map all customers`() {
        val response = CustomersResponseDTO(
            customers = listOf(
                createCustomerDTO(
                    id = "1",
                    name = "Customer One"
                ),
                createCustomerDTO(
                    id = "2",
                    name = "Customer Two"
                )
            )
        )

        val result = response.toDomain()

        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Customer One", result[0].name)
        assertEquals("2", result[1].id)
        assertEquals("Customer Two", result[1].name)
    }

    @Test
    fun `response toDomain should return empty list when customers is null`() {
        val response = CustomersResponseDTO(
            customers = null
        )

        val result = response.toDomain()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `response toDomain should return empty list when customers is empty`() {
        val response = CustomersResponseDTO(
            customers = emptyList()
        )

        val result = response.toDomain()

        assertTrue(result.isEmpty())
    }

    private fun createCustomerDTO(
        id: String? = "1",
        name: String? = "Customer",
        status: String? = "Active",
        email: String? = "customer@example.com",
        phone: String? = "+55 91 99999-9999",
        profileImage: String? = null,
        profileLink: String? = "https://example.com/customer"
    ): CustomerDTO {
        return CustomerDTO(
            id = id,
            name = name,
            status = status,
            email = email,
            phone = phone,
            profileImage = profileImage,
            profileLink = profileLink
        )
    }
}
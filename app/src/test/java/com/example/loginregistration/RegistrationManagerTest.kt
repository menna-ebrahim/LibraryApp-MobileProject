package com.example.loginregistration


import com.example.loginregistration.AuthManger.RegistrationManager
import com.example.loginregistration.Database.Auth
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.argumentCaptor
class RegistrationManagerTest {


    @Mock
    lateinit var mockAuthRepository: Auth

    lateinit var registrationManager: RegistrationManager

    @Before
    fun setup() {

        MockitoAnnotations.openMocks(this)
        registrationManager = RegistrationManager(mockAuthRepository)

    }

    @Test
    fun `empty fields returns error message`() {

        registrationManager.validateAndRegister("", "", "") { result ->
            assertEquals("Please fill all fields", result)
        }

    }

    @Test
    fun `password mismatch returns error message`() {

        registrationManager.validateAndRegister("test@test.com", "123456", "000000") { result ->
            assertEquals("Password and Confirm mismatch", result)
        }

    }

    @Test
    fun `valid input calls createUser in repository`() {

        registrationManager.validateAndRegister("valid@test.com",
            "123456", "123456") { result -> }

        verify(mockAuthRepository).createUser(eq("valid@test.com"),
            eq("123456"), any())

    }

}
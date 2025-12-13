package com.example.loginregistration


import com.example.loginregistration.AuthManger.LoginManager
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


class LoginManagerTest {

    @Mock
    lateinit var mockAuthRepository: Auth

    lateinit var loginManager: LoginManager

    @Before
    fun setup() {

        MockitoAnnotations.openMocks(this)
        loginManager = LoginManager(mockAuthRepository)

    }

    @Test
    fun `empty email or password returns fill fields error`() {

        loginManager.validateAndLogin("", "123456") { result ->
            assertEquals("Please fill all fields", result)
        }

    }

    @Test
    fun `login failure returns failed message`() {

        val callbackCaptor = argumentCaptor<(String?) -> Unit>()

        loginManager.validateAndLogin("user@123.com", "123456789") { result ->
            assertEquals("Login Failed", result)
        }

        verify(mockAuthRepository).loginUser(any(), any(), callbackCaptor.capture())
        callbackCaptor.firstValue.invoke(null)

    }

    @Test
    fun `admin login returns AdminSuccess`() {

        val loginCallback = argumentCaptor<(String?) -> Unit>()
        val roleCallback = argumentCaptor<(String?) -> Unit>()

        loginManager.validateAndLogin("admin@test.com", "123456") { result ->
            assertEquals("AdminSuccess", result)
        }

        verify(mockAuthRepository).loginUser(any(), any(), loginCallback.capture())
        loginCallback.firstValue.invoke("someUserId")

        verify(mockAuthRepository).getUserRole(eq("someUserId"), roleCallback.capture())
        roleCallback.firstValue.invoke("Admin")

    }

    @Test
    fun `regular user login returns UserSuccess`() {

        val loginCallback = argumentCaptor<(String?) -> Unit>()
        val roleCallback = argumentCaptor<(String?) -> Unit>()

        loginManager.validateAndLogin("user@test.com", "123456") { result ->
            assertEquals("UserSuccess", result)
        }

        verify(mockAuthRepository).loginUser(any(), any(), loginCallback.capture())
        loginCallback.firstValue.invoke("someUserId")

        verify(mockAuthRepository).getUserRole(eq("someUserId"), roleCallback.capture())
        roleCallback.firstValue.invoke("User")

    }


}
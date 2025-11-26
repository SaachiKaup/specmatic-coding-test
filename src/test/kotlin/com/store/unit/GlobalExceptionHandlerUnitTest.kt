package com.store.unit

import com.store.helper.GlobalExceptionHandler
import com.store.helper.badRequestResponse
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerUnitTest {

    private lateinit var handler: GlobalExceptionHandler
    private lateinit var mockRequest: HttpServletRequest

    @BeforeEach
    fun setup() {
        handler = GlobalExceptionHandler()
        mockRequest = mock()
    }

    @Test
    fun `handleInvalidJson should return 400 with correct message`() {
        val exception = mock<HttpMessageNotReadableException>()

        val response = handler.handleInvalidJson(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("Bad Request - Invalid JSON", response.body?.error)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `handleValidationException should return 400 with correct message`() {
        val exception = mock<MethodArgumentNotValidException>()

        val response = handler.handleValidationException(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("Bad Request - Arguments Invalid", response.body?.error)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `badRequestResponse should create proper error response`() {
        val response = badRequestResponse("Test Error Message")

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("Test Error Message", response.body?.error)
        assertEquals("/products", response.body?.path)
        assertNotNull(response.body?.timestamp)
    }
}

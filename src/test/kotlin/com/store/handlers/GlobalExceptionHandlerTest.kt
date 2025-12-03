package com.store.handlers
import com.store.models.ErrorResponseBody
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private var handler: GlobalExceptionHandler = GlobalExceptionHandler()
    private var mockRequest: HttpServletRequest = mock()

    @Test
    fun `handleInvalidJson should return 400 with correct message`() {
        val exception = mock<HttpMessageNotReadableException>()
        val response = handler.handleInvalidJson(exception, mockRequest)
        validateResponse("Bad Request - Invalid JSON", response)
    }

    @Test
    fun `handleValidationException should return 400 with correct message`() {
        val exception = mock<MethodArgumentNotValidException>()
        val response = handler.handleValidationException(exception, mockRequest)
        validateResponse("Bad Request - Arguments Invalid", response)
    }

    @Test
    fun `badRequestResponse should create proper error response`() {
        val expectedErrorMessage = "Test Error Message"
        val response = badRequestResponse(expectedErrorMessage)
        validateResponse(expectedErrorMessage, response)
    }

    private fun validateResponse(
        expectedErrorMessage: String,
        response: ResponseEntity<ErrorResponseBody>
    ) {
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals(expectedErrorMessage, response.body?.error)
        assertEquals("/products", response.body?.path)
        assertNotNull(response.body?.timestamp)
    }
}

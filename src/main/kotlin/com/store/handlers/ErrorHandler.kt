package com.store.helper

import com.store.models.ErrorResponseBody
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import jakarta.servlet.http.HttpServletRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Error Response Helper Function
fun badRequestResponse(message: String = "Bad Request"): ResponseEntity<ErrorResponseBody> {
    val error = ErrorResponseBody(
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        status = 400,
        error = message,
        path = "/products"
    )
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
}

// Global Exception Handler
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseBody> {
        return badRequestResponse("Bad Request - Invalid JSON")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseBody> {
        return badRequestResponse("Bad Request - Arguments Invalid")
    }
}

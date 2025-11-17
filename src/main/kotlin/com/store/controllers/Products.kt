package com.store.controllers

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import jakarta.servlet.http.HttpServletRequest

// Models
enum class ProductType { book, food, gadget, other }

data class ProductDetails(
    val name: String,
    val type: ProductType,
    val inventory: Int
)

data class ProductId(val id: Int)

data class Product(
    val id: Int,
    @JsonUnwrapped
    val details: ProductDetails
)

data class ErrorResponseBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val path: String
)

// Helper function
fun badRequestResponse(path: String, message: String = "Bad Request"): ResponseEntity<ErrorResponseBody> {
    val error = ErrorResponseBody(
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        status = 400,
        error = message,
        path = path
    )
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
}

// Exception Handler for invalid JSON/enum values
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseBody> {
        return badRequestResponse(request.requestURI)
    }
}

// Controller
@RestController
class Products {
    private val products = mutableListOf<Product>()
    private var nextId = 1

    @PostMapping("/products")
    fun createProduct(
        @RequestBody productDetails: ProductDetails,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        // Manual validation for blank name
        if (productDetails.name.isBlank()) {
            return badRequestResponse(request.requestURI)
        }

        // Manual validation for inventory bounds
        if (productDetails.inventory < 1 || productDetails.inventory > 9999) {
            return badRequestResponse(request.requestURI)
        }

        val id = nextId++
        val product = Product(id, productDetails)
        products.add(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(id))
    }

    @GetMapping("/products")
    fun getProducts(@RequestParam type: ProductType): List<Product> {
        return products.filter { it.details.type == type }
    }
}

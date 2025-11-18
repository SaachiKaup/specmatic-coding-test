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
import org.springframework.web.bind.MethodArgumentNotValidException
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.DecimalMax

// Models
enum class ProductType { book, food, gadget, other }

data class ProductDetails(
    @field:NotBlank(message = "Name is required and cannot be blank")
    val name: String,

    @field:NotNull(message = "Type is required")
    val type: ProductType,

    @field:NotNull(message = "Inventory is required")
    @field:Min(value = 1, message = "Inventory must be at least 1")
    @field:Max(value = 9999, message = "Inventory must be at most 9999")
    val inventory: Int,

    @field:NotNull(message = "Cost is required")
    @field:DecimalMin(value = "0.01", message = "Cost must be at least 0.01")
    @field:DecimalMax(value = "999999.99", message = "Cost must be at most 999999.99")
    val cost: Double 

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

// Controller
@RestController
class Products {
    private val products = mutableListOf<Product>()
    private var nextId = 1

    @PostMapping("/products")
    fun createProduct(
        @Valid @RequestBody productDetails: ProductDetails,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        // Log incoming request for debugging
        println("DEBUG: Received POST /products with: name='${productDetails.name}', type=${productDetails.type}, inventory=${productDetails.inventory}")

        // Manual validation for blank name
        if (productDetails.name.isBlank()) {
            println("DEBUG: Rejecting - name is blank")
            return badRequestResponse(request.requestURI)
        }

        // Manual validation for inventory bounds
        if (productDetails.inventory < 1 || productDetails.inventory > 9999) {
            println("DEBUG: Rejecting - inventory out of bounds: ${productDetails.inventory}")
            return badRequestResponse(request.requestURI)
        }

        println("DEBUG: Received POST /products with: name='${productDetails.name}', type=${productDetails.type}, 
  inventory=${productDetails.inventory}, cost=${productDetails.cost}")

        val id = nextId++
        val product = Product(id, productDetails)
        products.add(product)
        println("DEBUG: Created product with id=$id")
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(id))
    }

    @GetMapping("/products")
    fun getProducts(@RequestParam(required = false) type: ProductType?): List<Product> {
        return if (type != null) {
            products.filter { it.details.type == type }
        } else {
            products
        }
    }
}

// Global exception handler
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException :: class)
    fun handleInvalidJson(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseBody> {
        println("DEBUG: Caught HttpMessageNotReadableException: ${ex.message}")
        return badRequestResponse(request.requestURI, "Bad Request")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseBody> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        println("DEBUG: Validation failed: $errors")
        return badRequestResponse(request.requestURI, "Bad Request")
    }
}

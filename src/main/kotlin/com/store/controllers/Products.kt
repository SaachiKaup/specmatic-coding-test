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
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.math.BigDecimal
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

const val MIN_INVENTORY = 1L
const val MAX_INVENTORY = 9999L
const val MIN_COST = "0.0"
const val MAX_COST = "1000000.0"

data class ProductDetails(
    @field:NotBlank(message = "Name is required and cannot be blank")
    val name: String,

    @field:NotNull(message = "Type is required")
    val type: ProductType,

    @field:NotNull(message = "Inventory is required")
    @field:Min(value = MIN_INVENTORY, message = "Inventory must be at least 1")
    @field:Max(value = MAX_INVENTORY, message = "Inventory must be at most 9999")
    val inventory: Int,

    @field:DecimalMin(value = MIN_COST, message = "Cost must be at least 0.01")
    @field:DecimalMax(value = MAX_COST, message = "Cost must be at most 999999.99")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSetter(nulls = Nulls.FAIL)
    val cost: BigDecimal? = null 
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

fun badRequestResponse(message: String = "Bad Request"): ResponseEntity<ErrorResponseBody> {
    val error = ErrorResponseBody(
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        status = 400,
        error = message,
        path = "/products"
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

        val id = nextId++
        products.add(Product(id, productDetails))

        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(id))
    }

    @GetMapping("/products")
    fun getProducts(@RequestParam(required = false) type: ProductType?): List<Product> {
        if (type != null)
            return products.filter { it.details.type == type }
        return products
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

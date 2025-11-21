package com.store.controllers

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import com.store.helper.badRequestResponse
import com.store.models.Product
import com.store.models.ProductDetails
import com.store.models.ProductId
import com.store.models.ProductType

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

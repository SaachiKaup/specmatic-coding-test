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
import com.store.handlers.ProductService
import com.store.models.Product
import com.store.models.ProductDetails
import com.store.models.ProductId
import com.store.models.ProductType

@RestController
class ProductController(private val productService: ProductService) {

    @PostMapping("/products")
    fun createProduct(
        @Valid @RequestBody productDetails: ProductDetails,
        request: HttpServletRequest
    ): ResponseEntity<ProductId> {
        val productId = productService.createProduct(productDetails)
        return ResponseEntity.status(HttpStatus.CREATED).body(productId)
    }

    @GetMapping("/products")
    fun getProducts(@RequestParam(required = false) type: ProductType?): List<Product> {
        return productService.getProducts(type)
    }
}

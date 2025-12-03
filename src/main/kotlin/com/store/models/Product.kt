package com.store.models

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.DecimalMax
import java.math.BigDecimal

enum class ProductType { book, food, gadget, other }

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
) {
    companion object {
        const val MIN_INVENTORY = 1L
        const val MAX_INVENTORY = 9999L
        const val MIN_COST = "0.0"
        const val MAX_COST = "1000000.0"
    }
}

data class ProductId(val id: Int)

data class Product(
    val id: Int,
    @JsonUnwrapped
    val details: ProductDetails
)

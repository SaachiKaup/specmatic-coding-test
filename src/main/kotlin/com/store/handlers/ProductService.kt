package com.store.handlers

import com.store.models.Product
import com.store.models.ProductDetails
import com.store.models.ProductId
import com.store.models.ProductType
import org.springframework.stereotype.Component
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

@Component
class ProductHandler {
    private val products = Collections.synchronizedList(mutableListOf<Product>())
    private val nextId = AtomicInteger(1)

    @Synchronized
    fun createProduct(productDetails: ProductDetails): ProductId {
        val id = nextId.getAndIncrement()
        val product = Product(id, productDetails)
        products.add(product)
        return ProductId(id)
    }

    fun getProducts(type: ProductType?): List<Product> {
        return if (type != null) {
            products.filter { it.details.type == type }
        } else {
            products.toList()
        }
    }
}

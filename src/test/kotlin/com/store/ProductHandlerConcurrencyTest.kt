package com.store

import com.store.handlers.ProductHandler
import com.store.models.ProductDetails
import com.store.models.ProductType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.concurrent.thread

class ProductHandlerConcurrencyTest {

    @Test
    fun `test concurrent product creation`() {
        val handler = ProductHandler()
        val numberOfThreads = 100
        val productsPerThread = 10
        val threads = mutableListOf<Thread>()

        // Launch multiple threads
        repeat(numberOfThreads) { threadIndex ->
            val thread = thread {
                repeat(productsPerThread) { productIndex ->
                    val productDetails = ProductDetails(
                        name = "Product-$threadIndex-$productIndex",
                        type = ProductType.gadget,
                        inventory = 10
                    )
                    handler.createProduct(productDetails)
                }
            }
            threads.add(thread)
        }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Verify total count
        val allProducts = handler.getProducts(null)
        assertEquals(numberOfThreads * productsPerThread, allProducts.size,
            "Total products should match expected count")

        // Verify all IDs are unique
        val allIds = allProducts.map { it.id }
        val uniqueIds = allIds.toSet()
        assertEquals(allIds.size, uniqueIds.size,
            "All product IDs should be unique")
    }
}

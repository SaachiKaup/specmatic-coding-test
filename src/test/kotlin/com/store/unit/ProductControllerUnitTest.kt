package com.store.unit

import com.store.controllers.ProductController
import com.store.handlers.ProductHandler
import com.store.models.Product
import com.store.models.ProductDetails
import com.store.models.ProductId
import com.store.models.ProductType
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.argumentCaptor
import org.springframework.http.HttpStatus

class ProductControllerUnitTest {

    private lateinit var mockHandler: ProductHandler
    private lateinit var controller: ProductController
    private lateinit var mockRequest: HttpServletRequest

    @BeforeEach
    fun setup() {
        mockHandler = mock()
        controller = ProductController(mockHandler)
        mockRequest = mock()
    }

    @Test
    fun `createProduct should return 201 with ProductId`() {
        val productDetails = ProductDetails("Test Product", ProductType.gadget, 10)
        val expectedProductId = ProductId(123)

        whenever(mockHandler.createProduct(productDetails)).thenReturn(expectedProductId)

        val response = controller.createProduct(productDetails, mockRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedProductId, response.body)
        verify(mockHandler, times(1)).createProduct(productDetails)
    }

    @Test
    fun `createProduct should delegate to handler with correct details`() {
        val productDetails = ProductDetails("Another Product", ProductType.book, 5)
        whenever(mockHandler.createProduct(any())).thenReturn(ProductId(1))

        controller.createProduct(productDetails, mockRequest)

        val captor = argumentCaptor<ProductDetails>()
        verify(mockHandler).createProduct(captor.capture())
        assertEquals("Another Product", captor.firstValue.name)
        assertEquals(ProductType.book, captor.firstValue.type)
        assertEquals(5, captor.firstValue.inventory)
    }

    @Test
    fun `getProducts with no filter should return all products`() {
        val expectedProducts = listOf(
            Product(1, ProductDetails("Product 1", ProductType.gadget, 10)),
            Product(2, ProductDetails("Product 2", ProductType.book, 5))
        )
        whenever(mockHandler.getProducts(null)).thenReturn(expectedProducts)

        val result = controller.getProducts(null)

        assertEquals(expectedProducts, result)
        verify(mockHandler, times(1)).getProducts(null)
    }

    @Test
    fun `getProducts with type filter should pass filter to handler`() {
        val expectedProducts = listOf(
            Product(1, ProductDetails("Gadget 1", ProductType.gadget, 10))
        )
        whenever(mockHandler.getProducts(ProductType.gadget)).thenReturn(expectedProducts)

        val result = controller.getProducts(ProductType.gadget)

        assertEquals(expectedProducts, result)
        verify(mockHandler, times(1)).getProducts(ProductType.gadget)
    }
}

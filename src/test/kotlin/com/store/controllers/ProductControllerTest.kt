package com.store.controllers
import com.store.handlers.ProductService
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

class ProductControllerTest {

    private lateinit var mockService: ProductService
    private lateinit var controller: ProductController
    private lateinit var mockRequest: HttpServletRequest

    @BeforeEach
    fun setup() {
        mockService = mock()
        controller = ProductController(mockService)
        mockRequest = mock()
    }

    @Test
    fun `createProduct should return 201 with ProductId`() {
        val productDetails = ProductDetails("Test Product", ProductType.gadget, 10)
        val expectedProductId = ProductId(123)

        whenever(mockService.createProduct(productDetails)).thenReturn(expectedProductId)

        val response = controller.createProduct(productDetails, mockRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedProductId, response.body)
        verify(mockService, times(1)).createProduct(productDetails)
    }

    @Test
    fun `createProduct should delegate to service with correct details`() {
        val productDetails = ProductDetails("Another Product", ProductType.book, 5)
        whenever(mockService.createProduct(any())).thenReturn(ProductId(1))

        controller.createProduct(productDetails, mockRequest)

        val captor = argumentCaptor<ProductDetails>()
        verify(mockService).createProduct(captor.capture())
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
        whenever(mockService.getProducts(null)).thenReturn(expectedProducts)

        val result = controller.getProducts(null)

        assertEquals(expectedProducts, result)
        verify(mockService, times(1)).getProducts(null)
    }

    @Test
    fun `getProducts with type filter should pass filter to service`() {
        val expectedProducts = listOf(
            Product(1, ProductDetails("Gadget 1", ProductType.gadget, 10))
        )
        whenever(mockService.getProducts(ProductType.gadget)).thenReturn(expectedProducts)

        val result = controller.getProducts(ProductType.gadget)

        assertEquals(expectedProducts, result)
        verify(mockService, times(1)).getProducts(ProductType.gadget)
    }
}

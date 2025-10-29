package pl.lazypizza.data.repository

import pl.lazypizza.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun getProductsByCategory(category: String): List<Product>
    suspend fun searchProducts(query: String): List<Product>
}
package pl.lazypizza.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lazypizza.domain.model.Product
import pl.lazypizza.domain.model.ProductCategory
import pl.lazypizza.domain.model.Topping

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun getProductsByCategory(category: ProductCategory): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getToppings(): List<Topping>
}
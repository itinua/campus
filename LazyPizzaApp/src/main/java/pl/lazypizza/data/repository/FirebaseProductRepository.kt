package pl.lazypizza.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.lazypizza.domain.model.Product
import pl.lazypizza.domain.model.ProductCategory
import pl.lazypizza.domain.model.Topping

class FirebaseProductRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val subscription = firestore.collection(PRODUCTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    launch {
                        val products = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Product::class.java)
                        }
                        trySend(products)
                    }
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getProductById(id: String): Product? {
        return try {
            val document = firestore.collection(PRODUCTS_COLLECTION)
                .document(id)
                .get()
                .await()

            document.toObject(Product::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getProductsByCategory(category: ProductCategory): List<Product> {
        return try {
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", category)
                .get()
                .await()

            val products = mutableListOf<Product>()
            products.filter { it.category == category }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchProducts(query: String): List<Product> {
        return try {
            val allProducts = firestore.collection(PRODUCTS_COLLECTION)
                .get()
                .await()
                .documents.let { docs ->
                    val products = mutableListOf<Product>()
                    for (doc in docs) {
                        val product = doc.toObject(Product::class.java)
                        product?.let {
                            products.add(product)
                        }
                    }
                    products
                }

            allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getToppings(): List<Topping> {
        return try {
            // Get products with category "Toppings" and convert them to Topping objects
            val toppingProducts = getProductsByCategory(ProductCategory.DRINKS)

            toppingProducts.map { product ->
                Topping(
                    name = product.name,
                    price = product.price,
                    imageUrl = product.image,
                )
            }
        } catch (e: Exception) {
            println("LazyPizza: Error getting toppings: ${e.message}")
            emptyList()
        }
    }

    suspend fun getImageUrl(storageUrl: String): String {
        return try {
            storage.getReferenceFromUrl(storageUrl).downloadUrl.await().toString()
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        private const val PRODUCTS_COLLECTION = "products"
    }
}
package pl.lazypizza.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.lazypizza.domain.model.Product
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
                            val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                            product?.let {
                                // Convert Firebase Storage reference URL to download URL
                                val imageUrl = when {
                                    it.image.startsWith("gs://") -> {
                                        try {
                                            storage.getReferenceFromUrl(it.image)
                                                .downloadUrl.await().toString()
                                        } catch (e: Exception) {
                                            it.image
                                        }
                                    }
                                    it.imageUrl.startsWith("gs://") -> {
                                        try {
                                            storage.getReferenceFromUrl(it.imageUrl)
                                                .downloadUrl.await().toString()
                                        } catch (e: Exception) {
                                            it.imageUrl
                                        }
                                    }
                                    it.imageUrl.isNotEmpty() -> it.imageUrl
                                    it.image.isNotEmpty() -> it.image
                                    else -> ""
                                }
                                it.copy(imageUrl = imageUrl)
                            }
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
            
            val product = document.toObject(Product::class.java)?.copy(id = document.id)
            product?.let {
                // Convert Firebase Storage reference URL to download URL
                val imageUrl = when {
                    it.image.startsWith("gs://") -> {
                        try {
                            storage.getReferenceFromUrl(it.image)
                                .downloadUrl.await().toString()
                        } catch (e: Exception) {
                            it.image
                        }
                    }
                    it.imageUrl.startsWith("gs://") -> {
                        try {
                            storage.getReferenceFromUrl(it.imageUrl)
                                .downloadUrl.await().toString()
                        } catch (e: Exception) {
                            it.imageUrl
                        }
                    }
                    it.imageUrl.isNotEmpty() -> it.imageUrl
                    it.image.isNotEmpty() -> it.image
                    else -> ""
                }
                it.copy(imageUrl = imageUrl)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", category)
                .get()
                .await()
            
            val products = mutableListOf<Product>()
            for (doc in snapshot.documents) {
                val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                product?.let {
                    // Convert Firebase Storage reference URL to download URL
                    val imageUrl = when {
                        it.image.startsWith("gs://") -> {
                            try {
                                storage.getReferenceFromUrl(it.image)
                                    .downloadUrl.await().toString()
                            } catch (e: Exception) {
                                it.image
                            }
                        }
                        it.imageUrl.startsWith("gs://") -> {
                            try {
                                storage.getReferenceFromUrl(it.imageUrl)
                                    .downloadUrl.await().toString()
                            } catch (e: Exception) {
                                it.imageUrl
                            }
                        }
                        it.imageUrl.isNotEmpty() -> it.imageUrl
                        it.image.isNotEmpty() -> it.image
                        else -> ""
                    }
                    products.add(it.copy(imageUrl = imageUrl))
                }
            }
            products.filter { it.category.equals(category, ignoreCase = true) }
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
                        val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                        product?.let {
                            // Convert Firebase Storage reference URL to download URL
                            val imageUrl = when {
                                it.image.startsWith("gs://") -> {
                                    try {
                                        storage.getReferenceFromUrl(it.image)
                                            .downloadUrl.await().toString()
                                    } catch (e: Exception) {
                                        it.image
                                    }
                                }
                                it.imageUrl.startsWith("gs://") -> {
                                    try {
                                        storage.getReferenceFromUrl(it.imageUrl)
                                            .downloadUrl.await().toString()
                                    } catch (e: Exception) {
                                        it.imageUrl
                                    }
                                }
                                it.imageUrl.isNotEmpty() -> it.imageUrl
                                it.image.isNotEmpty() -> it.image
                                else -> ""
                            }
                            products.add(it.copy(imageUrl = imageUrl))
                        }
                    }
                    products
                }
            
            allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.ingredients.any { it.contains(query, ignoreCase = true) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getToppings(): List<Topping> {
        return try {
            // Get products with category "Toppings" and convert them to Topping objects
            val toppingProducts = getProductsByCategory("Toppings")
            
            toppingProducts.map { product ->
                Topping(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    image = product.image,
                    available = product.isAvailable
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
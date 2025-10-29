package pl.lazypizza.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "Pizza",
    val imageUrl: String = "",
    val image: String = "", // Alternative field name for Firebase
    val ingredients: List<String> = emptyList(),
    val isAvailable: Boolean = true
) {
    fun getCategoryEnum(): ProductCategory {
        return ProductCategory.fromString(category)
    }
}

@Serializable
enum class ProductCategory(val displayName: String, val firebaseValue: String) {
    PIZZA("Pizza", "Pizza"),
    DRINKS("Drinks", "Drink"),
    SAUCES("Sauces", "Sauce"),
    ICE_CREAM("Ice Cream", "IceCream");
    
    companion object {
        fun fromString(value: String): ProductCategory {
            return values().find {
                it.firebaseValue.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            } ?: PIZZA
        }
    }
}
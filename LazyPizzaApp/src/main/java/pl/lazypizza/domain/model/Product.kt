package pl.lazypizza.domain.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id:String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,

    @get:PropertyName("category")
    @set:PropertyName("category")
    var _category: String = "",

    val image: String = ""
) {
    @get:Exclude
    @set:Exclude
    var category: ProductCategory
       get() = ProductCategory.fromFirebaseValue(_category) ?: ProductCategory.NONE
        set(value) {
            _category = value.firebaseValue
        }
}


@Serializable
enum class ProductCategory(val displayName: String, val firebaseValue: String) {
    NONE("NONE", ""),
    PIZZA("Pizza", "Pizza"),
    DRINKS("Drinks", "Drink"),
    SAUCES("Sauces", "Sauce"),
    ICE_CREAM("Ice Cream", "Ice Cream"),
    TOPPINGS("Toppings", "Toppings");

    companion object {
        fun fromFirebaseValue(value: String): ProductCategory? {
            println("fromFirebaseValue $value")
            return entries.find { it.firebaseValue.equals(value, ignoreCase = true) }
        }
    }
}
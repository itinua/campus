package pl.lazypizza.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Topping(
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val image: String = "",
    val available: Boolean = true
)

@Serializable
data class ToppingSelection(
    val topping: Topping,
    val quantity: Int = 0
) {
    val totalPrice: Double
        get() = topping.price * quantity
}
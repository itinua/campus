package pl.lazypizza.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

@Serializable
data class Cart(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val deliveryFee: Double = 0.0
) {
    val total: Double
        get() = subtotal + tax + deliveryFee
    
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
package pl.lazypizza.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Float
        get() = product.price * quantity
}

@Serializable
data class Cart(
    val items: List<CartItem> = emptyList(),
    val subtotal: Float = 0f,
    val tax: Float = 0f,
    val deliveryFee: Float = 0f
) {
    val total: Float
        get() = subtotal + tax + deliveryFee
    
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
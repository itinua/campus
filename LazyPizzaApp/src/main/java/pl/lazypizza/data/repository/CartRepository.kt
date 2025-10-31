package pl.lazypizza.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.lazypizza.domain.model.Cart
import pl.lazypizza.domain.model.CartItem
import pl.lazypizza.domain.model.Product

interface CartRepository {
    val cart: StateFlow<Cart>
    fun addToCart(product: Product)
    fun removeFromCart(product: Product)
    fun updateQuantity(product: Product, quantity: Int)
    fun clearCart()
}

class InMemoryCartRepository : CartRepository {
    private val _cart = MutableStateFlow(Cart())
    override val cart: StateFlow<Cart> = _cart.asStateFlow()

    override fun addToCart(product: Product) {
        _cart.update { currentCart ->
            val existingItem = currentCart.items.find { it.product.name == product.name }
            val updatedItems = if (existingItem != null) {
                currentCart.items.map { item ->
                    if (item.product.name == product.name) {
                        item.copy(quantity = item.quantity + 1)
                    } else item
                }
            } else {
                currentCart.items + CartItem(product = product, quantity = 1)
            }

            calculateCart(updatedItems)
        }
    }

    override fun removeFromCart(product: Product) {
        _cart.update { currentCart ->
            val updatedItems = currentCart.items.filter { it.product.name != product.name }
            calculateCart(updatedItems)
        }
    }

    override fun updateQuantity(product: Product, quantity: Int) {
        _cart.update { currentCart ->
            val updatedItems = if (quantity <= 0) {
                currentCart.items.filter { it.product.name != product.name }
            } else {
                currentCart.items.map { item ->
                    if (item.product.name == product.name) {
                        item.copy(quantity = quantity)
                    } else item
                }
            }
            calculateCart(updatedItems)
        }
    }

    override fun clearCart() {
        _cart.value = Cart()
    }

    private fun calculateCart(items: List<CartItem>): Cart {

        val subtotal = items.sumOf { it.totalPrice.toDouble() }
        val tax = subtotal * TAX_RATE
        val deliveryFee = if (subtotal > FREE_DELIVERY_THRESHOLD) 0.0 else DELIVERY_FEE

        return Cart(
            items = items,
            subtotal = subtotal.toFloat(),
            tax = tax.toFloat(),
            deliveryFee = deliveryFee.toFloat()
        )
    }

    companion object {
        private const val TAX_RATE = 0.08
        private const val DELIVERY_FEE = 2.99
        private const val FREE_DELIVERY_THRESHOLD = 30.0
    }
}
package pl.lazypizza.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.lazypizza.data.repository.CartRepository
import pl.lazypizza.domain.model.Cart
import pl.lazypizza.domain.model.Product

data class CartUiState(
    val cart: Cart = Cart(),
    val isEmpty: Boolean = true
)

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    val uiState: StateFlow<CartUiState> = cartRepository.cart
        .map { cart ->
            CartUiState(
                cart = cart,
                isEmpty = cart.items.isEmpty()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState()
        )

    fun increaseQuantity(product: Product) {
        val currentItem = uiState.value.cart.items.find { it.product.name == product.name }
        currentItem?.let {
            cartRepository.updateQuantity(product, it.quantity + 1)
        }
    }

    fun decreaseQuantity(product: Product) {
        val currentItem = uiState.value.cart.items.find { it.product.name == product.name }
        currentItem?.let {
            if (it.quantity > 1) {
                cartRepository.updateQuantity(product, it.quantity - 1)
            } else {
                cartRepository.removeFromCart(product)
            }
        }
    }

    fun removeFromCart(product: Product) {
        cartRepository.removeFromCart(product)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }
}
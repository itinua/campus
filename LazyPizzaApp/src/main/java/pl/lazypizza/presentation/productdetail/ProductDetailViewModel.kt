package pl.lazypizza.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.lazypizza.data.repository.CartRepository
import pl.lazypizza.data.repository.ProductRepository
import pl.lazypizza.domain.model.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductDetailViewModel(
    private val productId: String,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val product = productRepository.getProductById(productId)
                _uiState.update {
                    it.copy(
                        product = product,
                        isLoading = false,
                        error = if (product == null) "Product not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun increaseQuantity() {
        _uiState.update { state ->
            state.copy(quantity = state.quantity + 1)
        }
    }

    fun decreaseQuantity() {
        _uiState.update { state ->
            state.copy(quantity = maxOf(1, state.quantity - 1))
        }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        repeat(_uiState.value.quantity) {
            cartRepository.addToCart(product)
        }
    }
}
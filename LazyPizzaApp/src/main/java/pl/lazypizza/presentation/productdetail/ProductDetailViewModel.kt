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
import pl.lazypizza.domain.model.ProductCategory

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val toppings: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val basePrice: Double
        get() = product?.price ?: 0.0
    
//    val toppingsPrice: Double
//        get() = selectedToppings.values.sumOf { it.totalPrice }
//
//    val totalPrice: Double
//        get() = (basePrice + toppingsPrice) * quantity
}

class ProductDetailViewModel(
    private val productId: String,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
        loadToppings()
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
    
    private fun loadToppings() {
        viewModelScope.launch {
            try {
                val toppings = productRepository.getProductsByCategory(ProductCategory.TOPPINGS)
                println("toppings ${toppings.size}")

                //toppings.forEach { topping ->
                  //  println("LazyPizza: Topping - ${topping.name}: $${topping.price}")
               // }
                _uiState.update { state ->
                    state.copy(toppings = toppings)
                }
            } catch (e: Exception) {
                println("LazyPizza: Error loading toppings: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun updateToppingQuantity(topping: Product, quantity: Int) {
//        _uiState.update { state ->
//            val updatedToppings = state.selectedToppings.toMutableMap()
//            if (quantity <= 0) {
//                updatedToppings.remove(topping.name)
//            } else {
//                updatedToppings[topping.name] = ToppingSelection(topping, quantity)
//            }
//            state.copy(selectedToppings = updatedToppings)
//        }
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
        // For now, add the base product to cart
        // In a real app, you'd create a custom cart item with toppings
        repeat(_uiState.value.quantity) {
            cartRepository.addToCart(product)
        }
    }
}
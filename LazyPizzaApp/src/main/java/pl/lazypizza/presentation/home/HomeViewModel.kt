package pl.lazypizza.presentation.home

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

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItemCount: Int = 0
)

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        observeCart()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            productRepository.getProducts().collect { products ->
                _uiState.update { state ->
                    state.copy(
                        products = products,
                        filteredProducts = filterProducts(products, state.selectedCategory, state.searchQuery),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cart.collect { cart ->
                _uiState.update { it.copy(cartItemCount = cart.itemCount) }
            }
        }
    }

    fun selectCategory(category: ProductCategory?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredProducts = filterProducts(state.products, category, state.searchQuery)
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredProducts = filterProducts(state.products, state.selectedCategory, query)
            )
        }
    }

    fun addToCart(product: Product) {
        cartRepository.addToCart(product)
    }

    private fun filterProducts(
        products: List<Product>,
        category: ProductCategory?,
        searchQuery: String
    ): List<Product> {
        return products
            .filter { product ->
                (category == null || product.getCategoryEnum() == category) &&
                (searchQuery.isEmpty() ||
                 product.name.contains(searchQuery, ignoreCase = true) ||
                 product.description.contains(searchQuery, ignoreCase = true))
            }
    }
}
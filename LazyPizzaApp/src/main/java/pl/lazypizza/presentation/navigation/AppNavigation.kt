package pl.lazypizza.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import pl.lazypizza.presentation.cart.CartScreen
import pl.lazypizza.presentation.cart.CartViewModel
import pl.lazypizza.presentation.history.HistoryScreen
import pl.lazypizza.presentation.home.HomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val cartViewModel: CartViewModel = koinViewModel()
    val cartUiState by cartViewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                cartItemCount = cartUiState.cart.itemCount
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Menu.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Menu.route) {
                HomeScreen(
                    onProductClick = { product ->
                        // Navigate to product detail if needed
                    }
                )
            }
            
            composable(BottomNavItem.Cart.route) {
                CartScreen(
                    onCheckout = {
                        // Handle checkout
                    }
                )
            }
            
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
        }
    }
}
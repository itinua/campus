package pl.lazypizza.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.viewmodel.koinViewModel
import pl.lazypizza.presentation.cart.CartScreen
import pl.lazypizza.presentation.cart.CartViewModel
import pl.lazypizza.presentation.history.HistoryScreen
import pl.lazypizza.presentation.home.HomeScreen
import pl.lazypizza.presentation.productdetail.ProductDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val cartViewModel: CartViewModel = koinViewModel()
    val cartUiState by cartViewModel.uiState.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = when {
        currentRoute == null -> false
        currentRoute.startsWith("product_detail/") -> false
        else -> true
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    cartItemCount = cartUiState.cart.itemCount
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Menu.route,
            modifier = if (showBottomBar) Modifier.padding(paddingValues) else Modifier
        ) {
            composable(BottomNavItem.Menu.route) {
                HomeScreen(
                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.name}")
                    }
                )
            }
            
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddToCart = {
                        navController.navigate(BottomNavItem.Cart.route)
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
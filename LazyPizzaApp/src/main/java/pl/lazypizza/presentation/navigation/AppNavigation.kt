package pl.lazypizza.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import org.koin.compose.viewmodel.koinViewModel
import pl.lazypizza.presentation.cart.CartScreen
import pl.lazypizza.presentation.cart.CartViewModel
import pl.lazypizza.presentation.history.HistoryScreen
import pl.lazypizza.presentation.home.HomeScreen
import pl.lazypizza.presentation.productdetail.ProductDetailScreen

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    MENU("Menu", Icons.Filled.MenuBook),
    CART("Cart", Icons.Filled.ShoppingCart),
    HISTORY("History", Icons.Filled.History),
}


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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MENU) }


    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = Color.Red
        ),
    )
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    NavigationSuiteScaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                            currentDestination = it;
                        when(it) {
                            AppDestinations.MENU -> navController.navigate(BottomNavItem.Menu.route)
                            AppDestinations.CART -> navController.navigate(BottomNavItem.Cart.route)
                            AppDestinations.HISTORY -> navController.navigate(BottomNavItem.History.route)
                        }


                              },
                    colors = myNavigationSuiteItemColors,
                )
            }
        },
        layoutType = customNavSuiteType
    ) {

        Scaffold() { paddingValues ->
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
}
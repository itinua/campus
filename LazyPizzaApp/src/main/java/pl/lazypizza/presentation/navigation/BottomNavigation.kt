package pl.lazypizza.presentation.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Menu : BottomNavItem(
        route = "menu",
        label = "Menu",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    )
    
    object Cart : BottomNavItem(
        route = "cart",
        label = "Cart",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    )
    
    object History : BottomNavItem(
        route = "history",
        label = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    cartItemCount: Int = 0
) {
    val items = listOf(
        BottomNavItem.Menu,
        BottomNavItem.Cart,
        BottomNavItem.History
    )
    
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { item ->
            BottomNavBarItem(
                item = item,
                currentRoute = currentRoute,
                navController = navController,
                badgeCount = if (item is BottomNavItem.Cart) cartItemCount else 0
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavBarItem(
    item: BottomNavItem,
    currentRoute: String?,
    navController: NavController,
    badgeCount: Int = 0
) {
    val isSelected = currentRoute == item.route
    
    NavigationBarItem(
        selected = isSelected,
        onClick = {
            if (currentRoute != item.route) {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        },
        icon = {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = Color(0xFFFF6B35),
                            contentColor = Color.White
                        ) {
                            Text(badgeCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = if (isSelected) Color(0xFFFF6B35) else Color.Gray
                )
            }
        },
        label = {
            Text(
                text = item.label,
                color = if (isSelected) Color(0xFFFF6B35) else Color.Gray
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFFF6B35),
            unselectedIconColor = Color.Gray,
            selectedTextColor = Color(0xFFFF6B35),
            unselectedTextColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}
package pl.lazypizza.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import pl.lazypizza.domain.model.CartItem

@Composable
fun CartScreen(
    onCheckout: () -> Unit = {},
    viewModel: CartViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        TopBar()
        
        if (uiState.isEmpty) {
            EmptyCartContent()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.cart.items,
                    key = { it.product.name }
                ) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onIncrease = { viewModel.increaseQuantity(cartItem.product) },
                        onDecrease = { viewModel.decreaseQuantity(cartItem.product) },
                        onRemove = { viewModel.removeFromCart(cartItem.product) }
                    )
                }
            }
            
            CheckoutSection(
                cart = uiState.cart,
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
private fun TopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Text(
            text = "Shopping Cart",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyCartContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🛒",
                fontSize = 64.sp
            )
            Text(
                text = "Your cart is empty",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = "Add some delicious items to get started!",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItem.product.image,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$${String.format("%.2f", cartItem.product.price)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                    
                    Text(
                        text = "$${String.format("%.2f", cartItem.totalPrice)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
        }
        
        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = TextAlign.Center
        )
        
        IconButton(
            onClick = onIncrease,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF6B35))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CheckoutSection(
    cart: pl.lazypizza.domain.model.Cart,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${String.format("%.2f", cart.subtotal)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tax",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${String.format("%.2f", cart.tax)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = if (cart.deliveryFee == 0f) "Free" else "$${String.format("%.2f", cart.deliveryFee)}",
                    fontSize = 14.sp,
                    color = if (cart.deliveryFee == 0f) Color(0xFF4CAF50) else Color.Gray
                )
            }
            
            HorizontalDivider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", cart.total)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            }
            
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Proceed to Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
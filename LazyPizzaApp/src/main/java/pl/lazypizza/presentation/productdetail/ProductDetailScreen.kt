package pl.lazypizza.presentation.productdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.lazypizza.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel(parameters = { parametersOf(productId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val toppings = uiState.toppings
    val selectedToppings = uiState.selectedToppings
    val totalPrice = uiState.totalPrice.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Product Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        uiState.product?.let { product ->

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                val adaptiveInfo = currentWindowAdaptiveInfo()
                if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
                        WIDTH_DP_EXPANDED_LOWER_BOUND
                    )
                ) {
                    HorizontalLayout(
                        product,
                        toppings,
                        selectedToppings,
                        viewModel::updateToppingQuantity, totalPrice
                    )
                } else {
                    VerticalLayout(
                        product,
                        toppings,
                        selectedToppings,
                        viewModel::updateToppingQuantity, totalPrice
                    )
                }
            }


        }
    }
}

@Composable
fun HorizontalLayout(
    product: Product,
    toppings: List<Product>,
    selectedToppings: Map<String, Product>,
    onQuantityChanged: (Product, Int) -> Unit,
    price: Float,
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(Modifier.weight(1f)) {
            stickyHeader {
                ProductName(product)
                AddToCart(
                    Modifier.background(MaterialTheme.colorScheme.background),
                    onAddToCart = { }, price
                )
            }
            item {
                ProductImage(product)

                ProductDescription(product)
            }


        }
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn {
                item {
                    AllToppings(toppings, selectedToppings, onQuantityChanged)
                }

            }
        }
    }
}


@Composable
fun VerticalLayout(
    product: Product,
    toppings: List<Product>,
    selectedToppings: Map<String, Product>,
    onQuantityChanged: (Product, Int) -> Unit,
    price: Float
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ProductImage(product)
        }
        stickyHeader {
            ProductName(product)
        }
        item {
            ProductDescription(product)
        }
        item {
            AllToppings(toppings, selectedToppings, onQuantityChanged)

        }
    }

    AddToCart(Modifier, onAddToCart = { }, price)
}


@Composable
private fun ProductDescription(product: Product) {
    Text(
        text = product.description,
        fontSize = 16.sp,
        color = Color.Gray,
        lineHeight = 22.sp
    )
}

@Composable
private fun ProductName(product: Product) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = product.name,
            modifier = Modifier
                .weight(1f),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = product.price.displayPrice(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@SuppressLint("DefaultLocale")
fun Float.displayPrice() = "$${String.format("%.2f", this)}"

@Composable
private fun ProductImage(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = product.image,
            contentDescription = product.name,
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun AllToppings(
    toppings: List<Product>,
    selectedToppings: Map<String, Product>,
    onQuantityChanged: (Product, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ADD EXTRA TOPPINGS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
        if (toppings.isNotEmpty()) {
            ToppingsGrid(
                toppings = toppings,
                selectedToppings = selectedToppings,
                onQuantityChanged = onQuantityChanged
            )
        } else {
            Text(
                text = "No toppings available at the moment",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun AddToCart(modifier: Modifier, onAddToCart: () -> Unit, price: Float) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                onAddToCart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B35)
            )
        ) {
            if (price > 0) {
                Text(
                    text = "Add to Cart for ${price.displayPrice()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Add to Cart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ToppingsGrid(
    toppings: List<Product>,
    selectedToppings: Map<String, Product>,
    onQuantityChanged: (Product, Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Display toppings in a grid that wraps content
        val rows = toppings.chunked(3)
        rows.forEach { rowToppings ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowToppings.forEach { topping ->
                    ToppingCard(
                        topping = topping,
                        selectedToppings = selectedToppings,
                        onQuantityChanged = onQuantityChanged,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add empty spaces if row is not complete
                repeat(3 - rowToppings.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


fun <T> Boolean.ifElse(first: T, second: T): T = if (this) first else second

@Composable
private fun ToppingCard(
    topping: Product,
    selectedToppings: Map<String, Product>,
    onQuantityChanged: (Product, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var isClicked by remember { mutableStateOf(false) }

    var count = selectedToppings[topping.id]?.quantity ?: 0

    println("updatedToppings ${topping.id} $count")
    if (count > 0) {
        isClicked = true
    }

    Card(
        modifier = modifier
            .aspectRatio(0.85f)
            .clickable(onClick = { isClicked = !isClicked }),
        shape = RoundedCornerShape(12.dp),
        border = if (isClicked) BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)) else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors().copy(

            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Topping Image
            AsyncImage(
                model = topping.image,
                contentDescription = topping.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Topping Name
            Text(
                text = topping.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )


            if (isClicked || count > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    OutlinedIconButton(
                        modifier = Modifier.size(30.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            0.5.dp,
                            Color.Gray.copy(alpha = (count > 0).ifElse(0.7f, 0.2f))
                        ),

                        onClick = {
                            if (count > 0)
                                onQuantityChanged(topping, count - 1)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            tint = Color.Gray.copy(
                                alpha =
                                    (count > 0).ifElse(0.7f, 0.2f)
                            ),
                            contentDescription = "-"
                        )
                    }


                    Text(
                        modifier = Modifier.weight(1f),
                        text = "$count",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedIconButton(
                        modifier = Modifier.size(30.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            0.5.dp,
                            Color.Gray.copy(alpha = (count < 3).ifElse(0.7f, 0.2f))
                        ),
                        onClick = {
                            //count = (count + 1).coerceAtMost(3)
                            if(count<3)
                            onQuantityChanged(topping, count + 1)
                        }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = Color.Gray.copy(alpha = (count < 3).ifElse(0.7f, 0.2f)),
                            contentDescription = "-"
                        )
                    }

                }
            } else {
                // Price
                Text(
                    text = topping.price.displayPrice(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
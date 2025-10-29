package pl.lazypizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import pl.lazypizza.ui.theme.AppTheme

data class Product(
    val category: String = "",
    val name: String = "",
    val price: Float = 0f,
    val image: String = "",
    val description: String = "",
)

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
                product.description?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ProductScreen() {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Load data once on composition
    LaunchedEffect(Unit) {
        ProductRepository.getProducts {
            products = it
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}


object ProductRepository {
    fun getProducts(onResult: (List<Product>) -> Unit) {
        Firebase.firestore.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->

                val products = snapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }
                onResult(products)
                println(snapshot)
                println("!!!!!!!!!!!!!!!!!!!!!! OK")
            }
            .addOnFailureListener { e ->
                println("!!!!!!!!!!!!!!!!!!!!!! ${e.message}")
                onResult(emptyList())
            }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                //CampusApp()
                // ProductScreen()

                var text by remember { mutableStateOf("result:") }
                Column {
                    Spacer(modifier = Modifier.height(30.dp))

                    Text("$text")

                    var products by remember { mutableStateOf(emptyList<Product>()) }

//                    remember {
//                        var a = Firebase.auth.signInAnonymously()
//                        a.addOnFailureListener {
//                            text = "fail"
//                        }
//                        a.addOnSuccessListener {
//                            text = "success"
//
//
//                        }
//                    }
                    LaunchedEffect(Unit) {
                        ProductRepository.getProducts { list ->
                            products = list
                        }
                    }
                    LazyColumn {
                        items(products) {
                            println(it.image)

                            Row {
                                var res by remember { mutableStateOf("") }

                                LaunchedEffect(it.image) {

                                    res = Firebase.storage.getReferenceFromUrl(it.image)
                                        .downloadUrl.await().toString()
                                }
                                //if(res.isNotEmpty()){
                                AsyncImage(
                                    model = res,
                                    contentDescription = null,
                                )
                                //  }
                                Text(it.name)
                                Text(it.category)
                                Text(it.price.toString())
                            }
                        }
                    }

                }


            }
        }
    }

}

@PreviewScreenSizes
@Composable
fun CampusApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
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
                    onClick = { currentDestination = it },
                    colors = myNavigationSuiteItemColors,
                )
            }
        },
        layoutType = customNavSuiteType
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android ${currentDestination.label.uppercase()}",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Menu", Icons.Filled.MenuBook),
    FAVORITES("Cart", Icons.Filled.ShoppingCart),
    PROFILE("History", Icons.Filled.History),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        Greeting("Android")
    }
}
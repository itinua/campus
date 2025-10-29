package pl.lazypizzautil

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobInfo
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import java.io.File
import java.io.FileInputStream

data class Product(
    val category: String,
    val name: String,
    val price: Float,
    val image: String,
    val description: String? = null,
)

fun parseDb(): List<Product> {
    val root = "/Users/ivanivanenko/git/pl/LazyPizzaUtil/src/main/resources"

    val db = File(root, "database.txt")
    val divider = "—"
    var category = ""
    val result = mutableListOf<Product>()


    db.readLines().forEach { line ->
        when {
            line.isEmpty() -> {}
            line.startsWith("#") -> {
                category = line.removePrefix("#").trim()
                println("### Category: $category")
            }

            line.contains(divider) -> {
                line.split(divider, limit = 3).run {
                    val productName = get(0).trim()
                    val productPrice = get(1).trim().removePrefix("$").toFloat()
                    val productDescr =
                        if (indices.count() > 2) get(2).trim().removePrefix("Ingredients:")
                            .trim() else ""
                    val img = File(root, "lazy-pizza/$category/$productName.png")

                    require(img.exists()) { "Img $img not found" }

                    val product =
                        Product(
                            category, productName, productPrice,
                            image = img.path,
                            productDescr
                        )
                    result += product
                    println(product)

                }
            }
        }
    }
    return result
}

fun main() {
    val products = parseDb()
    uploadDbForFirebase(products.subList(0, 1))
}


fun uploadDbForFirebase(products: List<Product>) {
    val serviceAccount = object {}.javaClass.getResourceAsStream("/serviceAccountKey.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setStorageBucket("lazzypizza-45597.firebasestorage.app")
        .build()

    FirebaseApp.initializeApp(options)
    val firestore = FirestoreClient.getFirestore()

    val bucket = StorageClient.getInstance().bucket()
    products.forEach { product ->
        val blobInfo =
            BlobInfo.newBuilder(bucket.name, "media/${product.category}/${product.name + ".png"}")
                .setContentType("image/png")
                .build()

        val blob = bucket.storage.create(
            blobInfo,
            FileInputStream(product.image)
        )
        //val downloadUrl =
        //product.imgFile = blob.mediaLink
        val uploadedImage = product.copy(image = blob.mediaLink)

        firestore.collection("products")
            .document("${product.category}-${product.name}")
            .set(uploadedImage).get()

    }

    println("Import Finished")

}



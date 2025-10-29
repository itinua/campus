package pl.lazypizzautil

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit

data class Product(
    val category: String,
    val name: String,
    val price: Float,
    val img: String,
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

                    require(img.exists()){"Img $img not found"}

                    val product =
                        Product(category, productName, productPrice, img.path, productDescr)
                    result += product
                    println(product)

                }
            }
        }
    }
    return result
}

fun main() {
    parseDb()
}


fun main1() {
    System.getProperties().forEach {
        println(it)
    }

    val serviceAccount = object {}.javaClass.getResourceAsStream("/serviceAccountKey.json")

    //val serviceAccount = FileInputStream("LazyPizzaUtil/lazzypizza-45597-firebase-adminsdk-fbsvc-c4516c0eeb.json")

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setStorageBucket("lazzypizza-45597.firebasestorage.app")
        .build()

    FirebaseApp.initializeApp(options)
    val db = FirestoreClient.getFirestore()

    //val p = Product

    val bucket = StorageClient.getInstance().bucket()
    val blobInfo = BlobInfo.newBuilder(bucket.name, "media/7-up.png")

        .setContentType("image/png")
        .build()

    val blob = bucket.storage.create(
        blobInfo,
        FileInputStream("/Users/ivanivanenko/git/pl/media/lazy-pizza/drink/7-up.png")
    )
    blob.toBuilder().setAcl(listOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))).build().update()

    val downloadUrl = blob.mediaLink
    println("Upload image $downloadUrl")


    val options2 = Storage.BlobListOption.prefix("")
    val blobIterable = bucket.list(options2).iterateAll()

    blobIterable.forEach { blob ->
        // The name is the full path, e.g., "media/7-up.png"
        println("Found file: ${blob.name} ${blob.signUrl(7, TimeUnit.DAYS)}")
    }

    println("--- End of List ---")





    println("Import Finished")

}



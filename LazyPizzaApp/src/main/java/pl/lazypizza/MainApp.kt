package pl.lazypizza

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class MainApp : Application(),SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {

        val diskCache = DiskCache.Builder()
            .directory(cacheDir.resolve("image_cache"))
            .maxSizePercent(0.02)
            .build()

        return ImageLoader.Builder(this)
            .components {
                add(UrlModifyingInterceptor())
            }
            .diskCache(diskCache)
            .build()
    }

}

class UrlModifyingInterceptor : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val url = chain.request.data
        val publicUrl = Firebase.storage.getReferenceFromUrl(url.toString())
                                                .downloadUrl.await().toString()

        val newRequest = chain.request.newBuilder()
            .data(publicUrl)
            .build()

        return chain.withRequest(newRequest).proceed()
    }


}
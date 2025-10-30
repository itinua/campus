package pl.lazypizza.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pl.lazypizza.data.repository.CartRepository
import pl.lazypizza.data.repository.FirebaseProductRepository
import pl.lazypizza.data.repository.InMemoryCartRepository
import pl.lazypizza.data.repository.ProductRepository
import pl.lazypizza.presentation.cart.CartViewModel
import pl.lazypizza.presentation.home.HomeViewModel
import pl.lazypizza.presentation.productdetail.ProductDetailViewModel

val firebaseModule = module {
    single { Firebase.firestore }
    single { Firebase.storage }
}

val repositoryModule = module {
    single<ProductRepository> { FirebaseProductRepository(get(), get()) }
    single<CartRepository> { InMemoryCartRepository() }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::ProductDetailViewModel)
}

val networkModule = module {
    single { HttpClientFactory.create(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            firebaseModule,
            repositoryModule,
            viewModelModule,
            networkModule
        )
    }
}
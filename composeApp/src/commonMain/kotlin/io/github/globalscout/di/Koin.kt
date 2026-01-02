package io.github.globalscout.di

import io.github.globalscout.data.CountryRepository
import io.github.globalscout.data.DefaultCountryRepository
import io.github.globalscout.data.network.CountryService
import io.github.globalscout.ui.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::CountryService)
    singleOf(::DefaultCountryRepository).bind<CountryRepository>()
    viewModelOf(::HomeViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule)
}
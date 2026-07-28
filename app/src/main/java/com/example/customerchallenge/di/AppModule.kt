package com.example.customerchallenge.di

import com.example.customerchallenge.data.remote.datasource.CustomerRemoteDataSource
import com.example.customerchallenge.data.repository.CustomerRepositoryImpl
import com.example.customerchallenge.domain.repository.CustomerRepository
import com.example.customerchallenge.domain.usecase.GetCustomersUseCase
import org.koin.dsl.module

val appModule = module {

    //remoteDataSource
    single { CustomerRemoteDataSource(api = get()) }

    //repository
    single<CustomerRepository> { CustomerRepositoryImpl(get()) }

    //usecase
    factory { GetCustomersUseCase(repository = get()) }

}
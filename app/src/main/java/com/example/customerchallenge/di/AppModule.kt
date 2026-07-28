package com.example.customerchallenge.di

import com.example.customerchallenge.data.remote.datasource.CustomerRemoteDataSource
import org.koin.dsl.module

val appModule = module {

    //remoteDataSource
    single{ CustomerRemoteDataSource(api = get()) }

}
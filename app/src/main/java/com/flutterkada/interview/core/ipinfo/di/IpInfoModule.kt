package com.flutterkada.interview.core.ipinfo.di

import com.flutterkada.interview.core.ipinfo.data.repository.IpInfoRepositoryImpl
import com.flutterkada.interview.core.ipinfo.domain.repository.IpInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IpInfoModule {

    @Binds
    @Singleton
    abstract fun bindIpInfoRepository(
        ipInfoRepositoryImpl: IpInfoRepositoryImpl
    ): IpInfoRepository
}

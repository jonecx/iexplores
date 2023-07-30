package com.app.sambaaccesssmb.di

import com.app.sambaaccesssmb.ui.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class FileRepositoryModule {

    @Provides
    fun provideFileRepository(ioDispatcher: CoroutineDispatcher): FileRepository {
        return FileRepository(ioDispatcher)
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}

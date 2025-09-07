package com.jarosz.szymon.nqueens.di

import android.content.Context
import androidx.room.Room
import com.jarosz.szymon.nqueens.data.AppDatabase
import com.jarosz.szymon.nqueens.data.ResultsDao
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.data.ResultsRepositoryImpl
import com.jarosz.szymon.nqueens.domain.Timer
import com.jarosz.szymon.nqueens.domain.TimerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "nQueens.db")
            .build()


    @Provides
    fun provideDao(db: AppDatabase): ResultsDao = db.resultsDao()


    @Provides
    fun provideRepository(dao: ResultsDao): ResultsRepository =
        ResultsRepositoryImpl(dao)
}

@Module
@InstallIn(ViewModelComponent::class)
object TimerModule {
    @Provides
    fun provideTimer(): Timer = TimerImpl(Dispatchers.IO)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher


@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

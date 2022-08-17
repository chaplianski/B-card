package com.chaplianski.bcard.di

import android.content.Context
import androidx.room.Room
import com.chaplianski.bcard.data.repository.UserRepositoryImpl
import com.chaplianski.bcard.data.repository.CardRepositoryImpl
import com.chaplianski.bcard.data.storage.database.CardDatabase
import com.chaplianski.bcard.data.storage.database.CardStorageImpl
import com.chaplianski.bcard.data.storage.database.UserStorageImpl
import com.chaplianski.bcard.data.storage.storage.CardStorage
import com.chaplianski.bcard.data.storage.storage.UserStorage
import com.chaplianski.bcard.domain.repository.UserRepository
import com.chaplianski.bcard.domain.repository.CardRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule() {

    @Singleton
    @Provides
    fun provideCardDao(cardDatabase: CardDatabase) = cardDatabase.cardDao()

    @Singleton
    @Provides
    fun provideCardDatabase(context: Context): CardDatabase =
        Room.databaseBuilder(
            context,
            CardDatabase::class.java,
            "card_db"
        )
            .build()

    @Provides
    fun provideAuthorizationRepository(impl: UserRepositoryImpl): UserRepository = impl
    @Provides
    fun provideCardRepository(impl: CardRepositoryImpl): CardRepository = impl
    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    fun provideBCardStorage(impl: CardStorageImpl): CardStorage = impl
    @Provides
    fun provideUserStorage(impl: UserStorageImpl): UserStorage = impl

}

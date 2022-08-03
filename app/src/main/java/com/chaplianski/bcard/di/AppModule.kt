package com.chaplianski.bcard.di

import android.content.Context
import androidx.room.Room
import com.chaplianski.bcard.data.repository.AuthorizationRepositoryImpl
import com.chaplianski.bcard.data.repository.CardRepositoryImpl
import com.chaplianski.bcard.data.storage.database.CardDatabase
import com.chaplianski.bcard.data.storage.database.CardStorageImpl
import com.chaplianski.bcard.data.storage.storage.CardStorage
import com.chaplianski.bcard.domain.repository.AuthorizationRepository
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
    fun provideAuthorizationRepository(impl: AuthorizationRepositoryImpl): AuthorizationRepository = impl
    @Provides
    fun provideCardRepository(impl: CardRepositoryImpl): CardRepository = impl
//    @Provides
//    fun provideInterestRepository(impl: InterestRepositoryImpl): InterestRepository = impl
//    @Provides
//    fun providePurposeRepository(impl: PurposeRepositoryImpl): PurposeRepository = impl
//    @Provides
//    fun provideTaskRepository(impl: TaskRepositoryImpl): TaskRepository = impl
//    @Provides
//    fun provideRepeatRepository(impl: RepeatRepositoryImpl): RepeatRepository = impl
//    @Provides
//    fun provideCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository = impl
//
//
//
    @Provides
    fun provideBCardStorage(impl: CardStorageImpl): CardStorage = impl
//    @Provides
//    fun provideRepeatStorage(impl: RepeatStorageImpl): RepeatStorage = impl
//    @Provides
//    fun provideCategoryStorage(impl: CategoryStorageImpl): CategoryStorage = impl
//    @Provides
//    fun providePurposeStorage(impl: PurposeStorageImpl): PurposeStorage = impl

}

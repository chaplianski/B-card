package com.chaplianski.bcard.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.modeldto.CardDTO


@Database(
    entities = [
        CardDTO::class,
        ],
    version = 1,
    exportSchema = false
)
//@TypeConverters(PhotoConverters::class)

abstract class CardDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
}


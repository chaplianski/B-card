package com.chaplianski.bcard.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO


@Database(
    entities = [
        CardDTO::class, UserDTO::class
        ],
    version = 1,
    exportSchema = false
)
//@TypeConverters(PhotoConverters::class)

abstract class CardDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
}


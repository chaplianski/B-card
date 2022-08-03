package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cards")
data class CardDTO(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val name: String,
    val photo: String,
    val phone: String,
    val linkedin: String,
    val email: String,
    val speciality: String,
    val location: String
)

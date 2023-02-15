package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val login: String = "",
    val password: String = "",
    val secretQuestion: String = "",
    val secretAnswer: String = ""
)

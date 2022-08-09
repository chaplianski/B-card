package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cards")
data class CardDTO(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val photo: String,
    val phone: String,
    val linkedin: String,
    val email: String,
    val speciality: String,
    val location: String,
    val profilInfo: String,
    val education: String,
    val professionalSkills: String,
    val workExperience: String,
    val reference: String,
    val cardColor: String,
    val strokeColor: String,
    val cornerRound: Float,
    val formPhoto: String
)

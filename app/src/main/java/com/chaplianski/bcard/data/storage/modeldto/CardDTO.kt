package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cards")
data class CardDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val surname: String = "",
    val photo: String = "",
    val phone: String = "",
    val linkedin: String = "",
    val email: String = "",
    val speciality: String = "",
    val organization: String = "",
    val town: String = "",
    val country: String = "",
    val profilInfo: String = "",
    val education: String = "",
    val professionalSkills: String = "",
    val workExperience: String = "",
    val reference: String = "",
    val cardColor: String = "#FFCDD2",
    val strokeColor: String = "#0097A7",
    val cornerRound: Float = 0F,
    val formPhoto: String = "oval"
)

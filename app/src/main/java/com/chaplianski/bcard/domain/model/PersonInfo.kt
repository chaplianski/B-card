package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonInfo(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val surname: String = "",
    val photo: String = "",
    val workPhone: String = "",
    val homePhone: String = "",
    val email: String = "",
    val speciality: String = "",
    val organization: String = "",
    val town: String = "",
    val country: String = ""
): Parcelable

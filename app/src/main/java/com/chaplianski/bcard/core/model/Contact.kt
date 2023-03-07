package com.chaplianski.bcard.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val name: String,
    val surname: String,
    val workPhone: String,
    var isChecked: Boolean
): Parcelable

package com.chaplianski.bcard.core.model

import com.chaplianski.bcard.domain.model.Card


sealed class ContactContent {
    data class Letter(val letter: Char) : ContactContent()
    data class Contact(val card: Card) : ContactContent()
}
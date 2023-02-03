package com.chaplianski.bcard.domain.model


sealed class ContactContent {
    data class Letter(val letter: Char) : ContactContent()
    data class Contact(val card: Card) : ContactContent()
}
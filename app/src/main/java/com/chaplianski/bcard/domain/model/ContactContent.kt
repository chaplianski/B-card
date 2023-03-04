package com.chaplianski.bcard.domain.model

/**
 * Этот класс используется только в view layer ему в домейн делать нечего)
 */
sealed class ContactContent {
    data class Letter(val letter: Char) : ContactContent()
    data class Contact(val card: Card) : ContactContent()
}
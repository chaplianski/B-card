package com.chaplianski.bcard.domain.repository

import com.chaplianski.bcard.domain.model.Card

interface CardRepository {

    fun getCards(): List<Card>
}
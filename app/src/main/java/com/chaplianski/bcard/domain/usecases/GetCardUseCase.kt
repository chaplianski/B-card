package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class GetCardUseCase @Inject constructor(private val cardRepository: CardRepository) {

    fun execute (): List<Card>{
        return cardRepository.getCards()
    }
}
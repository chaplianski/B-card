package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class AddCardUseCase @Inject constructor(private val cardRepository: CardRepository) {

    suspend fun execute(card: Card){
        cardRepository.addCard(card)
    }
}
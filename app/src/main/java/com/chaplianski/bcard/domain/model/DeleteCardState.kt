package com.chaplianski.bcard.domain.model

/**
 * этот класс для чего? нигде не используется
 */
sealed class DeleteCardState{
    object Loading: DeleteCardState()
    class Success(val result: Int): DeleteCardState()
    class Failure(val exception: String): DeleteCardState()
}
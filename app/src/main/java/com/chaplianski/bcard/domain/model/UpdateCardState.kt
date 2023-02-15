package com.chaplianski.bcard.domain.model

sealed class UpdateCardState{
    object Loading: UpdateCardState()
    class Success(val result: Int): UpdateCardState()
    class Failure(val exception: String): UpdateCardState()
}
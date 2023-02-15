package com.chaplianski.bcard.domain.model

sealed class DeleteCardState{
    object Loading: DeleteCardState()
    class Success(val result: Int): DeleteCardState()
    class Failure(val exception: String): DeleteCardState()
}
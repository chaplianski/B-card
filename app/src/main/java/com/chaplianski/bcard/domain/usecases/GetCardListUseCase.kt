package com.chaplianski.bcard.domain.usecases

import android.util.Log
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GetCardListUseCase @Inject constructor(private val cardRepository: CardRepository) {

    fun execute (fieldBySorting: String) = resultOf {
//        Log.d("MyLog", "cardList 3")
        cardRepository.getCardList(fieldBySorting)
    }

    /**
     * Like [runCatching], but with proper coroutines cancellation handling. Also only catches [Exception] instead of [Throwable].
     *
     * Cancellation exceptions need to be rethrown. See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
     */

    inline fun <R> resultOf(block: () -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Like [runCatching], but with proper coroutines cancellation handling. Also only catches [Exception] instead of [Throwable].
     *
     * Cancellation exceptions need to be rethrown. See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
     */
    inline fun <T, R> T.resultOf(block: T.() -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Like [mapCatching], but uses [resultOf] instead of [runCatching].
     */
    inline fun <R, T> Result<T>.mapResult(transform: (value: T) -> R): Result<R> {
        val successResult = getOrNull()
        return when {
            successResult != null -> resultOf { transform(successResult) }
            else -> Result.failure(exceptionOrNull() ?: error("Unreachable state"))
        }
    }
}


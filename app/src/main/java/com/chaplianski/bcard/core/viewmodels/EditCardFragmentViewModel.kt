package com.chaplianski.bcard.core.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.core.graphics.BitmapCompat
import androidx.lifecycle.*
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.domain.usecases.UpdateCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class EditCardFragmentViewModel @Inject constructor(
//    application: Application,
    private val addCardUseCase: AddCardUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase
    ) : ViewModel() {

    val _photoUri = MutableLiveData<String>()
    val photoUri: LiveData<String> get() = _photoUri

    val _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard


    fun getCardData(cardId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val card = getCardUseCase.execute(cardId)
            _currentCard.postValue(card)
        }
    }

    fun addCard(card: Card){
        viewModelScope.launch(Dispatchers.IO) {
        addCardUseCase.execute(card)
        }
    }

    fun updateCard(card: Card){
        viewModelScope.launch(Dispatchers.IO) {
            updateCardUseCase.execute(card)
        }
    }


    fun insertPhoto(uri: Uri, contentResolver: ContentResolver, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var bitmapPhoto = getBitmap(contentResolver, uri)
            val bitmapByteCount: Int? = bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
            val imageFile = File(uri.toString())
            val currentHeight = bitmapPhoto.height.toDouble()
            val currentWidth = bitmapPhoto.width.toDouble()
            if (bitmapPhoto.height > bitmapPhoto.width && bitmapPhoto.width > 600) {
                val cameraCoef = currentHeight / currentWidth
                val height = 600 * cameraCoef
                bitmapPhoto = getResizedBitmap(bitmapPhoto, 600.0, height)
               val bitmapByteCount: Int? =
                    bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
            }

            if (currentHeight < currentWidth && currentHeight > 600) {
                val cameraCoef = (currentWidth / currentHeight)
                val width = 600 * cameraCoef

                bitmapPhoto = getResizedBitmap(bitmapPhoto, width, 600.0)
                val bitmapByteCount: Int? =
                    bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
            }

            val uriAvatar = saveImageInExternalCacheDir(context, bitmapPhoto)

            _photoUri.postValue(uriAvatar)


        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Double, newHeight: Double): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    fun saveImageInExternalCacheDir(context: Context, bitmap: Bitmap): String? {
        val fileName = System.currentTimeMillis().toString()
        val filePath = context.externalCacheDir.toString() + "/" + fileName + ".jpg"
        try {
            val fos = FileOutputStream(File(filePath))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filePath
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}
package com.chaplianski.bcard.core.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.lifecycle.*
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.GetCardListState
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardListUseCase
import ezvcard.VCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class LoadCardsDialogViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardListUseCase: GetCardListUseCase,
) : ViewModel() {


    private var _cardList = MutableLiveData<List<Card>>()
    val cardList: LiveData<List<Card>> get() = _cardList

    private var _allCards = MutableLiveData<List<Card>>()
    val allCards: LiveData<List<Card>> get() = _allCards
    private var _getCardListState = MutableStateFlow<GetCardsState>(GetCardsState.Loading)
    val getCardListState get() = _getCardListState.asStateFlow()

//    private var _cardListFromAccount =
//        MutableStateFlow<CardListFromAccountState>(CardListFromAccountState.Loading)
//    val cardListFromAccount get() = _cardListFromAccount.asStateFlow()
//
//    fun fetchContacts(accountContactPicker: AccountContactsPicker) {
//        viewModelScope.launch {
//            _cardListFromAccount.emit(CardListFromAccountState.Success())
//            accountContactPicker.fetchContacts()
//            accountContactPicker.cardListFromAccount
//                .flowWithLifecycle(lifecycle)
//                .onEach {
//                    when (it){
//                        is AccountContactsPicker.CardListFromAccountState.Success -> {
//                            _cardList.postValue(it)
//                        }
//                        else -> {}
//                    }
//                }
//                .launchIn(viewModelScope)
//
//        }
//    }


    suspend fun getCards(fieldBySorting: String) {
        getCardListUseCase.execute(fieldBySorting)
            .onSuccess { _getCardListState.emit(GetCardsState.Success(it)) }
            .onFailure {  }
    }

//    private fun observeAuthors() {
//        viewModel.authors
//            .flowWithLifecycle(lifecycle)
//            .onEach { allAuthorsAdapter.submitData(it) }
//            .launchIn(lifecycleScope)
//    }


    fun convertVcardToCardList(
        vcardList: List<VCard>,
        contentResolver: ContentResolver,
        context: Context
    ) {

        val cardList = mutableListOf<Card>()
        vcardList.forEach { vcard ->

            var nameValue = ""
            var surnameValue = ""
            var townValue = ""
            var countryValue = ""
            var homePhoneValue = ""
            var organizationValue = ""
            var specialityValue = ""
            var workPhoneValue = ""
            var emailValue = ""
            var photoValue = ""

            if (!vcard.structuredName.family.isNullOrEmpty()) {
                surnameValue = vcard.structuredName.family
            }
            if (!vcard.structuredName.given.isNullOrEmpty()) {
                nameValue = vcard.structuredName.given
            }
            if (!vcard.titles.isNullOrEmpty()) {
                if (!vcard.titles[0].value.isNullOrEmpty()) {
                    specialityValue = vcard.titles[0].value
                }
            }
            if (!vcard.organization.values.isNullOrEmpty()) {
                organizationValue = vcard.organization.values[0]
            }
            if (!vcard.addresses.isNullOrEmpty()) {
                if (!vcard.addresses[0].locality.isNullOrEmpty()) {
                    townValue = vcard.addresses[0].locality
                }
                if (!vcard.addresses[0].country.isNullOrEmpty()) {
                    countryValue = vcard.addresses[0].country
                }
            }
            if (!vcard.telephoneNumbers.isNullOrEmpty()) {
                if (!vcard.telephoneNumbers[0].text.isNullOrEmpty()) {
                    workPhoneValue = vcard.telephoneNumbers[0].text
                }
                if (vcard.telephoneNumbers.size > 1 && !vcard.telephoneNumbers[1].text.isNullOrEmpty()) {
                    homePhoneValue = vcard.telephoneNumbers[1].text
                }
            }
            if (!vcard.emails.isNullOrEmpty()) {
                if (!vcard.emails[0].value.isNullOrEmpty()) {
                    emailValue = vcard.emails[0].value
                }
            }
            if (!vcard.photos.isNullOrEmpty()) {
                if (vcard.photos[0].data.isNotEmpty()) {
                    val photo = vcard.photos[0].data
                    val photoBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.size)
                    photoValue = insertPhoto(contentResolver, context, photoBitmap) ?: ""
                }
            }
            var personAdditionalInfoValue = ""
            var profSkillsValue = ""
            var privateInfoValue = ""
            var referenceValue = ""
            var cardTextureValue = DEFAULT_CARD_TEXTURE
            var cardTextColorValue = DEFAULT_CARD_TEXT_COLOR
            var cardCornerValue = DEFAULT_CARD_CORNER
            var formPhotoValue = DEFAULT_CARD_FORM_PHOTO


            val personAdditionalInfo = vcard.getExtendedProperties(PROFIL_INFO)
            personAdditionalInfo.forEach {
                personAdditionalInfoValue = it.value
            }

            val profSkills = vcard.getExtendedProperties(PROFESSIONAL_SKILLS)
            profSkills.forEach {
                profSkillsValue = it.value
            }

            val privateInfo = vcard.getExtendedProperties(WORK_EXPERIENCE)
            privateInfo.forEach {
                privateInfoValue = it.value
            }

            val reference = vcard.getExtendedProperties(REFERENCE)
            reference.forEach {
                referenceValue = it.value
            }

            val cardTexture = vcard.getExtendedProperties(CARD_TEXTURE)
            cardTexture.forEach {
                cardTextureValue = it.value
            }

            val cardTextColor = vcard.getExtendedProperties(CARD_TEXT_COLOR)
            cardTextColor.forEach {
                cardTextColorValue = it.value
            }
            val cardCorner = vcard.getExtendedProperties(CARD_CORNER)
            cardCorner.forEach {
                cardCornerValue = it.value.toBoolean()
            }
            val cardFormPhoto = vcard.getExtendedProperties(FORM_PHOTO)
            cardFormPhoto.forEach {
                formPhotoValue = it.value
            }

            val card = photoValue.let {
                Card(
                    id = 0,
                    userId = 0,
                    name = nameValue,
                    surname = surnameValue,
                    photo = it,
                    workPhone = workPhoneValue,
                    homePhone = homePhoneValue,
                    email = emailValue,
                    speciality = specialityValue,
                    organization = organizationValue,
                    town = townValue,
                    country = countryValue,
                    additionalContactInfo = personAdditionalInfoValue,
                    professionalInfo = profSkillsValue,
                    privateInfo = privateInfoValue,
                    reference = referenceValue,
                    cardTexture = cardTextureValue,
                    isCardCorner = cardCornerValue,
                    cardTextColor = cardTextColorValue,
                    cardFormPhoto = formPhotoValue
                )
            }
            cardList.add(card)
        }
        _cardList.postValue(cardList)
    }

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            addCardUseCase.execute(card)
        }
    }

    fun insertPhoto(contentResolver: ContentResolver, context: Context, bitmap: Bitmap): String? {
//        viewModelScope.launch(Dispatchers.IO) {
//            val uri = getTmpFileUri(context)

//            var bitmapPhoto = MediaStore.Images.Media.getBitmap(contentResolver, uri)
////            var bitmapByteCount: Int? = bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
//            val imageFile = File(uri.toString())
        var bitmapPhoto = bitmap

        val currentHeight = bitmapPhoto.height.toDouble()
        val currentWidth = bitmapPhoto.width.toDouble()
        if (bitmapPhoto.height > bitmapPhoto.width && bitmapPhoto.width > 600) {
            val cameraCoef = currentHeight / currentWidth
            val height = 150 * cameraCoef
            bitmapPhoto = getResizedBitmap(bitmapPhoto, 150.0, height)
//                 bitmapByteCount =
//                    bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
        }

        if (currentHeight < currentWidth && currentHeight > 150) {
            val cameraCoef = (currentWidth / currentHeight)
            val width = 150 * cameraCoef

            bitmapPhoto = getResizedBitmap(bitmapPhoto, width, 150.0)
//                bitmapByteCount =
//                    bitmapPhoto?.let { BitmapCompat.getAllocationByteCount(it) }
        }

        val uriAvatar = saveImageInExternalCacheDir(context, bitmapPhoto)

//            _photoUri.postValue(uriAvatar)

        return uriAvatar

//        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
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

//    private fun getTmpFileUri(context: Context): Uri {
//
//        var fos: FileOutputStream? = null
//        try {
//            if (base64ImageData != null) {
//                fos = context.openFileOutput("imageName.png", Context.MODE_PRIVATE)
//                val decodedString: ByteArray = Base64.decode(base64ImageData, Base64.DEFAULT)
//                fos.write(decodedString)
//                fos.flush()
//                fos.close()
//            }
//        } catch (e: Exception) {
//        } finally {
//            if (fos != null) {
//                fos = null
//            }
//        }
//
//
//        val tmpFile = File(context.cacheDir, "temp.jpg")
//        return FileProvider.getUriForFile(
//            context,
//            "${BuildConfig.APPLICATION_ID}.provider",
//            tmpFile
//        )
//    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    sealed class CardListFromAccountState() {
        object Loading : CardListFromAccountState()
        class Success(val cardList: List<Card>) : CardListFromAccountState()
        class Error(val exception: String) : CardListFromAccountState()
        object NoSuccess : CardListFromAccountState()
    }

    sealed class GetCardsState{
        object Loading: GetCardsState()
        class Success(val cardList: List<Card>): GetCardsState()
        class Failure(val exception: String): GetCardsState()

    }
}
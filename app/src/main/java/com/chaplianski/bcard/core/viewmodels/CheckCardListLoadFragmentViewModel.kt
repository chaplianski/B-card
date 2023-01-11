package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.core.ui.ShareFragment
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import ezvcard.VCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckCardListLoadFragmentViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase
): ViewModel() {

    private val _loadedCardList = MutableLiveData<List<Card>>()
    val loadedCardList: LiveData<List<Card>> get() = _loadedCardList


    fun convertVCardListToCardList(vcardList: List<VCard>) {

        val cardList = mutableListOf<Card>()
        vcardList.forEach { vcard ->

            val formatedName = vcard.formattedName
            val addInfoProperties = vcard.getExtendedProperties(ShareFragment.ADD_INFO)
            val cardSettingsProperties = vcard.getExtendedProperties(ShareFragment.CARD_SETTINGS)

            val surnameValue = vcard.structuredName.family
            val nameValue = vcard.structuredName.given
            val phoneValue = vcard.telephoneNumbers[0].text
            val emailValue = vcard.emails[0].value
            val townValue = vcard.addresses[0].locality ?: ""
            val countryValue = vcard.addresses[0].country ?: ""
            val specialityValue = vcard.titles[0].value
            val organizationValue = vcard.organization.values[0]
            val linkedInValue = vcard.urls[0].value
            val photoValue = vcard.photos[0].url
            var profilInfoValue = ""
            var profSkillsValue = ""
            var educationValue = ""
            var workExperienceValue = ""
            var referenceValue = ""
            var cardColorValue = 0
            var strokeColorValue = ""
            var cardCornerRadiusValue = 0f
            var formPhotoValue = ""

            for (property in addInfoProperties) {
                when (property.propertyName) {
                    ShareFragment.PROFIL_INFO -> profilInfoValue = property.getParameter(
                        ShareFragment.PROFIL_INFO)
                    ShareFragment.PROFESSIONAL_SKILLS -> profSkillsValue = property.getParameter(
                        ShareFragment.PROFESSIONAL_SKILLS
                    )
                    ShareFragment.EDUCATION -> educationValue = property.getParameter(ShareFragment.EDUCATION)
                    ShareFragment.WORK_EXPERIENCE -> workExperienceValue = property.getParameter(
                        ShareFragment.WORK_EXPERIENCE
                    )
                    ShareFragment.REFERENCE -> referenceValue = property.getParameter(ShareFragment.REFERENCE)
                }
            }
            for (property in cardSettingsProperties) {
                when (property.propertyName) {
                    ShareFragment.CARD_COLOR -> cardColorValue = property.getParameter(ShareFragment.CARD_COLOR).toInt()
                    ShareFragment.PROFESSIONAL_SKILLS -> strokeColorValue = property.getParameter(
                        ShareFragment.STROKE_COLOR
                    )
                    ShareFragment.EDUCATION -> cardCornerRadiusValue =
                        property.getParameter(ShareFragment.CARD_CORNER).toString().toFloat()
                    ShareFragment.WORK_EXPERIENCE -> formPhotoValue = property.getParameter(
                        ShareFragment.FORM_PHOTO
                    )

                }
            }

            val card = Card(
                0,
                0,
                nameValue,
                surnameValue,
                photoValue,
                phoneValue,
                linkedInValue,
                emailValue,
                specialityValue,
                organizationValue,
                townValue,
                countryValue,
                profilInfoValue,
                educationValue,
                profSkillsValue,
                workExperienceValue,
                referenceValue,
                cardColorValue,
                strokeColorValue,
                cardCornerRadiusValue,
                formPhotoValue
            )
            cardList.add(card)
        }
        _loadedCardList.postValue(cardList)

    }

    fun saveCheckedCardToDB(listCard: MutableList<Card>) {
        viewModelScope.launch(Dispatchers.IO) {
            listCard.forEach { card ->
                addCardUseCase.execute(card)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

    }

}
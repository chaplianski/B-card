package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.loader.app.LoaderManager
import com.chaplianski.bcard.domain.model.Card
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AccountContactsPicker(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (isHavePermition: Boolean) -> Unit
) {

    private val requestPermissionLauncher = activityResultRegistry.register(
        REGISTRY_KEY_PERMISSION,
        ActivityResultContracts.RequestPermission()
    ) {result ->
        if (result) callback(true)
//        if (result) callback.invoke(getContacts())
//        LoaderManager.getInstance(this).initLoader(0, null, this)

    }

    fun checkPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
    }



    private fun getContacts(): List<Card> {
        val accountContacts = mutableListOf<Card>()
        val resolver = context.contentResolver
        val cursor = resolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID) ?: 1)
                    val name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE)
                            ?: 1
                    )
                    val photo = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI) ?: 1
                    )
                    var nameValue = ""
                    var surnameValue = ""
                    var phoneValue = ""
                    var homePhoneValue = ""
                    var emailValue = ""
                    var organizationValue = ""
                    var organizationDepartmentValue = ""
                    var organizationJobValue = ""
                    var townValue = ""
                    var countryValue = ""
                    var addressValue = ""
                    var birthdayValue = ""
                    var nikeNameValue = ""
                    var webSiteValue = ""
                    var noteValue = ""
                    var photoValue = ""

                    val phoneSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Phone.DATA1// + " = " + ContactsContract.CommonDataKinds.Phone.NUMBER
                    val phoneCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.DATA),
                        phoneSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    phoneValue = getValueToField(phoneCursor, name, phoneValue)

                    val homePhoneSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE// + " = " + ContactsContract.CommonDataKinds.Phone.NUMBER
                    val homePhoneCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.DATA),
                        homePhoneSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    homePhoneValue = getValueToField(homePhoneCursor, name, homePhoneValue)

                    val emailSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    val emailCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.DATA),
                        emailSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    emailValue = getValueToField(emailCursor, name, emailValue)

                    val organizationSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Organization.TYPE_WORK
                    val organizationCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Organization.DATA),
                        organizationSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    if (organizationCursor?.moveToFirst() == true){
                        organizationValue = getValueToField(organizationCursor, name, organizationValue)
                    }


                    val organizationDepartmentSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Organization.DEPARTMENT
                    val organizationDepartmentCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Organization.DATA),
                        organizationDepartmentSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    organizationDepartmentValue = getValueToField(
                        organizationDepartmentCursor,
                        name,
                        organizationDepartmentValue
                    )

                    val organizationJobSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION
                    val organizationJobCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Organization.DATA),
                        organizationJobSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    organizationJobValue =
                        getValueToField(organizationJobCursor, name, organizationJobValue)

                    val townSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.StructuredPostal.CITY
                    val townCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.DATA),
                        townSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    townValue = getValueToField(townCursor, name, townValue)

                    val countrySelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
                    val countryCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.DATA),
                        countrySelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    countryValue = getValueToField(countryCursor, name, countryValue)

                    val addressSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
                    val addressCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.DATA),
                        addressSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    addressValue = getValueToField(addressCursor, name, addressValue)

                    val birthdaySelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Event.TYPE + " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
                    val birthdayCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Event.DATA),
                        birthdaySelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    birthdayValue = getValueToField(birthdayCursor, name, birthdayValue)

                    val nikeNameSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Nickname.DATA1 // + " = " + ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT
                    val nikeNameCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Nickname.DATA),
                        nikeNameSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    nikeNameValue = getValueToField(nikeNameCursor, name, nikeNameValue)

                    val noteSelection =
                        ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Note.NOTE // + " = " + ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT
                    val noteCursor = resolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Note.NOTE),
                        noteSelection,
                        null,
                        ContactsContract.Data.DISPLAY_NAME
                    )
                    noteValue = getValueToField(noteCursor, name, noteValue)

                    val addInfoStringBuilder = StringBuilder().apply {
                        if (addressValue.isNotEmpty()) {
                            this.append("Postal address:\n")
                            this.append(addressValue)
                        }
                    }.toString()
                    val profInfoStringBuilder = StringBuilder().apply {
                        if (organizationDepartmentValue.isNotEmpty()) {
                            this.append("Department:\n")
                            this.append(organizationDepartmentValue)
                        }
                    }.toString()
                    val privateInfoStringBuilder = StringBuilder().apply {
                        if (nikeNameValue.isNotEmpty()) {
                            this.append("Nickname: ")
                            this.append(nikeNameValue)
                            this.append("\n")
                        }
                        if (birthdayValue.isNotEmpty()) {
                            this.append("Birthday: ")
                            this.append(birthdayValue)
                            this.append("\n")
                        }
                    }.toString()

                    if (name != null) {
                        val nameList = name.split(",")
                        nameValue = nameList[0]
                        if (nameList.size > 1) surnameValue = name.split(",")[1]
                    }
                    if (nameValue.isNotEmpty() || surnameValue.isNotEmpty()) {
                        val card = Card(
                            id = 0,
                            userId = 0,
                            name = surnameValue.trimStart() ?: "",
                            surname = nameValue.trimStart(),
                            photo = photo ?: "",
                            workPhone = phoneValue,
                            homePhone = homePhoneValue,
                            email = emailValue,
                            town = townValue,
                            country = countryValue,
                            speciality = organizationJobValue,
                            organization = organizationValue,
                            additionalContactInfo = addInfoStringBuilder,
                            professionalInfo = profInfoStringBuilder,
                            privateInfo = privateInfoStringBuilder
                        )
                        accountContacts.add(card)
                    }
                }
            }
        }
        cursor?.close()

        return accountContacts
    }

    private fun getValueToField(
        cursor: Cursor?,
        name: String?,
        value: String
    ): String {
        var dataValue = value
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                if (name != null) {
                    dataValue = cursor.getString(0)
                }
            }
            cursor.close()
        }
        return dataValue
    }
    companion object {
        val REGISTRY_KEY_PERMISSION = "registry key permission"

    }

}
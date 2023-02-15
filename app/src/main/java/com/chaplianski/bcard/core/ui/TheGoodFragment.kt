package com.chaplianski.bcard.core.ui

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.chaplianski.bcard.domain.model.Card


class TheGoodFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var PROJECTION_NUMBERS = arrayOf(
//        ContactsContract.Data.CONTENT_URI,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,

    )
    private var PROJECTION_DETAILS = arrayOf(
        ContactsContract.Contacts._ID.toString(),
        ContactsContract.Contacts.DISPLAY_NAME.toString(),
        ContactsContract.CommonDataKinds.Phone.NUMBER.toString(),
//        ContactsContract.CommonDataKinds.Phone.DATA1,
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE.toString(),
        ContactsContract.CommonDataKinds.Email.TYPE_WORK.toString(),
        ContactsContract.CommonDataKinds.Organization.TYPE_WORK.toString(),
        ContactsContract.CommonDataKinds.Organization.DEPARTMENT.toString(),
        ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION.toString(),
        ContactsContract.CommonDataKinds.StructuredPostal.CITY.toString(),
        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY.toString(),
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK.toString(),
        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString(),
        ContactsContract.CommonDataKinds.Nickname.DATA1.toString(),
        ContactsContract.CommonDataKinds.Note.NOTE.toString(),
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI.toString()
    )
    val cardList = mutableListOf<Card>()
    var loader = Loader<Cursor>(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val contactLoader = ContactLoader(requireActivity().applicationContext)

//        LoaderManager.getInstance(this).initLoader(0, null, contactLoader)
        LoaderManager.getInstance(this).initLoader(0, null, this)

//        loader = LoaderManager.getInstance(this).initLoader(LOAD_MANAGER_ID, null, this)
    }

    override fun onCreateLoader(
        id: Int,
        args: Bundle?
    ): Loader<Cursor?> {
        return when (id) {
            0 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION_NUMBERS,
                null,
                null,
                null
            )
            else -> CursorLoader(
                requireContext(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION_DETAILS,
                null,
                null,
                null
            )
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }

    override fun onLoadFinished(
        loader: Loader<Cursor?>,
        data: Cursor?
    ) {
        val contacts = mutableListOf<Long>()
        when (loader.id) {
            0 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        val contactId: Long = data.getLong(0)
                        contacts.add(contactId)
                        cardList.add(Card(id = contactId))
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@TheGoodFragment)
                    .initLoader(1, null, this)
            }
            1 -> if (data != null) {
                while (!data.isClosed && data.moveToNext()) {
                    val id: Long = data.getLong(0)
                    val name: String = data.getString(1)
                    val workPhone = data.getString(2)
                    val homePhone = data.getString(3)
                    val email = data.getString(4)
                    val organization = data.getString(5)
                    val department = data.getString(6)
                    val job = data.getString(7)
                    val city = data.getString(8)
                    val country = data.getString(9)
                    val address = data.getString(10)
                    val birthday = data.getString(11)
                    val nickname = data.getString(12)
                    val note = data.getString(13)
                    val photo = data.getString(14)

//                    cardList.add(Card(workPhone = phone))
//                    val contactPhones = phones[0]
                    if (cardList != null) {
                        cardList.forEach { card ->
                            if (card.id == id) {
                                val nameList = name.split(",")
                                var surnameValue = ""
                                val nameValue = nameList[0].trimStart()
                                if (nameList.size > 1) surnameValue = name.split(",")[1].trimStart()
                                val addInfoStringBuilder = StringBuilder().apply {
                                    if (address.isNotEmpty()) {
                                        this.append("Postal address:\n")
                                        this.append(address)
                                    }
                                }.toString()
                                val profInfoStringBuilder = StringBuilder().apply {
                                    if (department.isNotEmpty()) {
                                        this.append("Department:\n")
                                        this.append(department)
                                    }
                                }.toString()
                                val privateInfoStringBuilder = StringBuilder().apply {
                                    if (nickname.isNotEmpty()) {
                                        this.append("Nickname: ")
                                        this.append(nickname)
                                        this.append("\n")
                                    }
                                    if (birthday.isNotEmpty()) {
                                        this.append("Birthday: ")
                                        this.append(birthday)
                                        this.append("\n")
                                    }
                                }.toString()
                                card.name = nameValue
                                card.surname = surnameValue
                                card.photo = photo ?: ""
                                card.workPhone = workPhone
                                card.homePhone = homePhone
                                card.email = email
                                card.town = city
                                card.country = country
                                card.speciality = job
                                card.organization = organization
                                card.additionalContactInfo = addInfoStringBuilder
                                card.professionalInfo = profInfoStringBuilder
                                card.privateInfo = privateInfoStringBuilder

                            }
                        }
//                        for (card in cardList) {
//                            addContact(id, name, phone, photo)
//                        }
                    }
//                    cardList[1].copy()
                }
                data.close()
//                fillCardAdapter(cardList, addButton, cardListAdapter, checkboxAllCards)
                Log.d("MyLog", "cardListContact = $cardList")
            }
        }


    }


}
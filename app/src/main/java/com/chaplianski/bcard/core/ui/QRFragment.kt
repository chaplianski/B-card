package com.chaplianski.bcard.core.ui

import android.accounts.AccountManager
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chaplianski.bcard.R
import com.google.zxing.WriterException
import contacts.core.data.Data
import java.time.LocalDate
import java.util.*


class QRFragment : Fragment() {

    var qrImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrImage = view.findViewById(R.id.iv_qr_fragment_code)
        val qrButton: Button = view.findViewById(R.id.bt_qr_fragment_button)

        qrButton.setOnClickListener {
            generateQRCode("Insert text")
        }


    }

    fun generateQRCode(text: String) {
        val qrgEncoder = QRGEncoder(text, null, QRGContents.Type.TEXT, 600)
        val qrSaver = QRGSaver()
//
        try {
            val bitmap = qrgEncoder.bitmap
            qrImage?.setImageBitmap(bitmap)
        } catch (e: WriterException) {

        }
    }

    fun addContact(
        context: Context,
        DisplayName: String?,
        WorkNumber: String?,
        MobileNumber: String?,
        emailID: String?,
        jobTitle: String?,
        company: String?,
        address: String?
    ) {
        val ops = ArrayList<ContentProviderOperation>()
        val account: String? = getUsernameLong(context)
        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account)
                .build()
        )

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(
                ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DisplayName
                    ).build()
            )
        }
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops)
            //requestSyncNow(context);
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                //Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (e1: Exception) {
            }
        }
    }

    fun getUsernameLong(context: Context?): String? {
        val manager = AccountManager.get(context)
        val accounts = manager.getAccountsByType("com.google")
        val possibleEmails: MutableList<String?> = LinkedList()
        for (account in accounts) {

            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name)
            Log.i("DGEN ACCOUNT", "CALENDAR LIST ACCOUNT/" + account.name)
        }
        return if (!possibleEmails.isEmpty() && possibleEmails[0] != null) {
            possibleEmails[0]
        } else null
    }

//    fun fetchContacts(resolver: ContentResolver) : MutableCollection<ItemContact> {
//        var cols = arrayOf(
//            Data.CONTACT_ID,
//            Data.MIMETYPE,
//            Data.DISPLAY_NAME,
//            Phone.NUMBER,
//            Data.DATA1,
//            Data.DATA2,
//            Data.DATA3,
//        )
//
//        // get only rows of MIMETYPE phone and your new custom MIMETYPE
//        var selection = Data.MIMETYPE + " IN (" + Phone.CONTENT_ITEM_TYPE + ", " + "vnd.android.cursor.item/vnd.com.example.favorite_song" + ")"
//
//        var cursor : Cursor? = resolver.query(Data.CONTENT_URI, cols, null, null, Data.CONTACT_ID)
//        val map = hashMapOf<Long, ItemContact>()
//
//        while(cursor != null && cursor.moveToNext()) {
//            val contactId = cursor.getLong(0)
//            val mimetype = cursor.getString(1)
//
//            // gets the existing ItemContact from the map or if not found, puts an empty one in the map
//            val contact = map.getOrPut(contactId) { ItemContact() }
//
//            with(contact) {
//                if (mimetype == Phone.CONTENT_ITEM_TYPE) {
//                    contact_id = contactId
//                    name = cursor.getString(2)
//                    number = cursor.getString(3)
//                } else {
//                    contact_id = contactId
//                    song = cursor.getString(4)
//                    band = cursor.getString(5)
//                    album = cursor.getString(6)
//                }
//            }
//        }
//        return map.values
//    }

    // Import the contacts from Google Contacts



}


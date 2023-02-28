package com.chaplianski.bcard.core.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.core.app.ActivityCompat.startActivityForResult
import com.chaplianski.bcard.core.dialogs.SaveCardDialog
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.domain.model.Card
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.AddressType
import ezvcard.parameter.EmailType
import ezvcard.parameter.ImageType
import ezvcard.parameter.TelephoneType
import ezvcard.property.*
import java.io.File
import java.io.IOException
import java.nio.file.Files.createFile
import javax.inject.Inject

//class ContactSaver(
//    val context: Context,
//    activityResultRegistry: ActivityResultRegistry,
//    private val callback: (cardList: List<Card>) -> Unit
//) {
//
//    val vcardList = mutableListOf<VCard>()
//
//    val saveFileLauncher = activityResultRegistry.register(
//        REGISTRY_RESULT_SAVE_FILE,
//        CreateDocument("application/vcf")
//    ) { result ->
//
//
////        val outputStream = result.let {
////            context.contentResolver?.openOutputStream(it)
////        }
//////            outputStream?.write(vCard)
////        Ezvcard.write(vcardList).go(outputStream)
////        outputStream?.flush()
////        outputStream?.close()
//
////            val selectedFile = data?.data //The uri with the location of the file
////            val outputStream = data?.data?.let {
////                context.contentResolver?.openOutputStream(it)
////            }
//////            outputStream?.write(vCard)
////            Ezvcard.write(vcardList).go(outputStream)
////            outputStream?.flush()
////            outputStream?.close()
////            Ezvcard.write(vCard).version(VCardVersion.V3_0).go(file)
////            val file = File(selectedFile)
////            val vcard = Ezvcard.parse(file).first()
////            addVCardData(vcard)
////            Log.d("MyLog", "file = $selectedFile")
//
//
//    }
//
//    fun saveVCardToPhone(file: File, fileName: String) {
//        val path = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOCUMENTS
//        )
//        path.mkdirs()
//        val uriForFile = Uri.fromFile(file)
//        try {
//            createFile(uriForFile, "${fileName}.vcf")
//        } catch (e: Exception) {
//            Log.d("MyLog", "e = $e")
//        }
//    }
//
//    private fun createFile(pickerInitialUri: Uri, fileName: String) {
//
//        saveFileLauncher.launch(fileName)
//
////        val listCard = mutableListOf<Card>()
////        vcardList.clear()
////        listCard.clear()
////        cardList.forEach { cardItem ->
////            if (cardItem.isChecked) {
////                listCard.add(cardItem)
////                vcardList.add(createVCard(cardItem))
////            }
////
////        }
////
////        Log.d("MyLog", "${vcardList.size}")
////        if (!listCard.isNullOrEmpty()) {
////            when (listCard.size) {
////                1 -> {
////                    val card = listCard[0]
////                    val file = File("${card.surname}.vcf")
////                    saveVCardToPhone(file, card.surname)
////                }
////                else -> {
////                    val file = File("contacts.vcf")
////                    saveVCardToPhone(file, "contacts")
////                }
////
////            }
////        }
////        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
////            addCategory(Intent.CATEGORY_OPENABLE)
////            type = "application/vcf"
////            putExtra(Intent.EXTRA_TITLE, fileName)
////            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
////        }
////        startActivityForResult(intent, SaveCardDialog.CREATE_FILE)
//    }
//
////    class CreateSpecificTypeDocument(private val type: String) :
////        CreateDocument("todo/todo") {
////        override fun createIntent(context: Context, input: String): Intent {
////            return super.createIntent(context, input).setType(type)
////        }
////    }
//
//    @Throws(IOException::class)
//    private fun createVCard(card: Card): VCard {
//        val vcard = VCard()
//        vcard.kind = Kind.individual()
//        val structureName = StructuredName()
//        structureName.given = card.name
//        structureName.family = card.surname
//        vcard.structuredName = structureName
//        vcard.setFormattedName("${card.name} ${card.surname}")
//        vcard.addTitle(card.speciality)
//        vcard.setOrganization(card.organization)
//        val adr = Address()
//        adr.locality = card.town
//        adr.country = card.country
//        adr.types.add(AddressType.WORK)
//        vcard.addAddress(adr)
//        vcard.addTelephoneNumber(card.workPhone, TelephoneType.WORK)
//        vcard.addEmail(card.email, EmailType.WORK)
//        vcard.addTelephoneNumber(card.homePhone, TelephoneType.HOME)
//        val file = File(card.photo)
//        val photo = Photo(file, ImageType.JPEG)
//        vcard.addPhoto(photo)
//        vcard.uid = Uid.random()
//        vcard.revision = Revision.now()
//
//        val profile = vcard.addExtendedProperty(PROFIL_INFO, card.additionalContactInfo)
//        val skills = vcard.addExtendedProperty(PROFESSIONAL_SKILLS, card.professionalInfo)
//        val privateInfo = vcard.addExtendedProperty(WORK_EXPERIENCE, card.privateInfo)
//        val reference = vcard.addExtendedProperty(REFERENCE, card.reference)
//        val cardColor = vcard.addExtendedProperty(CARD_TEXTURE, card.cardTexture.toString())
//        val strokeColor = vcard.addExtendedProperty(CARD_TEXT_COLOR, card.cardTextColor)
//        val cardCorner = vcard.addExtendedProperty(CARD_CORNER, card.isCardCorner.toString())
//        val formPhoto = vcard.addExtendedProperty(FORM_PHOTO, card.cardFormPhoto)
//
//        profile.group = ADD_INFO
//        skills.group = ADD_INFO
//        privateInfo.group = ADD_INFO
//        reference.group = ADD_INFO
//        cardColor.group = CARD_SETTINGS
//        strokeColor.group = CARD_SETTINGS
//        cardCorner.group = CARD_SETTINGS
//        formPhoto.group = CARD_SETTINGS
//
//        Log.d("MyLog", "vcard = $vcard")
//        return vcard
//    }
//
//
//    companion object {
//        val REGISTRY_RESULT_SAVE_FILE = "registry result save file"
//    }
//}
//
//class SimpleContract : ActivityResultContract<List<VCard>, String?>() {
//
//    override fun createIntent(context: Context, input: List<VCard>): Intent {
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/vcf"
//
//        val file = when (input.size) {
//            1 -> {
//                val surname = input[0].structuredName.family
//                File("$surname.vcf")
//            }
//            else -> {
//                File("contacts.vcf")
//            }
//        }
//        val cardUri = saveVCardToPhone(file)
//
////            putExtra(Intent.EXTRA_TITLE, file)
////            putExtra(DocumentsContract.EXTRA_INITIAL_URI, cardUri)
//            putExtra("card uri", cardUri)
//
//    }
//            return intent
//        }
//
//    override fun parseResult(resultCode: Int, intent: Intent?): String? = when {
//        resultCode != Activity.RESULT_OK -> null      // Return null, if action is cancelled
//        else -> {
////            intent?.getStringExtra("data")        // Return the data
//            intent?.getStringExtra("card uri")
//        }
//    }
//
//        private fun saveVCardToPhone(file: File): Uri {
////        val file = File("contacts.vcf")
//
//            val path = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS
//            )
//            path.mkdirs()
//            val uriForFile = Uri.fromFile(file)
////        try {
////            createFile(uriForFile, "${fileName}.vcf")
////        } catch (e: Exception) {
////            Log.d("MyLog", "e = $e")
////        }
//            return uriForFile
//
//        }
//
//
//    }
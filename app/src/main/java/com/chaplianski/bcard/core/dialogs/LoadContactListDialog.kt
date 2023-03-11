package com.chaplianski.bcard.core.dialogs


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.helpers.CardDecorResources
import com.chaplianski.bcard.core.helpers.ProcessCard
import com.chaplianski.bcard.core.model.Contact
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.LoadContactListDialogViewModel
import com.chaplianski.bcard.databinding.DialogLoadContactsBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject


class LoadContactListDialog : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var _binding: DialogLoadContactsBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val loadContactListDialogViewModel: LoadContactListDialogViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .loadContactListDialogInject(this)
        super.onAttach(context)
    }

    private var PROJECTION_NUMBERS = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.Contacts.DISPLAY_NAME,
    )
    private var PROJECTION_PHONE = arrayOf(
        Data.CONTACT_ID,
        Data.DATA1,
    )

    private var PROJECTION_EMAIL = arrayOf(
        Data.CONTACT_ID,
        Data.DATA1
    )
    private var PROJECTION_ORGANIZATION = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Organization.COMPANY,
        ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION,
        ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
    )
    private var PROJECTION_LOCATION = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
    )
    private var PROJECTION_NOTE = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Note.NOTE,
    )
    private var PROJECTION_NICKNAME = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Nickname.NAME,
    )
    private var PROJECTION_EVENT = arrayOf(
        Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Event.START_DATE,
    )

    val SELECTION_HOME_PHONE =
        ContactsContract.CommonDataKinds.Phone.TYPE + " IN ('" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + "', '" + ContactsContract.CommonDataKinds.Phone.TYPE_HOME + "')"
    val SELECTION_ORGANIZATION =
        ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Organization.TYPE_WORK
    val SELECTION_BIRTHDAY =
        Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "')" //+ "' AND " +
    val SELECTION_NICKNAME =
        ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'"// AND " +
    val SELECTION_NOTE =
        Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "')"
    var SELECTION =
        Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "')"

    val mSelectionArgs = emptyArray<String>()
    val cardList = mutableListOf<Card>()
    var allCardList = emptyList<Card>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.attributes
        params?.y = 30
        window?.attributes = params
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogLoadContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = binding.btLoadCardDialogCancel
        cancelButton.setOnClickListener {
            dismiss()
        }

        loadContactListDialogViewModel.getCardListState
            .flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    is LoadContactListDialogViewModel.GetCardsState.Loading -> {}
                    is LoadContactListDialogViewModel.GetCardsState.Success -> {
                        allCardList = it.cardList
                        val listCard = mutableListOf<Card>()

                        cardList.forEach { cardItem ->
                            if (cardItem.isChecked) {
                                cardItem.id = 0
                                listCard.add(cardItem)
                            }
                        }
                        val doubleCardList = mutableListOf<Card>()
                        when {
                            allCardList.isEmpty() && listCard.isNotEmpty() -> {
                                listCard.forEach { card ->
                                    if (card.photo.isNotEmpty()) {
                                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                                            requireContext().contentResolver,
                                            Uri.parse(card.photo)
                                        )
                                        card.photo =
                                            saveImageInExternalCacheDir(requireContext(), bitmap)
                                    }
                                    loadContactListDialogViewModel.addCard(card)
                                }
                            }
                            allCardList.isNotEmpty() && listCard.isNotEmpty() -> {
                                listCard.forEach { checkedCard ->
                                    val doubleCheckedCard =
                                        allCardList.filter { checkedCard.name == it.name && checkedCard.surname == it.surname }
                                    if (doubleCheckedCard.isNotEmpty()) {
                                        if (checkedCard.photo.isNotEmpty()) {
                                            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                                                requireContext().contentResolver,
                                                Uri.parse(checkedCard.photo)
                                            )
                                            checkedCard.photo =
                                                saveImageInExternalCacheDir(
                                                    requireContext(),
                                                    bitmap
                                                )
                                        }
                                        doubleCardList.add(checkedCard)
                                    } else {
                                        if (checkedCard.photo.isNotEmpty()) {
                                            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                                                requireContext().contentResolver,
                                                Uri.parse(checkedCard.photo)
                                            )
                                            checkedCard.photo =
                                                saveImageInExternalCacheDir(
                                                    requireContext(),
                                                    bitmap
                                                )
                                        }
                                        loadContactListDialogViewModel.addCard(checkedCard)
                                    }
                                }
                            }
                        }

                        if (doubleCardList.isNotEmpty()) {
                            val contactList = mutableListOf<Contact>()
                            doubleCardList.forEach {
                                val contact = Contact(
                                    name = it.name,
                                    surname = it.surname,
                                    workPhone = it.workPhone,
                                    isChecked = false
                                )
                                contactList.add(contact)
                            }
                            showCheckDoubleCardListDialog(contactList)
                        }
                        if (doubleCardList.isNotEmpty()) {
                            setupCheckDoubleCardListDialog()
                        } else {
                            parentFragmentManager.setFragmentResult(
                                REQUEST_KEY,
                                bundleOf(CHECKED_OPTION to ADD_STATUS)
                            )
                            dismiss()
                        }
                    }
                    is LoadContactListDialogViewModel.GetCardsState.Failure -> {}
                }
            }
            .launchIn(lifecycleScope)

        LoaderManager.getInstance(this@LoadContactListDialog).initLoader(0, null, this)
    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            0 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION_NUMBERS,
                SELECTION,
                mSelectionArgs,
                SORT_ORDER
            )
            1 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION_PHONE,
                null,
                mSelectionArgs,
                SORT_ORDER
            )
            2 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION_PHONE,
                SELECTION_HOME_PHONE,
                mSelectionArgs,
                SORT_ORDER
            )
            3 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                PROJECTION_EMAIL,
                null,
                mSelectionArgs,
                SORT_ORDER
            )
            4 -> CursorLoader(
                requireContext(),
                Data.CONTENT_URI,
                PROJECTION_ORGANIZATION,
                SELECTION_ORGANIZATION,
                mSelectionArgs,
                SORT_ORDER
            )
            5 -> CursorLoader(
                requireContext(),
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                PROJECTION_LOCATION,
                null,
                mSelectionArgs,
                SORT_ORDER
            )
            6 -> CursorLoader(
                requireContext(),
                Data.CONTENT_URI,
                PROJECTION_NOTE,
                SELECTION_NOTE,
                mSelectionArgs,
                SORT_ORDER
            )
            7 -> CursorLoader(
                requireContext(),
                Data.CONTENT_URI,
                PROJECTION_NICKNAME,
                SELECTION_NICKNAME,
                mSelectionArgs,
                SORT_ORDER
            )
            8 -> CursorLoader(
                requireContext(),
                Data.CONTENT_URI,
                PROJECTION_EVENT,
                SELECTION_BIRTHDAY,
                mSelectionArgs,
                SORT_ORDER
            )
            else -> CursorLoader(requireContext())
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {

        when (loader.id) {
            0 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var photoUri = ""
                        if (data.getString(1) != null) photoUri = data.getString(1)
                        val name: String = data.getString(2)

                        val nameList = name.split(" ")
//                        Log.d("MyLog", "nameList = $nameList")
                        var nameValue = ""
                        var surnameValue = ""
                        when (nameList.size) {
                            1 -> {
                                surnameValue = nameList[0]
                            }
                            2 -> {
                                nameValue = nameList[0]
                                surnameValue = nameList[1].trimStart()
                            }
                            else -> {
                                val nameListSize = nameList.size
                                nameValue = nameList.joinToString(
                                    separator = " ",
                                    limit = nameListSize - 1,
                                    truncated = ""
                                )
                                surnameValue = nameList[nameListSize - 1].trimStart()
                            }
                        }
                        cardList.add(
                            Card(
                                name = nameValue, surname = surnameValue, photo = photoUri,
                                id = id.toLong()
                            )
                        )
                    }
                    data.close()
                }

                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(1, null, this)
            }
            1 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        val phone = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {
                                card.workPhone = phone
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(2, null, this)
            }
            2 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        val phone: String = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) card.homePhone = phone
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(3, null, this)
            }
            3 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var email = ""
                        if (data.getString(1) != null) email = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {

                                card.email = email
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(4, null, this)
            }
            4 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var company = ""
                        if (data.getString(1) != null) company = data.getString(1)
                        var job = ""
                        if (data.getString(2) != null) job = data.getString(2)
                        var department = ""
                        if (data.getString(3) != null) department = data.getString(3)

                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {
                                card.organization = company
                                card.speciality = job
                                val profInfoStringBuilder = StringBuilder().apply {
                                    if (department.isNotEmpty()) {
                                        this.append("Department:\n")
                                        this.append(department)
                                    }
                                }.toString()
                                card.professionalInfo = profInfoStringBuilder
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(5, null, this)

            }
            5 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var city = ""
                        if (data.getString(1) != null) city = data.getString(1)
                        var country = ""
                        if (data.getString(2) != null) country = data.getString(2)
                        var address = ""
                        if (data.getString(3) != null) address = data.getString(3)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {

                                card.town = city
                                card.country = country
                                val addInfoStringBuilder = StringBuilder().apply {
                                    if (address.isNotEmpty()) {
                                        this.append(resources.getString(R.string.postal_address))
                                        this.append(address)
                                    }
                                }.toString()
                                card.additionalContactInfo = addInfoStringBuilder
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(6, null, this)
            }
            6 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var note = ""
                        if (data.getString(1) != null) note = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {
                                val privateInfoStringBuilder = StringBuilder().apply {
                                    if (note.isNotEmpty()) {
                                        this.append(resources.getString(R.string.note))
                                        this.append(note)
//                                        this.append("\n")
                                    }
                                }.toString()
                                card.privateInfo = privateInfoStringBuilder
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(7, null, this)
            }
            7 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var nickname = ""
                        if (data.getString(1) != null) nickname = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {
                                val privatInfo = card.privateInfo
                                val privateInfoStringBuilder = StringBuilder().apply {
                                    if (nickname.isNotEmpty()) {
                                        this.append(privatInfo)
                                        this.append("\n")
                                        this.append(resources.getString(R.string.nickname))
                                        this.append(nickname)
                                    }
                                }.toString()
                                card.privateInfo = privateInfoStringBuilder
                            }
                        }
                    }
                    data.close()
                }
                LoaderManager.getInstance(this@LoadContactListDialog)
                    .initLoader(8, null, this)
            }
            8 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        var id = ""
                        if (data.getString(0) != null) id = data.getString(0)
                        var birthday = ""
                        if (data.getString(1) != null) birthday = data.getString(1)
                        cardList.forEach { card ->
                            if (card.id == id.toLong()) {
                                val privatInfo = card.privateInfo
                                val privateInfoStringBuilder = StringBuilder().apply {
                                    if (birthday.isNotEmpty()) {
                                        this.append(privatInfo)
                                        this.append("\n")
                                        this.append("Birthday: ")
                                        this.append(birthday)
                                    }
                                }.toString()
                                card.privateInfo = privateInfoStringBuilder
                            }
                            val cardDecorResource = CardDecorResources()
                            card.cardTexture =
                                cardDecorResource.getCardTextureResource().random().cardTextureName
                        }
                    }
                    data.close()
                }
            }
        }

        val processCardList = ProcessCard(requireContext())
        val addButton = binding.btLoadCardDialogAdd
        val cardListRV = binding.rvLoadCardDialog

        val cardListAdapter = CardListShareFragmentAdapter()
        cardListRV.layoutManager = LinearLayoutManager(context)
        cardListRV.adapter = cardListAdapter
        val checkboxAllCards = binding.checkBoxLoadCardDialogCheckAll
        processCardList.fillCardAdapter(cardList, addButton, cardListAdapter, checkboxAllCards)

        addButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d("MyLog", "get all card to vm")
                loadContactListDialogViewModel.getCards(LoadCardListFromFileDialog.SURNAME)
            }
        }
    }

    private fun setupCheckDoubleCardListDialog() {
        CheckDoubleCardListDialog.setupListener(
            parentFragmentManager,
            this.viewLifecycleOwner
        ) { status, contactList ->
            Log.d("MyLog", "in double setup dialog")
            when (status) {
                CheckDoubleCardListDialog.ADD_STATUS -> {
                    val addDoubleCardList = mutableListOf<Card>()
                    contactList.forEach { contact ->
                        val card =
                            cardList.filter { it.name == contact.name && it.surname == contact.surname && it.workPhone == contact.workPhone }
                        addDoubleCardList.addAll(card)
                    }
                    addDoubleCardList.forEach {
                        loadContactListDialogViewModel.addCard(it)
                    }
                    parentFragmentManager.setFragmentResult(
                        LoadCardListFromFileDialog.REQUEST_KEY,
                        bundleOf(
                            LoadCardListFromFileDialog.CHECKED_OPTION to LoadCardListFromFileDialog.AFTER_CHECK_DOUBLE_STATUS,
                            CURRENT_CARD_ID to LoadCardListFromFileDialog.FAKE_CURRENT_CARD_ID
                        )
                    )
                    dismiss()
                }
                CheckDoubleCardListDialog.CANCEL_STATUS -> {
                    parentFragmentManager.setFragmentResult(
                        LoadCardListFromFileDialog.REQUEST_KEY,
                        bundleOf(
                            LoadCardListFromFileDialog.CHECKED_OPTION to LoadCardListFromFileDialog.AFTER_CHECK_DOUBLE_STATUS,
                            CURRENT_CARD_ID to LoadCardListFromFileDialog.FAKE_CURRENT_CARD_ID
                        )
                    )
                    dismiss()
                }
            }
        }
    }

    private fun showCheckDoubleCardListDialog(contactList: List<Contact>) {
        CheckDoubleCardListDialog.show(parentFragmentManager, contactList)
    }

    fun saveImageInExternalCacheDir(context: Context, bitmap: Bitmap): String {
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

    companion object {
        fun newInstance(): LoadContactListDialog {
            return LoadContactListDialog()
        }

        const val SORT_ORDER = Data.MIMETYPE
        val CHECKED_OPTION = "checked contacts option"
        val ADD_STATUS = "add contacts status"
        val CANCEL_STATUS = "cancel load contacts status"
        val TAG = LoadContactListDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager) {
            val dialogFragment = LoadContactListDialog()
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val status = result.getString(CHECKED_OPTION)
                    if (status != null) {
                        listener.invoke(status)
                    }
                })
        }
    }
}
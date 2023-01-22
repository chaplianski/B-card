package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.factories.CardSettingsDialogViewModelFactory
import com.chaplianski.bcard.core.utils.CARD_CORNER_SIZE
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.CardSettingsDialogViewModel
import com.chaplianski.bcard.databinding.DialogCardSettingsBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.CardSettings
import javax.inject.Inject


class CardSettingsDialog : DialogFragment() {

    private var _binding: DialogCardSettingsBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var cardSettingsDialogViewModelFactory: CardSettingsDialogViewModelFactory
    val cardSettingsDialogViewModel: CardSettingsDialogViewModel by viewModels { cardSettingsDialogViewModelFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .cardSettingsDialogInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 230
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogCardSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_person_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val texture1 = binding.radioButtonCardTexture1
        val texture2 = binding.radioButtonCardTexture2
        val texture3 = binding.radioButtonCardTexture3
        val texture4 = binding.radioButtonCardTexture4
        val texture5 = binding.radioButtonCardTexture5
        val texture6 = binding.radioButtonCardTexture6
        val texture7 = binding.radioButtonCardTexture7
        val texture8 = binding.radioButtonCardTexture8
        val texture9 = binding.radioButtonCardTexture9
        val texture10 = binding.radioButtonCardTexture10
        val texture11 = binding.radioButtonCardTexture11
        val texture12 = binding.radioButtonCardTexture12
        val cardTexture1 = binding.radiogroupCardSettingsDialogTexture1
        val cardTexture2 = binding.radiogroupCardSettingsDialogTexture2
        val cardTextureImage = binding.ivCardSettingsDialogTexture
        val cardViewTextureImage = binding.cardViewCardSettingsDialogTexture
        val cardAvatar = binding.ivCardSettingsDialogAvatar
        val cardTextSchema = binding.rgCardSettingsDialogTextColor
        val textSchemaImage = binding.ivCardSettingsDialogTextSchema
        val cornerRound = binding.radiogroupCardSettingsDialogCornersVariant
        val formAvatar = binding.radiogroupCardSettingsDialogFormPhoto
        val ovalButton = binding.cbCardSettingsDialogOval
        val squareButton = binding.cbCardSettingsDialogRectangle
        val bigCorner = binding.cbCardSettingsDialogYes
        val withoutCorner = binding.cbCardSettingsDialogNo


        val saveButton = binding.btCardSettingsDialogSave
        val cancelButton = binding.btCardSettingsDialogCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)
        var checkedCardTextureVariant = 0
        var checkedCornerSizeVariant = false
        var checkedFormAvatarVariant = ""
        var checkedColorTextVariant = ""

        if (currentCardId != null) {
            cardSettingsDialogViewModel.getCardData(currentCardId)
        }

        cardSettingsDialogViewModel.currentCard.observe(this.viewLifecycleOwner){ card ->
            checkedCardTextureVariant = card.cardTexture
            checkedColorTextVariant = card.cardTextColor
            checkedCornerSizeVariant = card.isCardCorner
            checkedFormAvatarVariant = card.cardFormPhoto

            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
//            textSchemaImage.background = AppCompatResources.getColorStateList(requireContext(), )
//            context?.resources?.getColor(checkedColorTextVariant, null)
//                ?.let { textSchemaImage.setColorFilter(it) }
            cardAvatar.setImageResource(R.drawable.ic_user_circle)
            if (checkedCornerSizeVariant) cardViewTextureImage.radius = CARD_CORNER_SIZE
        }

        var isChecking = true
//        var currentTexture = 0
        cardTexture1.setOnCheckedChangeListener { group, checkedId ->

            val checkedItem = group.checkedRadioButtonId

            if (checkedId != -1 && isChecking) {
                isChecking = false
                Log.d("MyLog", "clear Text 2")
                cardTexture2.clearCheck()
            }
            isChecking = true
            checkedCardTextureVariant = when (checkedId) {
                R.id.radioButton_card_texture_1 -> R.drawable.paper_01
                R.id.radioButton_card_texture_2 -> R.drawable.paper_04
                R.id.radioButton_card_texture_3 -> R.drawable.paper_07
                R.id.radioButton_card_texture_4 -> R.drawable.paper_015
                R.id.radioButton_card_texture_5 -> R.drawable.paper_016
                R.id.radioButton_card_texture_6 -> R.drawable.paper_019
                else -> {R.drawable.paper_034}
            }
            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
            Log.d("MyLog",
                context?.resources?.getResourceEntryName(checkedCardTextureVariant).toString()
            )
        }

        cardTexture2.setOnCheckedChangeListener { group, checkedId ->

            val checkedItem = group.checkedRadioButtonId
            Log.d("MyLog", "checkedId = $checkedId")
            if (checkedId != -1 && isChecking) {
                isChecking = false
                Log.d("MyLog", "clear Text 1")
                cardTexture1.clearCheck()
            }
            isChecking = true

            checkedCardTextureVariant = when (checkedId) {
                R.id.radioButton_card_texture_7 -> R.drawable.paper_031
                R.id.radioButton_card_texture_8 -> R.drawable.paper_033
                R.id.radioButton_card_texture_9 -> R.drawable.paper_034
                R.id.radioButton_card_texture_10 -> R.drawable.paper_035
                R.id.radioButton_card_texture_11 -> R.drawable.paper_06
                R.id.radioButton_card_texture_12 -> R.drawable.paper_09
                else -> {
                    R.drawable.paper_034
                }
            }
            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
            Log.d("MyLog",
                context?.resources?.getResourceEntryName(checkedCardTextureVariant).toString()
            )
        }

        if (checkedCardTextureVariant != 0){
            when(checkedCardTextureVariant){
                R.drawable.paper_01 -> texture1.isChecked = true
                R.drawable.paper_04 -> texture2.isChecked = true
                R.drawable.paper_07 -> texture3.isChecked = true
                R.drawable.paper_015 -> texture4.isChecked = true
                R.drawable.paper_016 -> texture5.isChecked = true
                R.drawable.paper_019 -> texture6.isChecked = true
                R.drawable.paper_031 -> texture7.isChecked = true
                R.drawable.paper_033 -> texture8.isChecked = true
                R.drawable.paper_034 -> texture9.isChecked = true
                R.drawable.paper_035 -> texture10.isChecked = true
                R.drawable.paper_06 -> texture11.isChecked = true
                R.drawable.paper_09 -> texture12.isChecked = true
            }
        }

        cardTextSchema.setOnCheckedChangeListener { group, checkedId ->
            val nameCheckedColorTextVariant = when (checkedId) {
                R.id.rg_card_settings_dialog_text_color_black -> R.color.black
                R.id.rg_card_settings_dialog_text_color_purple -> R.color.purple_700
                R.id.rg_card_settings_dialog_text_color_green -> R.color.green
                R.id.rg_card_settings_dialog_text_color_violet -> R.color.violet
                R.id.rg_card_settings_dialog_text_color_red -> R.color.red
                R.id.rg_card_settings_dialog_text_color_dark_yellow -> R.color.dark_yellow
                R.id.rg_card_settings_dialog_text_color_light_yellow -> R.color.light_yellow
                else -> {R.color.black}
            }
//            textSchemaImage?.tint = AppCompatResources.getDrawable(requireContext(), checkedColorTextVariant)
            checkedColorTextVariant = context?.resources?.getResourceEntryName(nameCheckedColorTextVariant).toString()
            val currentColor = resources.getString(checkedColorTextVariant.toInt())
            Log.d("MyLog", "checkedColorText = $checkedColorTextVariant, currentColor = $currentColor")
//            textSchemaImage.background =
//            context?.resources?.getColor(currentColor, null)
//                ?.let { textSchemaImage.setColorFilter(it) };
        }

//        if (checkedCornerSizeVariant != 1F) {
//            when (checkedCornerSizeVariant) {
//                true -> bigCorner.isChecked = true
//                else -> withoutCorner.isChecked = false
//            }
//        }

        cornerRound.setOnCheckedChangeListener { group, checkedId ->
            checkedCornerSizeVariant = when (checkedId) {
                R.id.cb_card_settings_dialog_yes -> {
                    cardViewTextureImage.radius = CARD_CORNER_SIZE
                    true
                }
                else -> {
                    cardViewTextureImage.radius = 0f
                    false
                }
            }

        }

        if (checkedFormAvatarVariant != "") {
            if (checkedFormAvatarVariant == "oval") ovalButton.isChecked = true
            else squareButton.isChecked = true
        }

        formAvatar.setOnCheckedChangeListener { group, checkedId ->
            checkedFormAvatarVariant = when (checkedId) {
                R.id.cb_card_settings_dialog_oval -> {
                    cardAvatar.setImageResource(R.drawable.ic_user_circle)
                    "oval"
                }
                else -> {
                    cardAvatar.setImageResource(R.drawable.ic_user_square)
                    "square"
                }
            }
        }

        saveButton.setOnClickListener {
            val cardSettings = CardSettings(
                cardTexture = checkedCardTextureVariant.toString(),
                cardTextColor = checkedColorTextVariant.toInt(),
                cardCorner = checkedCornerSizeVariant,
                cardAvatarForm = checkedFormAvatarVariant
            )
        }

//        personInfoButton.setOnClickListener {
//            parentFragmentManager.setFragmentResult(
//                REQUEST_KEY,
//                bundleOf(CHECKED_OPTION to PERSON_INFO_STATUS, CURRENT_CARD_ID to currentCardId)
//            )
//            dismiss()
//        }
//        additionalInfoButton.setOnClickListener {
//            parentFragmentManager.setFragmentResult(
//                REQUEST_KEY,
//                bundleOf(CHECKED_OPTION to ADD_INFO_STATUS, CURRENT_CARD_ID to currentCardId)
//            )
//            dialog?.dismiss()
//        }
//        settingsCardButton.setOnClickListener {
//            parentFragmentManager.setFragmentResult(
//                REQUEST_KEY,
//                bundleOf(CHECKED_OPTION to SETTINGS_CARD_STATUS, CURRENT_CARD_ID to currentCardId)
//            )
//            dialog?.dismiss()
//        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        //        val KEY_RESPONSE = "key response"
        val CHECKED_OPTION = "checked option"
        val PERSON_INFO_STATUS = "person information status"
        val ADD_INFO_STATUS = "additional information status"
        val SETTINGS_CARD_STATUS = "settings card status"


        val TAG = CardSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = CardSettingsDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Long, String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
                    if (status != null) {
                        listener.invoke(cardId, status)
                    }
                })
        }
    }
}
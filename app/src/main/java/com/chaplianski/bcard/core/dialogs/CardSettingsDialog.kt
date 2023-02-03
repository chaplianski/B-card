package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
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
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.core.viewmodels.CardSettingsDialogViewModel
import com.chaplianski.bcard.databinding.DialogCardSettingsBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.CardSettings
import javax.inject.Inject


class CardSettingsDialog : DialogFragment() {

    private var _binding: DialogCardSettingsBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val cardSettingsDialogViewModel: CardSettingsDialogViewModel by viewModels { vmFactory }

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
        val textColor1 = binding.rgCardSettingsDialogTextColorBlack
        val textColor2 = binding.rgCardSettingsDialogTextColorPurple
        val textColor3 = binding.rgCardSettingsDialogTextColorViolet
        val textColor4 = binding.rgCardSettingsDialogTextColorRed
        val textColor5 = binding.rgCardSettingsDialogTextColorDarkYellow
        val textColor6 = binding.rgCardSettingsDialogTextColorLightYellow
        val textColor7 = binding.rgCardSettingsDialogTextColorWhite

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
        var checkedCardTextureVariant = DEFAULT_CARD_TEXTURE
        var checkedCornerSizeVariant = DEFAULT_CARD_CORNER
        var checkedFormAvatarVariant = DEFAULT_CARD_FORM_PHOTO
        var checkedColorTextVariant = DEFAULT_CARD_TEXT_COLOR


        if (currentCardId != null && currentCardId != 0L) {
            cardSettingsDialogViewModel.getCardData(currentCardId)
        } else {
            if (checkedCardTextureVariant != "") {
                when (checkedCardTextureVariant) {
                    "paper_01" -> texture1.isChecked = true
                    "paper_43" -> texture2.isChecked = true
                    "paper_08" -> texture3.isChecked = true
                    "paper_015" -> texture4.isChecked = true
                    "paper_10" -> texture5.isChecked = true
                    "paper_019" -> texture6.isChecked = true
                    "paper_02" -> texture7.isChecked = true
                    "paper_033" -> texture8.isChecked = true
                    "paper_025" -> texture9.isChecked = true
                    "paper_03" -> texture10.isChecked = true
                    "paper_06" -> texture11.isChecked = true
                    "paper_09" -> texture12.isChecked = true
                }
            }

            when (checkedColorTextVariant) {
                resources.getString(R.color.black) -> {
                    textColor1.isChecked = true
                }
                resources.getString(R.color.purple_700) -> {
                    textColor2.isChecked = true
                }
                resources.getString(R.color.violet) -> {
                    textColor3.isChecked = true
                }
                resources.getString(R.color.red) -> {
                    textColor4.isChecked = true
                }
                resources.getString(R.color.dark_yellow) -> {
                    textColor5.isChecked = true
                }
                resources.getString(R.color.light_yellow) -> {
                    textColor6.isChecked = true
                }
                resources.getString(R.color.white) -> {
                    textColor7.isChecked = true
                }
            }

            if (checkedCornerSizeVariant) bigCorner.isChecked = true
            else withoutCorner.isChecked = true

            if (checkedFormAvatarVariant == AVATAR_FORM_OVAL) {
                cardAvatar.setImageResource(R.drawable.ic_user_circle)
                ovalButton.isChecked = true
            } else {
                cardAvatar.setImageResource(R.drawable.ic_user_square)
                squareButton.isChecked = true
            }
        }

        cardSettingsDialogViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->
            Log.d("MyLog", "currentCard = $card")
            checkedCardTextureVariant = card.cardTexture
            checkedColorTextVariant = card.cardTextColor
            checkedCornerSizeVariant = card.isCardCorner
            checkedFormAvatarVariant = card.cardFormPhoto

            if (checkedCardTextureVariant != "") {
                when (checkedCardTextureVariant) {
                    "paper_01" -> texture1.isChecked = true
                    "paper_43" -> texture2.isChecked = true
                    "paper_08" -> texture3.isChecked = true
                    "paper_015" -> texture4.isChecked = true
                    "paper_10" -> texture5.isChecked = true
                    "paper_019" -> texture6.isChecked = true
                    "paper_02" -> texture7.isChecked = true
                    "paper_033" -> texture8.isChecked = true
                    "paper_025" -> texture9.isChecked = true
                    "paper_03" -> texture10.isChecked = true
                    "paper_06" -> texture11.isChecked = true
                    "paper_09" -> texture12.isChecked = true
                }
            }

            when (checkedColorTextVariant) {
                resources.getString(R.color.black) -> {
                    textColor1.isChecked = true
                }
                resources.getString(R.color.purple_700) -> {
                    textColor2.isChecked = true
                }
                resources.getString(R.color.violet) -> {
                    textColor3.isChecked = true
                }
                resources.getString(R.color.red) -> {
                    textColor4.isChecked = true
                }
                resources.getString(R.color.dark_yellow) -> {
                    textColor5.isChecked = true
                }
                resources.getString(R.color.light_yellow) -> {
                    textColor6.isChecked = true
                }
                resources.getString(R.color.white) -> {
                    textColor7.isChecked = true
                }
            }

            if (checkedCornerSizeVariant) bigCorner.isChecked = true
            else withoutCorner.isChecked = true

            if (checkedFormAvatarVariant == AVATAR_FORM_OVAL) {
                cardAvatar.setImageResource(R.drawable.ic_user_circle)
                ovalButton.isChecked = true
            } else {
                cardAvatar.setImageResource(R.drawable.ic_user_square)
                squareButton.isChecked = true
            }

        }

        val textureResource = this.resources.getIdentifier(
            checkedCardTextureVariant,
            "drawable",
            activity?.packageName
        )
        cardTextureImage.setImageResource(textureResource)
//        cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
        val currentColor = Color.parseColor(checkedColorTextVariant)
        textSchemaImage.setColorFilter(currentColor)

        if (checkedCornerSizeVariant) cardViewTextureImage.radius = CARD_CORNER_SIZE


        var isChecking = true
        var currentTexture = 0
        cardTexture1.setOnCheckedChangeListener { group, checkedId ->

            val checkedItem = group.checkedRadioButtonId

            if (checkedId != -1 && isChecking) {
                isChecking = false
//                Log.d("MyLog", "clear Text 2")
                cardTexture2.clearCheck()
            }
            isChecking = true
            currentTexture = when (checkedId) {
//            checkedCardTextureVariant = when (checkedId) {
                R.id.radioButton_card_texture_1 -> R.drawable.paper_01
                R.id.radioButton_card_texture_2 -> R.drawable.paper_43
                R.id.radioButton_card_texture_3 -> R.drawable.paper_08
                R.id.radioButton_card_texture_4 -> R.drawable.paper_015
                R.id.radioButton_card_texture_5 -> R.drawable.paper_10
                R.id.radioButton_card_texture_6 -> R.drawable.paper_019
                else -> {
                    R.drawable.paper_015
                }
            }
//            cardTextureImage.setImageResource(textureResource)
            checkedCardTextureVariant =
                context?.resources?.getResourceEntryName(currentTexture).toString()
            val textureResource = this.resources.getIdentifier(
                checkedCardTextureVariant,
                "drawable",
                activity?.packageName
            )
            cardTextureImage.setImageResource(textureResource)

//            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), currentTexture)
//            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
//            Log.d("MyLog",
//                context?.resources?.getResourceEntryName(checkedCardTextureVariant).toString()
//            )
        }

        cardTexture2.setOnCheckedChangeListener { group, checkedId ->

            val checkedItem = group.checkedRadioButtonId
//            Log.d("MyLog", "checkedId = $checkedId")
            if (checkedId != -1 && isChecking) {
                isChecking = false
                Log.d("MyLog", "clear Text 1")
                cardTexture1.clearCheck()
            }
            isChecking = true
            currentTexture = when (checkedId) {
//            checkedCardTextureVariant = when (checkedId) {
                R.id.radioButton_card_texture_7 -> R.drawable.paper_02
                R.id.radioButton_card_texture_8 -> R.drawable.paper_033
                R.id.radioButton_card_texture_9 -> R.drawable.paper_025
                R.id.radioButton_card_texture_10 -> R.drawable.paper_03
                R.id.radioButton_card_texture_11 -> R.drawable.paper_06
                R.id.radioButton_card_texture_12 -> R.drawable.paper_09
                else -> {
                    R.drawable.paper_034
                }
            }
//            val textureResource = this.resources.getIdentifier(checkedCardTextureVariant, "drawable", activity?.packageName)
//            cardTextureImage.setImageResource(textureResource)
//                        cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), currentTexture)

            checkedCardTextureVariant =
                context?.resources?.getResourceEntryName(currentTexture).toString()
            val textureResource = this.resources.getIdentifier(
                checkedCardTextureVariant,
                "drawable",
                activity?.packageName
            )
            cardTextureImage.setImageResource(textureResource)
//            cardTextureImage.background = AppCompatResources.getDrawable(requireContext(), checkedCardTextureVariant)
//            Log.d("MyLog",
//                context?.resources?.getResourceEntryName(checkedCardTextureVariant).toString()
//            )
        }

        Log.d("MyLog", "checkedCardTextureVariant = $checkedCardTextureVariant")

//        if (currentTexture != 0) {

//        }




        cardTextSchema.setOnCheckedChangeListener { group, checkedId ->
            val nameCheckedColorTextVariant = when (checkedId) {
                R.id.rg_card_settings_dialog_text_color_black -> R.color.black
                R.id.rg_card_settings_dialog_text_color_purple -> R.color.purple_700
                R.id.rg_card_settings_dialog_text_color_violet -> R.color.violet
                R.id.rg_card_settings_dialog_text_color_red -> R.color.red
                R.id.rg_card_settings_dialog_text_color_dark_yellow -> R.color.dark_yellow
                R.id.rg_card_settings_dialog_text_color_light_yellow -> R.color.light_yellow
                R.id.rg_card_settings_dialog_text_color_white -> R.color.white
                else -> {
                    R.color.black
                }
            }
            val currentColor = Color.parseColor(getString(nameCheckedColorTextVariant))
            textSchemaImage.setColorFilter(currentColor)
            checkedColorTextVariant = getString(nameCheckedColorTextVariant)
        }



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

//        if (checkedFormAvatarVariant != "") {

//        }

        formAvatar.setOnCheckedChangeListener { group, checkedId ->
            checkedFormAvatarVariant = when (checkedId) {
                R.id.cb_card_settings_dialog_oval -> {
                    cardAvatar.setImageResource(R.drawable.ic_user_circle)
                    AVATAR_FORM_OVAL
                }
                else -> {
                    cardAvatar.setImageResource(R.drawable.ic_user_square)
                    AVATAR_FORM_SQUARE
                }
            }
        }

        saveButton.setOnClickListener {
            val cardSettings = CardSettings(
                cardId = currentCardId ?: 0L,
                cardTexture = checkedCardTextureVariant.toString(),
                cardTextColor = checkedColorTextVariant,
                cardCorner = checkedCornerSizeVariant,
                cardAvatarForm = checkedFormAvatarVariant
            )
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CURRENT_CARD_ID to currentCardId,
                    CHECKED_OPTION to SAVE_STATUS,
                    SETTINGS_CARD_INFO to cardSettings
                )
            )
            dismiss()
        }
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CURRENT_CARD_ID to currentCardId,
                    CHECKED_OPTION to CANCEL_STATUS,
                    SETTINGS_CARD_INFO to CardSettings()
                )
            )
            dismiss()
        }
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

    companion object {

        val CHECKED_OPTION = "checked option"
        val SAVE_STATUS = "card settings save button status"
        val CANCEL_STATUS = "card settings cancel button status"
        val SETTINGS_CARD_INFO = "card settings  information"


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
            listener: (Long, String, Parcelable) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    Log.d("MyLog", "setupListener set")
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
                    val cardSettingsInfo = result.getParcelable<CardSettings>(SETTINGS_CARD_INFO)
                    Log.d("MyLog", "cardInfo = $cardSettingsInfo, status = $status")
                    if (cardSettingsInfo != null && status != null) {

                        listener.invoke(cardId, status, cardSettingsInfo)
                    }
                })
        }
    }
}
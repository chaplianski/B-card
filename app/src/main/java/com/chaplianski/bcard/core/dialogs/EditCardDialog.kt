package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogEditCardBinding


class EditCardDialog : DialogFragment() {

    private var _binding: DialogEditCardBinding? = null
    val binding get() = _binding!!

    private val isPersonInfoEnable: Boolean
        get() = requireArguments().getBoolean(PERSON_INFO_ENABLE)

    private val isAdditionalInfoEnable: Boolean
        get() = requireArguments().getBoolean(
            ADDITIONAL_INFO_ENABLE
        )
    private val isCardSettingsEnable: Boolean
        get() = requireArguments().getBoolean(
            CARD_SETTING_INFO_ENABLE
        )
//    val isPersonInfoEnable = arguments?.getBoolean(PERSON_INFO_ENABLE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 300
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogEditCardBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_edit_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val personInfoButton = binding.tvEditCardDialogPersonInfo
        val additionalInfoButton = binding.tvEditCardDialogAddInfo
        val settingsCardButton = binding.tvEditCardDialogSettings
        val personInfoButtonIcon = binding.tvEditCardDialogPersonInfoPlus
        val additionalInfoButtonIcon = binding.tvEditCardDialogAddInfoPlus
        val cardSettingsButtonIcon = binding.tvEditCardDialogCardSettingsPlus
        val saveSettingsButton = binding.tvEditCardDialogSave
        val cancelButton = binding.tvEditCardDialogCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        Log.d("MyLog", "edit dialog onViewCreated, id = $currentCardId")

        if (isPersonInfoEnable == true) personInfoButtonIcon.isVisible = true
        if (isAdditionalInfoEnable == true) additionalInfoButtonIcon.isVisible = true
        if (isCardSettingsEnable == true) cardSettingsButtonIcon.isVisible = true

        personInfoButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CHECKED_OPTION to PERSON_INFO_STATUS,
                    CURRENT_CARD_ID to currentCardId
                )
            )
            dismiss()
        }
        additionalInfoButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CHECKED_OPTION to ADD_INFO_STATUS,
                    CURRENT_CARD_ID to currentCardId
                )
            )
            dialog?.dismiss()
        }
        settingsCardButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CHECKED_OPTION to SETTINGS_CARD_STATUS,
                    CURRENT_CARD_ID to currentCardId
                )
            )
            dialog?.dismiss()
        }
        saveSettingsButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to SAVE_STATUS, CURRENT_CARD_ID to currentCardId)
            )
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val PERSON_INFO_STATUS = "person information status"
        val ADD_INFO_STATUS = "additional information status"
        val SAVE_STATUS = "save button status"
        val SETTINGS_CARD_STATUS = "settings card status"
        val PERSON_INFO_ENABLE = "person information enable"
        val ADDITIONAL_INFO_ENABLE = "additional information enable"
        val CARD_SETTING_INFO_ENABLE = "card settings information enable"


        val TAG = EditCardDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(
            manager: FragmentManager,
            currentCardId: Long,
            isPersonInfoEnable: Boolean,
            isAdditionalInfoEnable: Boolean,
            isCardSettingsEnable: Boolean
        ) {
            val dialogFragment = EditCardDialog()
            Log.d(
                "MyLog",
                "show Edit Dialog, cardId = $currentCardId"
            )
//            val bundle = Bundle()
//            bundle.putLong(CURRENT_CARD_ID, currentCardId)
//            bundle.putBoolean(PERSON_INFO_ENABLE, isPersonInfoEnable)
//            bundle.putBoolean(ADDITIONAL_INFO_ENABLE, isAdditionalInfoEnable)
//            bundle.putBoolean(CARD_SETTING_INFO_ENABLE, isCardSettingsEnable)
//            dialogFragment.arguments = bundle

            dialogFragment.arguments = bundleOf(
                PERSON_INFO_ENABLE to isPersonInfoEnable,
                ADDITIONAL_INFO_ENABLE to isAdditionalInfoEnable,
                CARD_SETTING_INFO_ENABLE to isCardSettingsEnable,
                CURRENT_CARD_ID to currentCardId,
            )
//            Log.d("MyLog", "bundle = ${bundle}")
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
//                    val isPersonInfoEnable = result.getBoolean(PERSON_INFO_ENABLE, false)
//                    val isAdditionalInfoEnable = result.getBoolean(ADDITIONAL_INFO_ENABLE, false)
//                    val isCardSettingsEnable = result.getBoolean(CARD_SETTING_INFO_ENABLE, false)
                    if (status != null) {
                        listener.invoke(
                            cardId,
                            status
                        ) //, isPersonInfoEnable, isAdditionalInfoEnable, isCardSettingsEnable)
                    }
                })
        }
    }


}
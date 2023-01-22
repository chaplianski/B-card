package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.dialogs.ShareContactsDialog.Companion.CHECKED_OPTION
import com.chaplianski.bcard.core.dialogs.ShareContactsDialog.Companion.LOAD_STATUS
import com.chaplianski.bcard.core.dialogs.ShareContactsDialog.Companion.SAVE_STATUS
import com.chaplianski.bcard.core.helpers.PhotoPicker
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogEditCardBinding
import com.google.android.material.textview.MaterialTextView


class EditCardDialog : DialogFragment() {

    private var _binding: DialogEditCardBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 30
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
        val cancelButton = binding.tvEditCardDialogCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        personInfoButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to PERSON_INFO_STATUS, CURRENT_CARD_ID to currentCardId)
            )
            dismiss()
        }
        additionalInfoButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to ADD_INFO_STATUS, CURRENT_CARD_ID to currentCardId)
            )
            dialog?.dismiss()
        }
        settingsCardButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to SETTINGS_CARD_STATUS, CURRENT_CARD_ID to currentCardId)
            )
            dialog?.dismiss()
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

        //        val KEY_RESPONSE = "key response"
        val CHECKED_OPTION = "checked option"
        val PERSON_INFO_STATUS = "person information status"
        val ADD_INFO_STATUS = "additional information status"
        val SETTINGS_CARD_STATUS = "settings card status"


        val TAG = EditCardDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = EditCardDialog()
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
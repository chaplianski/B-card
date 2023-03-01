package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogShareContactsBinding


class ShareContactsDialog : DialogFragment() {

    private var _binding: DialogShareContactsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 30
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogShareContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_share_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveVcfButton = binding.tvShareDialogSaveVcf
        val saveCsvButton = binding.tvShareDialogSaveCsv
        val loadFromFileButton = binding.tvShareDialogLoadVcf
        val cancelButton = binding.tvShareDialogCancel
        val loadFromAccountButton = binding.tvShareDialogLoadFromAccount
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID, -1L)

        saveVcfButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to SAVE_VCF_OPTION)
            )
            dismiss()
        }
        saveCsvButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to SAVE_CSV_OPTION)
            )
            dismiss()
        }
        loadFromFileButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to LOAD_FROM_FILE_OPTION)
            )
            dialog?.dismiss()
        }
        loadFromAccountButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to LOAD_FROM_GOOGLE_ACCOUNT_OPTION)
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

        val CHECKED_OPTION = "checked share option"
        val SAVE_VCF_OPTION = "save vcf share option"
        val SAVE_CSV_OPTION = "save csv share option"
        val LOAD_FROM_FILE_OPTION = "load from file option"
        val LOAD_FROM_GOOGLE_ACCOUNT_OPTION = "load from google account option"



        val TAG = ShareContactsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"
        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = ShareContactsDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (Long, String) -> Unit) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, FragmentResultListener { key, result ->
                val cardId = result.getLong(CURRENT_CARD_ID)
                val status = result.getString(CHECKED_OPTION)
                if (status != null) {
                    listener.invoke(cardId, status)
                }
            })
        }
    }
}
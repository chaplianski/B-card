package com.chaplianski.bcard.core.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogAboutSettingsBinding

class AboutSettingsDialog :
    BasisDialogFragment<DialogAboutSettingsBinding>(DialogAboutSettingsBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val okButton = binding.btAboutDialogOk
        val policyButton = binding.tvAboutDialogPolicy
        val termsButton = binding.tvAboutDialogTerms
        val textField = binding.tvAboutDialogContent

        okButton.setOnClickListener {
            dismiss()
        }

        policyButton.setOnClickListener {
            textField.text = "Any text about policy"
        }

        termsButton.setOnClickListener {
            textField.text = "Any text about terms"
        }
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val TAG = AboutSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(
            manager: FragmentManager,
        ) {
            val dialogFragment = AboutSettingsDialog()
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
                        listener.invoke(
                            cardId,
                            status
                        )
                    }
                })
        }
    }
}
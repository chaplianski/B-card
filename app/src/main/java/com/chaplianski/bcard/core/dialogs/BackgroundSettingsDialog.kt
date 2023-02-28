package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.helpers.CardDecorResources
import com.chaplianski.bcard.core.utils.CURRENT_BACKGROUND
import com.chaplianski.bcard.core.utils.DEFAULT_BACKGROUND
import com.chaplianski.bcard.core.utils.RESOURCE_TYPE_DRAWABLE
import com.chaplianski.bcard.databinding.DialogBackgroundSettingsBinding


class BackgroundSettingsDialog :
    BasisDialogFragment<DialogBackgroundSettingsBinding>(DialogBackgroundSettingsBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage = binding.ivBackgroundSettingsDialog
        val setupButton = binding.btBackgroundSettingsDialogSetup
        val cancelButton = binding.btBackgroundSettingsDialogCancel
        val backgroundRadioGroup = binding.radiogroupBackgroundSettingsDialog
        val background1RadioButton = binding.rbBackgroundSettingsDialog1
        val background2RadioButton = binding.rbBackgroundSettingsDialog2
        val background3RadioButton = binding.rbBackgroundSettingsDialog3
        val background4RadioButton = binding.rbBackgroundSettingsDialog4
        val background5RadioButton = binding.rbBackgroundSettingsDialog5
        val background6RadioButton = binding.rbBackgroundSettingsDialog6

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var checkedBackgroundVariant = sharedPref?.getString(CURRENT_BACKGROUND, DEFAULT_BACKGROUND)
        val cardDecorResource = CardDecorResources()

        val backgroundResource = this.resources.getIdentifier(
            checkedBackgroundVariant,
            RESOURCE_TYPE_DRAWABLE,
            activity?.packageName
        )
        backgroundImage.setImageResource(backgroundResource)

        if (checkedBackgroundVariant != "") {
            when (checkedBackgroundVariant) {
                cardDecorResource.background.keys.elementAt(0) -> background1RadioButton.isChecked = true
                cardDecorResource.background.keys.elementAt(1) -> background2RadioButton.isChecked = true
                cardDecorResource.background.keys.elementAt(2) -> background3RadioButton.isChecked = true
                cardDecorResource.background.keys.elementAt(3) -> background4RadioButton.isChecked = true
                cardDecorResource.background.keys.elementAt(4) -> background5RadioButton.isChecked = true
                cardDecorResource.background.keys.elementAt(5) -> background6RadioButton.isChecked = true
            }
        }

        backgroundRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val currentBackground = when(i){
                R.id.rb_background_settings_dialog_1 -> cardDecorResource.background.values.elementAt(0)
                R.id.rb_background_settings_dialog_2 -> cardDecorResource.background.values.elementAt(1)
                R.id.rb_background_settings_dialog_3 -> cardDecorResource.background.values.elementAt(2)
                R.id.rb_background_settings_dialog_4 -> cardDecorResource.background.values.elementAt(3)
                R.id.rb_background_settings_dialog_5 -> cardDecorResource.background.values.elementAt(4)
                R.id.rb_background_settings_dialog_6 -> cardDecorResource.background.values.elementAt(5)
                else -> R.drawable.background_32
            }

            checkedBackgroundVariant =
                context?.resources?.getResourceEntryName(currentBackground).toString()
            val textureResource = this.resources.getIdentifier(
                checkedBackgroundVariant,
                RESOURCE_TYPE_DRAWABLE,
                activity?.packageName
            )
            backgroundImage.setImageResource(textureResource)
        }

        setupButton.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            sharedPref?.edit()?.putString(CURRENT_BACKGROUND, checkedBackgroundVariant)?.apply()
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to SETUP_STATUS, BACKGROUND_VARIANT to checkedBackgroundVariant)
            )
            dismiss()
        }
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS, BACKGROUND_VARIANT to checkedBackgroundVariant)
            )
            dismiss()
        }
    }

    companion object {

        val CHECKED_OPTION = "checked background option"
        val SETUP_STATUS = "setup status"
        val CANCEL_STATUS = "cancel status"
        val BACKGROUND_VARIANT = "background variant"
        val TAG = BackgroundSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager) {
            val dialogFragment = BackgroundSettingsDialog()
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String, String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val background = result.getString(BACKGROUND_VARIANT)
                    val status = result.getString(CHECKED_OPTION)
                    if (status != null && background != null) {
                        listener.invoke(status, background)
                    }
                })
        }
    }
}
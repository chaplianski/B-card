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
import com.chaplianski.bcard.databinding.DialogCheckDoubleCardListBinding
import com.chaplianski.bcard.databinding.DialogChooseVariantAddingPhotoBinding


class ChooseVariantAddingPhotoDialog :
    BasisDialogFragment<DialogChooseVariantAddingPhotoBinding>(DialogChooseVariantAddingPhotoBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val galleryButton = binding.tvChooseVariantAddingPhotoDialogGallery
        val cameraButton = binding.tvChooseVariantAddingPhotoDialogCamera
        val cancelButton = binding.tvChooseVariantAddingPhotoDialogCancel

        galleryButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CHECKED_OPTION to GALLERY_STATUS)
            )
            dismiss()
        }

        cameraButton.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CHECKED_OPTION to CAMERA_STATUS)
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val GALLERY_STATUS = "save status"
        val CAMERA_STATUS = "load status"
        val TAG = ChooseVariantAddingPhotoDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(manager: FragmentManager) {
            val dialogFragment = ChooseVariantAddingPhotoDialog()
            dialogFragment.arguments = bundleOf(
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (String) -> Unit) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, FragmentResultListener { key, result ->
                val status = result.getString(CHECKED_OPTION)
                if (status != null) {
                    listener.invoke(status)
                }
            })
        }
    }
}
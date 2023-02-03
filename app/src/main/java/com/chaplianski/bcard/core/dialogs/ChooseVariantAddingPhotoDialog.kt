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
import com.chaplianski.bcard.databinding.DialogChooseVariantAddingPhotoBinding


class ChooseVariantAddingPhotoDialog : DialogFragment() {

    private var _binding: DialogChooseVariantAddingPhotoBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        //      params?.x = 300
        params?.y = 300
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogChooseVariantAddingPhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_share_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val galleryButton = binding.tvChooseVariantAddingPhotoDialogGallery
        val cameraButton = binding.tvChooseVariantAddingPhotoDialogCamera
        val cancelButton = binding.tvChooseVariantAddingPhotoDialogCancel
//        val currentCardId = arguments?.getLong(CURRENT_CARD_ID, -1L)

        galleryButton.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CHECKED_OPTION to GALLERY_STATUS)
            )
            dismiss()
        }
//
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        //        val KEY_RESPONSE = "key response"
        val CHECKED_OPTION = "checked option"
        val GALLERY_STATUS = "save status"
        val CAMERA_STATUS = "load status"



        val TAG = ChooseVariantAddingPhotoDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"
        //
        fun show(manager: FragmentManager) {
            val dialogFragment = ChooseVariantAddingPhotoDialog()
//            val kids = ArrayList(kidList)
            dialogFragment.arguments = bundleOf(
//                CURRENT_TASK to task.mapToEditTask(),
//                "kids" to kids,
//                CURRENT_CHORE_ID to task.choreId,
//                TASK_STATUS to taskStatus
//                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (String) -> Unit) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, FragmentResultListener { key, result ->
//                val cardId = result.getLong(CURRENT_CARD_ID)
                val status = result.getString(CHECKED_OPTION)
                if (status != null) {
                    listener.invoke(status)
                }
            })
        }
    }
}
package com.chaplianski.bcard.presenter.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.databinding.FragmentEditCardBinding
import com.chaplianski.bcard.presenter.adapters.CardColorAdapter
import com.chaplianski.bcard.presenter.adapters.StrokeColorAdapter
import com.chaplianski.bcard.presenter.helpers.PhotoPicker


class EditCardFragment : Fragment() {

      var _binding: FragmentEditCardBinding? = null
      val binding: FragmentEditCardBinding get() = _binding!!
    lateinit var photoPicker: PhotoPicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
          _binding = FragmentEditCardBinding.inflate(inflater, container, false)
          return binding.root
    }

      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

          photoPicker =
              PhotoPicker(context!!, requireActivity().activityResultRegistry) { uri ->
                  savePhotoToAnswer(uri)
              }

          val personInfo: ConstraintLayout = binding.personInfo.clUserPersonInfo
          val personInfoButton: TextView = binding.tvEditFragmentProfileTitle
          val avatar: ImageView = binding.personInfo.ivUserPersonInfoAvatar



          val additionInfo: ConstraintLayout = binding.additionInfo.clUserInfo
          val additionInfoButton: TextView = binding.tvEditFragmentAdditionInfo
          val profileInfo: EditText = binding.additionInfo.userEditInformationProfileInfo
          val profSkills: EditText = binding.additionInfo.userEditInformationProfSkills
          val education: EditText = binding.additionInfo.userEditInformationEducation
          val workExperience: EditText = binding.additionInfo.userEditInformationWorkExperience
          val reference: EditText = binding.additionInfo.userEditInformationReference

          val cardSettings: ConstraintLayout = binding.cardSettings.clCardSettings
          val cardSettingsButton: TextView = binding.tvEditFragmentProfileCardSettings
          val cardColors: RecyclerView = binding.cardSettings.rvCardSettingsColor
          val strokeColors: RecyclerView = binding.cardSettings.rvCardSettingsStrokeColor


          val saveButtonEdit: Button = binding.btEditCardSave


          personInfoButton.setOnClickListener {
              Log.d("MyLog", "click")
              personInfo.visibility =
                  if (personInfo.isVisible) View.GONE
                  else View.VISIBLE
              if (additionInfo.isVisible) additionInfo.visibility = View.GONE
              if (cardSettings.isVisible) cardSettings.visibility = View.GONE
          }

          additionInfoButton.setOnClickListener {
              Log.d("MyLog", "click")
              additionInfo.visibility =
                  if (additionInfo.isVisible) View.GONE
                  else View.VISIBLE
              if (personInfo.isVisible) personInfo.visibility = View.GONE
              if (cardSettings.isVisible) cardSettings.visibility = View.GONE
          }

          cardSettingsButton.setOnClickListener {
              Log.d("MyLog", "click")
              cardSettings.visibility =
                  if (cardSettings.isVisible) View.GONE
                  else View.VISIBLE
              if (additionInfo.isVisible) additionInfo.visibility = View.GONE
              if (personInfo.isVisible) personInfo.visibility = View.GONE
          }



          saveButtonEdit.setOnClickListener {
              findNavController().navigate(R.id.action_editCardFragment_to_cardsFragment)
//            cardsFragmentViewModel.addCard()
          }

          avatar.setOnClickListener {
              addAvatarDialog()
          }

          val cardColorsAdapter = CardColorAdapter()
          cardColors.layoutManager = GridLayoutManager(context,5)
          cardColors.adapter = cardColorsAdapter

          val strokeColorsAdapter = StrokeColorAdapter()
          strokeColors.layoutManager = GridLayoutManager(context, 6)
          strokeColors.adapter = strokeColorsAdapter






      }


      override fun onDestroy() {
            _binding = null
            super.onDestroy()
      }

    private fun savePhotoToAnswer(imageUri: Uri?) {
//        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
//        val questionId = sharedPref?.getString(Constants.CURRENT_QUESTION_ID, "")

        val contentResolver = context?.getContentResolver()

        if (imageUri != null) {
            if (contentResolver != null) {
//                cardsFragmentViewModel.insertPhoto(imageUri)
            }

        }
    }

    private fun addAvatarDialog() {
        val photoDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Choose variant of adding photo")
        builder.setItems(photoDialogItems) { _, position ->
            when (position) {
                0 -> photoPicker.pickPhoto()
                1 -> photoPicker.takePhoto()
            }
        }
        builder.show()
    }
}
package com.chaplianski.bcard.presenter.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentRegistrationBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.presenter.factories.RegistrationViewModelFactory
import com.chaplianski.bcard.presenter.utils.CURRENT_USER_ID
import com.chaplianski.bcard.presenter.viewmodels.RegistrationFragmentViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class RegistrationFragment : Fragment(R.layout.fragment_registration) {

//    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModelFactory
    val registrationFragmentViewModel: RegistrationFragmentViewModel by viewModels {registrationViewModelFactory  }

    var _binding : FragmentRegistrationBinding? = null
    val binding get() = _binding!!

    override fun onAttach(context: Context) {
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .registrationFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        auth = FirebaseAuth.getInstance()//Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val nameField = binding.etRegistrationFragmentName
        val nameText = binding.etRegistrationFragmentName
        val surnameField = binding.otfRegistrationFragmentSurname
        val surnameText = binding.etRegistrationFragmentSuname
        val emailField = binding.otfRegistrationFragmentEmail
        val emailText = binding.etRegistrationFragmentEmail
        val phoneField = binding.otfRegistrationFragmentPhone
        val phoneText = binding.etRegistrationFragmentPhone
        val passwordField = binding.otfRegistrationFragmentPassword
        val passwordText = binding.etRegistrationFragmentPassword
        val passwordRepeatField = binding.otfRegistrationFragmentRepeatPassword
        val passwordRepeatText = binding.etRegistrationFragmentRepeatPassword
        val saveButton: Button = binding.btRegistrationFragmentSignup
//        val currentUser = auth.currentUser

        saveButton.setOnClickListener {

            val email = emailField.editText?.text.toString()
            val password = passwordField.editText?.text.toString()
            val passwordRepeat = passwordRepeatField.editText?.text.toString()

            if(!email.isEmpty() && !password.isEmpty() && password == passwordRepeat){
                registerUser(email, password)
            }else {
                if (email.isEmpty()) emailField.error = "Please enter email"
                if (password.isEmpty()) passwordField.error = "Please enter password"
                if (password != passwordRepeat) passwordRepeatField.error = "Password values are not the same"
            }
        }

        registrationFragmentViewModel.userId.observe(this.viewLifecycleOwner){
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            sharedPref?.edit()?.putLong(CURRENT_USER_ID, it)
            findNavController().navigate(R.id.action_registrationFragment_to_cardsFragment)
        }

    }

    fun registerUser(email: String, password: String){

        registrationFragmentViewModel.addUser(email, password)



//        val email = binding.etRegistrationFragmentEmail.text.toString()
//        val password = binding.etRegistrationFragmentPassword.text.toString()
        Log.d("MyLog", "Registration fun")

//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if(task.isSuccessful) {
//                    Log.d("MyLog", "Registration is successfully ended")
//                }
//            }.addOnFailureListener { exception ->
//                Log.d("MyLog", exception.message.toString())
//            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
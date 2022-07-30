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
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentRegistrationBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var auth: FirebaseAuth

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

        val name = binding.etRegistrationFragmentName
        val email = binding.etRegistrationFragmentEmail
        val password = binding.etRegistrationFragmentPassword

        auth = FirebaseAuth.getInstance()//Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val name = binding.etRegistrationFragmentName

        val emailField = binding.otfRegistrationFragmentEmail

        val passwordField = binding.otfRegistrationFragmentPassword
        val saveButton: Button = binding.btRegistrationFragmentSignup
        val currentUser = auth.currentUser

        saveButton.setOnClickListener {

            val email = emailField.editText?.text.toString()
            val password = passwordField.editText?.text.toString()
            Log.d("MyLog", "email = $email, password = $password")
            if(!email.isEmpty() && !password.isEmpty()){
                registerUser(email, password)
                Log.d("MyLog", "email = $email, password = $password")
            }
        }

    }

    fun registerUser(email: String, password: String){


//        val email = binding.etRegistrationFragmentEmail.text.toString()
//        val password = binding.etRegistrationFragmentPassword.text.toString()
        Log.d("MyLog", "Registration fun")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d("MyLog", "Registration is successfully ended")
                }
            }.addOnFailureListener { exception ->
                Log.d("MyLog", exception.message.toString())
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
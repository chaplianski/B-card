package com.chaplianski.bcard.core.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentLoginBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.core.factories.LoginFragmentViewModelFactory
import com.chaplianski.bcard.core.utils.LAST_USER_LOGIN
import com.chaplianski.bcard.core.utils.LAST_USER_PASSWORD
import com.chaplianski.bcard.core.viewmodels.LoginFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class LoginFragment : Fragment() {

    @Inject
    lateinit var loginFragmentViewModelFactory: LoginFragmentViewModelFactory
    val loginFragmentViewModel: LoginFragmentViewModel by viewModels { loginFragmentViewModelFactory }

    lateinit var auth: FirebaseAuth
    var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!

    override fun onAttach(context: Context) {
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .loginFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = binding.btLoginFragmentSignup
        val emailField = binding.otfLoginFragmentEmail
        val emailText = binding.etLoginFragmentEmail
        val passwordField = binding.otfLoginFragmentPassword
        val passwordText = binding.etLoginFragmentPassword
        val signInButton = binding.btLoginFragmentSignin
        val errorMessage = binding.tvLoginFragmentError
        val rememberCheck = binding.cbLoginFragmentRemember
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
//        auth = FirebaseAuth.getInstance()

        emailText.setText(sharedPref?.getString(LAST_USER_LOGIN, "").toString())
        passwordText.setText(sharedPref?.getString(LAST_USER_PASSWORD, "").toString())

        if (!emailField.editText?.text.toString().isEmpty()) rememberCheck.isChecked = true

        Log.d("MyLog", "enter email = ${sharedPref?.getString(LAST_USER_LOGIN, "")}")
        Log.d("MyLog", "enter password = ${sharedPref?.getString(LAST_USER_PASSWORD, "")}")

        signInButton.setOnClickListener {
            val email = emailField.editText?.text.toString()
            val password = passwordField.editText?.text.toString()
            val user = User(0, email, password)

            if (!email.isEmpty() && !password.isEmpty()) {
                loginFragmentViewModel.checkLoginUser(user)
            } else {
                if (email.isEmpty()) emailField.error = "Please enter email"
                if (password.isEmpty()) passwordField.error = "Please enter password"
            }
        }

        loginFragmentViewModel.loginResponse.observe(this.viewLifecycleOwner) {
            if (it == -1L) {
                errorMessage.visibility = View.VISIBLE
            } else {
                if (rememberCheck.isChecked) {
                    sharedPref?.edit()
                        ?.putString(LAST_USER_LOGIN, emailField.editText?.text.toString())?.apply()

                    sharedPref?.edit()
                        ?.putString(LAST_USER_PASSWORD, passwordField.editText?.text.toString())?.apply()

                    Log.d("MyLog", "email exit = ${emailField.editText?.text.toString()}")
                    Log.d("MyLog", "email exit = ${passwordField.editText?.text.toString()}")
                } else {
                    sharedPref?.edit()?.putString(LAST_USER_LOGIN, "")
                    sharedPref?.edit()?.putString(LAST_USER_PASSWORD, "")
                }
                findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)
            }
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
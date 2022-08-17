package com.chaplianski.bcard.presenter.ui

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
import com.chaplianski.bcard.presenter.factories.LoginFragmentViewModelFactory
import com.chaplianski.bcard.presenter.viewmodels.LoginFragmentViewModel
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
        val passwordField = binding.otfLoginFragmentPassword
        val signInButton = binding.btLoginFragmentSignin
        auth = FirebaseAuth.getInstance()

        signInButton.setOnClickListener {
            val email = emailField.editText?.text.toString()
            val password = passwordField.editText?.text.toString()
            val user = User (0, email, password)

        loginFragmentViewModel.checkLoginUser(user)



//            loginFragmentViewModel.checkLoginUser(email, password)
            loginFragmentViewModel.loginResponse.observe(this.viewLifecycleOwner){
                if (it == -1L){

                } else {
                    findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)
                }

            }
//
//            findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)


//            checkLoginUser(email, password)
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
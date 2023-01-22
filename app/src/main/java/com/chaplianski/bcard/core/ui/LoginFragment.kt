package com.chaplianski.bcard.core.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.dialogs.PersonInformationDialog
import com.chaplianski.bcard.databinding.FragmentLoginBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.core.factories.LoginFragmentViewModelFactory
import com.chaplianski.bcard.core.utils.CURRENT_USER_ID
import com.chaplianski.bcard.core.utils.LAST_USER_LOGIN
import com.chaplianski.bcard.core.utils.LAST_USER_PASSWORD
import com.chaplianski.bcard.core.viewmodels.LoginFragmentViewModel
import com.chaplianski.bcard.domain.model.PersonInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class LoginFragment : Fragment() {

    @Inject
    lateinit var loginFragmentViewModelFactory: LoginFragmentViewModelFactory
    val loginFragmentViewModel: LoginFragmentViewModel by viewModels { loginFragmentViewModelFactory }

//    lateinit var auth: FirebaseAuth
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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val registerButton = binding.btLoginFragmentSignup
//        val emailField = binding.otfLoginFragmentEmail
//        val emailText = binding.etLoginFragmentEmail
//        val passwordField = binding.otfLoginFragmentPassword
//        val passwordText = binding.etLoginFragmentPassword
        val startButton = binding.btLoginFragmentSignin
        val errorMessage = binding.tvLoginFragmentError
        val registrationPanel = binding.clLoginFragmentLeftLeftPanel
        val motionLayout = binding.motionLayoutLoginFragment
//        val buttonMotionLayout = binding.clLoginFragmentLeftLeftPanel
        val layoutSignIn = binding.layoutLoginFragmentSignIn.layoutSignIn
        val loginSignInField = binding.layoutLoginFragmentSignIn.otfLayoutSignInLogin
        val loginSignInText = binding.layoutLoginFragmentSignIn.etLayoutSignInLogin
        val passwordSignInField = binding.layoutLoginFragmentSignIn.otfLayoutSignInPassword
        val passwordSignInText = binding.layoutLoginFragmentSignIn.etLayoutSignInPassword
        val rememberPasswordSignInButton = binding.layoutLoginFragmentSignIn.cbLayoutSignInRemember
        val forgotPasswordSignInButton =
            binding.layoutLoginFragmentSignIn.btLayoutSignInForgotPassword
        val signUpSignInButton = binding.layoutLoginFragmentSignIn.btLayoutSignInSignUp

        val layoutForgotPassword = binding.layoutLoginFragmentForgotPassword.layoutForgotPassword
        val cancelForgotPassword =
            binding.layoutLoginFragmentForgotPassword.btLayoutForgotPasswordCancel

        val layoutSignUp = binding.layoutLoginFragmentSignUp.layoutSignUp
        val cancelSignUpButton = binding.layoutLoginFragmentSignUp.btLayoutSignUpCancel
        val loginSignUpText = binding.layoutLoginFragmentSignUp.etLayoutSignUpLogin
        val loginSignUpField = binding.layoutLoginFragmentSignUp.otfLayoutSignUpLogin
        val passwordSignUpText = binding.layoutLoginFragmentSignUp.etLayoutSignUpPassword
        val passwordSignUpField = binding.layoutLoginFragmentSignUp.otfLayoutSignUpPassword
        val repeatPasswordSignUpText =
            binding.layoutLoginFragmentSignUp.etLayoutSignUpRepeatPassword
        val repeatPasswordSignUpField =
            binding.layoutLoginFragmentSignUp.otfLayoutSignUpRepeatPassword
        val secretQuestionSignUpText =
            binding.layoutLoginFragmentSignUp.etLayoutSignUpSecretQuestion
        val secretQuestionSignUpField =
            binding.layoutLoginFragmentSignUp.otfLayoutSignUpSecretQuestion
        val secretAnswerSignUpText = binding.layoutLoginFragmentSignUp.etLayoutSignUpSecretAnswer
        val secretAnswerSignUpField = binding.layoutLoginFragmentSignUp.otfLayoutSignUpSecretAnswer
        val saveSignUpButton = binding.layoutLoginFragmentSignUp.btLayoutSignUpSave

//        val rememberCheck = binding.cbLoginFragmentRemember
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        var registrationPanelCount = true

        cancelForgotPassword.setOnClickListener {
            layoutForgotPassword.visibility = View.GONE
            layoutSignIn.visibility = View.VISIBLE
        }

        forgotPasswordSignInButton.setOnClickListener {
//            buttonMotionLayout.setTransition(R.id.click_forgot_password_button_forward)
//            buttonMotionLayout.transitionToEnd()
            layoutSignIn.visibility = View.GONE
            layoutForgotPassword.visibility = View.VISIBLE
        }

        signUpSignInButton.setOnClickListener {
            layoutSignIn.visibility = View.GONE
            layoutSignUp.visibility = View.VISIBLE
        }

        listenNoEmptyCondition(loginSignUpText, loginSignUpField)
        listenNoEmptyCondition(passwordSignUpText, passwordSignUpField)
        listenNoEmptyCondition(repeatPasswordSignUpText, repeatPasswordSignUpField)
        listenNoEmptyCondition(secretQuestionSignUpText, secretQuestionSignUpField)
        listenNoEmptyCondition(secretAnswerSignUpText, secretAnswerSignUpField)

        saveSignUpButton.setOnClickListener {
            if (loginSignUpText.text?.isEmpty() == true) loginSignUpField.error =
                resources.getString(R.string.please_enter_login)
            if (passwordSignUpText.text?.isEmpty() == true) passwordSignUpField.error =
                resources.getString(R.string.please_enter_password)
            if (repeatPasswordSignUpText.text?.isEmpty() == true) repeatPasswordSignUpField.error =
                resources.getString(R.string.please_enter_password)
            if (secretQuestionSignUpText.text?.isEmpty() == true) secretQuestionSignUpField.error =
                resources.getString(R.string.please_enter_secret_question)
            if (secretAnswerSignUpText.text?.isEmpty() == true) secretAnswerSignUpField.error =
                resources.getString(R.string.please_enter_secret_answer)
            if (passwordSignUpText.text.toString() != repeatPasswordSignUpText.text.toString()) repeatPasswordSignUpField.error =
                resources.getString(R.string.password_does_not_match)

            if (loginSignUpText.text?.isNotEmpty() == true && passwordSignInText.text?.isNotEmpty() == true && repeatPasswordSignUpText.text?.isNotEmpty() == true && secretQuestionSignUpText.text?.isNotEmpty() == true && secretAnswerSignUpText.text?.isNotEmpty() == true) {
                Log.d("MyLog", "save person info")
                val newUser = User(
                    id = 0L,
                    login = loginSignInText.text.toString(),
                    password = passwordSignUpText.text.toString(),
                    secretQuestion = secretQuestionSignUpText.text.toString(),
                    secretAnswer = secretAnswerSignUpText.text.toString()
                )
                loginFragmentViewModel.addUser(newUser)
        }

        loginFragmentViewModel.userId.observe(this.viewLifecycleOwner){
            sharedPref?.edit()?.putLong(CURRENT_USER_ID, it)?.apply()
//            findNavController().navigate(R.id.action_registrationFragment_to_cardsFragment)
        }

        cancelSignUpButton.setOnClickListener {
            layoutSignUp.visibility = View.GONE
            layoutSignIn.visibility = View.VISIBLE
        }


        registrationPanel.setOnClickListener {
            if (registrationPanelCount) {
                motionLayout.setTransition(R.id.click_left_panel_forward)
                motionLayout.transitionToEnd()
                registrationPanelCount = false
            } else {
                motionLayout.setTransition(R.id.click_left_panel_back)
                motionLayout.transitionToEnd()
                registrationPanelCount = true
            }

        }


        loginSignInText.doOnTextChanged { inputText, _, _, _ ->
            if (loginSignInText.text.toString().isNotEmpty()) {
                loginSignInField.error = null
            }
            startButton.isActivated =
                loginSignInText.text.toString().isNotEmpty() && passwordSignInText.text.toString()
                    .isNotEmpty()
            Log.d("MyLog", "startButtonstate1 = ${startButton.isActivated}")
        }

        passwordSignInText.doOnTextChanged { inputText, _, _, _ ->
            if (passwordSignInText.text.toString().isNotEmpty()) {
                passwordSignInField.error = null
            }
            startButton.isActivated =
                loginSignInText.text.toString().isNotEmpty() && passwordSignInText.text.toString()
                    .isNotEmpty()
            Log.d("MyLog", "startButtonstate1 = ${startButton.isActivated}")
        }


//        emailText.setText(sharedPref?.getString(LAST_USER_LOGIN, "").toString())
//        passwordText.setText(sharedPref?.getString(LAST_USER_PASSWORD, "").toString())

        loginSignInText.setText(sharedPref?.getString(LAST_USER_LOGIN, "").toString())
        passwordSignInText.setText(sharedPref?.getString(LAST_USER_PASSWORD, "").toString())
        if (loginSignInText.text.toString().isNotEmpty()) rememberPasswordSignInButton.isChecked =
            true
//        if (loginSignInText.text.isNullOrEmpty()){
//
//        }


//        if (!emailField.editText?.text.toString().isEmpty()) rememberCheck.isChecked = true

//        Log.d("MyLog", "enter email = ${sharedPref?.getString(LAST_USER_LOGIN, "")}")
//        Log.d("MyLog", "enter password = ${sharedPref?.getString(LAST_USER_PASSWORD, "")}")
//
//        if (loginSignInText.text.toString().isNullOrEmpty() && passwordSignInText.text.toString().isNullOrEmpty()){
//            startButton.isActivated = false
//        }


        startButton.setOnClickListener {
            Log.d("MyLog", "startButtonstate2 = ${startButton.isActivated}")
            if (startButton.isActivated) {
                val login = loginSignInText.text.toString()
                val password = passwordSignInText.text.toString()
                val user = User(0, login, password)
                loginFragmentViewModel.checkLoginUser(user)
            }

//            if (!email.isEmpty() && !password.isEmpty()) {
//                loginFragmentViewModel.checkLoginUser(user)
//            } else {
//                if (email.isEmpty()) emailField.error = "Please enter email"
//                if (password.isEmpty()) passwordField.error = "Please enter password"
//            }
        }

        loginFragmentViewModel.loginResponse.observe(this.viewLifecycleOwner) {
            if (it == -1L) {
                errorMessage.visibility = View.VISIBLE
            } else {
                if (rememberPasswordSignInButton.isChecked) {
                    sharedPref?.edit()
                        ?.putString(LAST_USER_LOGIN, loginSignInText.text.toString())?.apply()

                    sharedPref?.edit()
                        ?.putString(LAST_USER_PASSWORD, passwordSignInText.text.toString())?.apply()

//                    Log.d("MyLog", "email exit = ${emailField.editText?.text.toString()}")
//                    Log.d("MyLog", "email exit = ${passwordField.editText?.text.toString()}")
                } else {
                    sharedPref?.edit()?.putString(LAST_USER_LOGIN, "")?.apply()
                    sharedPref?.edit()?.putString(LAST_USER_PASSWORD, "")?.apply()
                }
                findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)
            }
        }

//        startButton.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
//        }
    }

    private fun listenNoEmptyCondition(
        loginSignUpText: TextInputEditText,
        loginSignUpField: TextInputLayout
    ) {
        loginSignUpText.addTextChangedListener {
            if (loginSignUpText.text?.isNotEmpty() == true) loginSignUpField.error = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
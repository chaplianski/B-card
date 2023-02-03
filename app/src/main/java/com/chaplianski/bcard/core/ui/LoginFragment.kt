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
import com.chaplianski.bcard.core.utils.*
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

        val backgroundLayout = binding.motionLayoutLoginFragment
        val startButton = binding.btLoginFragmentSignin
        val registrationPanel = binding.clLoginFragmentLeftLeftPanel
        val motionLayout = binding.motionLayoutLoginFragment
//        val buttonMotionLayout = binding.clLoginFragmentLeftLeftPanel
        val layoutSignIn = binding.layoutLoginFragmentSignIn.layoutSignIn
        val loginSignInField = binding.layoutLoginFragmentSignIn.otfLayoutSignInLogin
        val loginSignInText = binding.layoutLoginFragmentSignIn.etLayoutSignInLogin
        val passwordSignInField = binding.layoutLoginFragmentSignIn.otfLayoutSignInPassword
        val passwordSignInText = binding.layoutLoginFragmentSignIn.etLayoutSignInPassword
        val errorMessage = binding.layoutLoginFragmentSignIn.tvLayoutSignInError
        val rememberPasswordSignInButton = binding.layoutLoginFragmentSignIn.cbLayoutSignInRemember
        val forgotPasswordSignInButton =
            binding.layoutLoginFragmentSignIn.btLayoutSignInForgotPassword
        val signUpSignInButton = binding.layoutLoginFragmentSignIn.btLayoutSignInSignUp

        val layoutForgotPassword = binding.layoutLoginFragmentForgotPassword.layoutForgotPassword
        val secretQuestionForgotPasswordText = binding.layoutLoginFragmentForgotPassword.tvLayoutForgotPasswordQuestion
        val secretAnswerForgotPasswordText = binding.layoutLoginFragmentForgotPassword.etLayoutForgotPasswordAnswer
        val secretAnswerForgotPasswordField = binding.layoutLoginFragmentForgotPassword.otfLayoutForgotPasswordAnswer
        val cancelForgotPassword =
            binding.layoutLoginFragmentForgotPassword.btLayoutForgotPasswordCancel
        val okForgotPasswordButton = binding.layoutLoginFragmentForgotPassword.btLayoutForgotPasswordOk

        val layoutNewPassword = binding.layoutLoginFragmentNewPassword.layoutNewPassword
        val newPasswordLayoutNewPasswordField = binding.layoutLoginFragmentNewPassword.otfLayoutNewPasswordPassword
        val newPasswordLayoutNewPasswordText = binding.layoutLoginFragmentNewPassword.etLayoutNewPasswordPassword
        val repeatNewPasswordLayoutNewPasswordField = binding.layoutLoginFragmentNewPassword.otfLayoutNewPasswordRepeatPassword
        val repeatNewPasswordLayoutNewPasswordText = binding.layoutLoginFragmentNewPassword.etLayoutNewPasswordRepeatPassword
        val okNewPasswordLayoutButton = binding.layoutLoginFragmentNewPassword.btLayoutNewPasswordOk
        val cancelNewPasswordLayoutButton = binding.layoutLoginFragmentNewPassword.btLayoutNewPasswordCancel

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
        val currentBackground = sharedPref?.getString(CURRENT_BACKGROUND, DEFAULT_BACKGROUND)
        val backgroundResource = this.resources.getIdentifier(
            currentBackground,
            "drawable",
            activity?.packageName
        )
        backgroundLayout.background = resources.getDrawable(backgroundResource)

        var registrationPanelCount = true

        //  *********** Sign In block view *******************************

        loginSignInText.doOnTextChanged { inputText, _, _, _ ->
            if (loginSignInText.text.toString().isNotEmpty()) {
                loginSignInField.error = null
            }
            startButton.isActivated =
                loginSignInText.text.toString()
                    .isNotEmpty() && passwordSignInText.text.toString()
                    .isNotEmpty()
        }

        passwordSignInText.doOnTextChanged { inputText, _, _, _ ->
            if (passwordSignInText.text.toString().isNotEmpty()) {
                passwordSignInField.error = null
            }
            startButton.isActivated =
                loginSignInText.text.toString()
                    .isNotEmpty() && passwordSignInText.text.toString()
                    .isNotEmpty()
        }

        loginSignInText.setText(sharedPref?.getString(LAST_USER_LOGIN, "").toString())
        passwordSignInText.setText(sharedPref?.getString(LAST_USER_PASSWORD, "").toString())
        if (loginSignInText.text.toString().isNotEmpty() && passwordSignInText.text.toString().isNotEmpty()){
            rememberPasswordSignInButton.isChecked = true
        }else {
//            rememberPasswordSignInButton.isChecked = false
            startButton.isActivated = false
        }


        forgotPasswordSignInButton.setOnClickListener {
            if (loginSignInText.text?.isEmpty() == true) {
                loginSignUpField.error = resources.getString(R.string.please_enter_login)
            } else {
                errorMessage.visibility = View.GONE
                loginFragmentViewModel.getSecretQuestion(loginSignInText.text.toString())
            }
        }

        loginFragmentViewModel.user.observe(this.viewLifecycleOwner){ user ->
            if (user == null) loginSignInField.error = resources.getString(R.string.such_login_not_exist)
            else {
                layoutSignIn.visibility = View.GONE
                layoutForgotPassword.visibility = View.VISIBLE
                secretQuestionForgotPasswordText.text = user.secretQuestion
                okForgotPasswordButton.setOnClickListener {
                    if (secretAnswerForgotPasswordText.text.toString() == user.secretAnswer){
                        layoutForgotPassword.visibility = View.GONE
                        layoutNewPassword.visibility = View.VISIBLE
                    }
                }

                listenNoEmptyCondition(newPasswordLayoutNewPasswordText, newPasswordLayoutNewPasswordField)
                listenNoEmptyCondition(repeatNewPasswordLayoutNewPasswordText, repeatNewPasswordLayoutNewPasswordField)

                okNewPasswordLayoutButton.setOnClickListener {
                    if (newPasswordLayoutNewPasswordText.text.toString().isEmpty()) {
                        newPasswordLayoutNewPasswordField.error = resources.getString(R.string.please_enter_password)
                    } else if (repeatNewPasswordLayoutNewPasswordText.text.toString().isEmpty()) {
                        repeatNewPasswordLayoutNewPasswordField.error = resources.getString(R.string.please_enter_password)
                    } else if (newPasswordLayoutNewPasswordText.text.toString() != repeatNewPasswordLayoutNewPasswordText.text.toString()){
                        repeatNewPasswordLayoutNewPasswordField.error = resources.getString(R.string.password_does_not_match)
                    } else {
                        val newUser = User(
                            id = user.id,
                            login = user.login,
                            password = newPasswordLayoutNewPasswordText.text.toString(),
                            secretQuestion = user.secretQuestion,
                            secretAnswer = user.secretAnswer
                        )
                        loginFragmentViewModel.updateUser(newUser)
                        findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)
                    }
                }
                okForgotPasswordButton.setOnClickListener {
                    if (secretAnswerForgotPasswordText.text.toString().isEmpty())
                        secretAnswerForgotPasswordField.error = resources.getString(R.string.please_enter_secret_answer)
                    else if (secretAnswerForgotPasswordText.text.toString() != user.secretAnswer){
                        secretAnswerForgotPasswordField.error = resources.getString(R.string.answer_is_wrong)
                    } else {
                        layoutForgotPassword.visibility = View.GONE
                        layoutNewPassword.visibility = View.VISIBLE
                    }
                }
            }
        }

        signUpSignInButton.setOnClickListener {
            layoutSignIn.visibility = View.GONE
            layoutSignUp.visibility = View.VISIBLE
        }


        //******** Sign UP block view  **********

        listenNoEmptyCondition(loginSignUpText, loginSignUpField)
        listenNoEmptyCondition(passwordSignUpText, passwordSignUpField)
        listenNoEmptyCondition(repeatPasswordSignUpText, repeatPasswordSignUpField)
        listenNoEmptyCondition(secretQuestionSignUpText, secretQuestionSignUpField)
        listenNoEmptyCondition(secretAnswerSignUpText, secretAnswerSignUpField)

        saveSignUpButton.setOnClickListener {
            Log.d("MyLog", "save person info 1, login = ${loginSignUpText.text}")
            when {
                loginSignUpText.text?.isEmpty() == true -> loginSignUpField.error =
                    resources.getString(R.string.please_enter_login)
                passwordSignUpText.text?.isEmpty() == true -> passwordSignUpField.error =
                    resources.getString(R.string.please_enter_password)
                repeatPasswordSignUpText.text?.isEmpty() == true -> repeatPasswordSignUpField.error =
                    resources.getString(R.string.please_enter_password)
                secretQuestionSignUpText.text?.isEmpty() == true -> secretQuestionSignUpField.error =
                    resources.getString(R.string.please_enter_secret_question)
                secretAnswerSignUpText.text?.isEmpty() == true -> secretAnswerSignUpField.error =
                    resources.getString(R.string.please_enter_secret_answer)
                passwordSignUpText.text.toString() != repeatPasswordSignUpText.text.toString() -> repeatPasswordSignUpField.error =
                    resources.getString(R.string.password_does_not_match)
                loginSignUpText.text?.toString()?.isNotEmpty() == true
                        && passwordSignUpText.text?.toString()?.isNotEmpty() == true
                        && repeatPasswordSignUpText.text?.toString()?.isNotEmpty() == true
                        && secretQuestionSignUpText.text?.toString()?.isNotEmpty() == true
                        && secretAnswerSignUpText.text?.toString()?.isNotEmpty() == true -> {
                    val newUser = User(
                        id = 0L,
                        login = loginSignUpText.text.toString(),
                        password = passwordSignUpText.text.toString(),
                        secretQuestion = secretQuestionSignUpText.text.toString(),
                        secretAnswer = secretAnswerSignUpText.text.toString()
                    )
                    loginFragmentViewModel.addUser(newUser)
                        }
                else -> {
                    Log.d("MyLog", "save person info 2, login = ${loginSignUpText.text}, password = ${passwordSignInText.text?.toString()}repassword = ${repeatPasswordSignUpText.text?.toString()}" +
                            " secQ = ${secretQuestionSignUpText.text?.toString()} ans = ${secretAnswerSignUpText.text?.toString()}")
                }
            }


//            if (loginSignUpText.text?.isEmpty() == true) {
//                Log.d("MyLog", "save person info 2")
//                loginSignUpField.error =
//                    resources.getString(R.string.please_enter_login)
//            }
//            else if (passwordSignUpText.text?.isEmpty() == true) {
//                Log.d("MyLog", "save person info 3")
//                passwordSignUpField.error =
//                    resources.getString(R.string.please_enter_password)
//            }
//            else if (repeatPasswordSignUpText.text?.isEmpty() == true) {
//                Log.d("MyLog", "save person info 4")
//                repeatPasswordSignUpField.error =
//                    resources.getString(R.string.please_enter_password)
//            }
//            else if (secretQuestionSignUpText.text?.isEmpty() == true) {
//                Log.d("MyLog", "save person info 5")
//                secretQuestionSignUpField.error =
//                    resources.getString(R.string.please_enter_secret_question)
//            }
//            else if (secretAnswerSignUpText.text?.isEmpty() == true) {
//                Log.d("MyLog", "save person info 6")
//                secretAnswerSignUpField.error =
//                    resources.getString(R.string.please_enter_secret_answer)
//            }
//            else if (passwordSignUpText.text.toString() != repeatPasswordSignUpText.text.toString()) {
//                Log.d("MyLog", "save person info 7")
//                repeatPasswordSignUpField.error =
//                    resources.getString(R.string.password_does_not_match)
//
//            }
//
//            else if (loginSignUpText.text?.toString()?.isNotEmpty() == true
//                && passwordSignInText.text?.toString()?.isNotEmpty() == true
//                && repeatPasswordSignUpText.text?.toString()?.isNotEmpty() == true
//                && secretQuestionSignUpText.text?.toString()?.isNotEmpty() == true
//                && secretAnswerSignUpText.text?.toString()?.isNotEmpty() == true) {
//                Log.d("MyLog", "login = ${loginSignUpText.text}, ${loginSignUpText.text?.toString()} ")
//                val newUser = User(
//                    id = 0L,
//                    login = loginSignInText.text.toString(),
//                    password = passwordSignUpText.text.toString(),
//                    secretQuestion = secretQuestionSignUpText.text.toString(),
//                    secretAnswer = secretAnswerSignUpText.text.toString()
//                )
//                loginFragmentViewModel.addUser(newUser)
//            }
        }

            loginFragmentViewModel.userId.observe(this.viewLifecycleOwner) {
                sharedPref?.edit()?.putLong(CURRENT_USER_ID, it)?.apply()
            findNavController().navigate(R.id.action_loginFragment_to_cardsFragment)
            }

            cancelSignUpButton.setOnClickListener {
                layoutSignUp.visibility = View.GONE
                layoutSignIn.visibility = View.VISIBLE
            }


        //  ************* Forgot password block view  ***************

        listenNoEmptyCondition(secretAnswerForgotPasswordText, secretAnswerForgotPasswordField)



        cancelForgotPassword.setOnClickListener {
            layoutForgotPassword.visibility = View.GONE
            layoutSignIn.visibility = View.VISIBLE
        }

        cancelNewPasswordLayoutButton.setOnClickListener {
            layoutNewPassword.visibility = View.GONE
            layoutForgotPassword.visibility = View.VISIBLE
        }


        //***********************************************************************
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





//        emailText.setText(sharedPref?.getString(LAST_USER_LOGIN, "").toString())
//        passwordText.setText(sharedPref?.getString(LAST_USER_PASSWORD, "").toString())


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
                    val user = User(0, login, password, "", "")
                    loginFragmentViewModel.checkLoginUser(user)
                }
            }

            loginFragmentViewModel.loginResponse.observe(this.viewLifecycleOwner) {
                Log.d("MyLog", "id user = $it")
                if (it == -1L) {
                    Log.d("MyLog", "id user = $it")
                    errorMessage.visibility = View.VISIBLE
                } else {
                    if (rememberPasswordSignInButton.isChecked) {
                        sharedPref?.edit()
                            ?.putString(LAST_USER_LOGIN, loginSignInText.text.toString())?.apply()

                        sharedPref?.edit()
                            ?.putString(LAST_USER_PASSWORD, passwordSignInText.text.toString())
                            ?.apply()

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
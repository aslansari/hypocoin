package com.aslansari.hypocoin.account.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.login.LoginViewModel
import com.aslansari.hypocoin.databinding.FragmentLoginCompleteBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import com.aslansari.hypocoin.viewmodel.login.LoginError
import com.aslansari.hypocoin.viewmodel.login.LoginResult
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import com.google.android.material.snackbar.Snackbar

class LoginCompleteFragment : BaseDialogFragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }
    private lateinit var binding: FragmentLoginCompleteBinding
    private val args: LoginCompleteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginCompleteBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = loginViewModel
            email = args.email
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.isDark = DarkModeUtil.isDarkMode(requireContext())
        loginViewModel.loginUIState.observe(viewLifecycleOwner) { state ->
            binding.progressLogin.visibility = if (state is LoginUIModel.Loading) View.VISIBLE else View.GONE
            when(state) {
                is LoginUIModel.Result -> {
                    if (state.loginResult == LoginResult.LOGIN_SUCCESS) {
                    }
                    when (state.loginResult) {
                        LoginResult.LOGIN_SUCCESS -> {
                            findNavController().navigate(R.id.action_login_completed)
                        }
                        LoginResult.PASSWORD_RESET_EMAIL_SENT -> {
                            Snackbar.make(binding.root, "Password reset email sent", Snackbar.LENGTH_INDEFINITE).show()
                        }
                        else -> {}
                    }
                }
                is LoginUIModel.Error -> {
                    if (state.loginError == LoginError.PASSWORD_RESET_EMAIL_NOT_SENT) {
                        Snackbar.make(binding.root, "Couldn't sent password reset email", Snackbar.LENGTH_LONG).show()
                    } else {
                        val errorString = when (state.loginError) {
                            LoginError.PASSWORD_INCORRECT -> getString(R.string.error_password_incorrect)
                            else -> getString(R.string.error_login_failed)
                        }
                        binding.textFieldPassword.error = errorString
                    }
                }
                else -> {}
            }
        }
    }
}
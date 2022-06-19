package com.aslansari.hypocoin.account.login.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.login.LoginViewModel
import com.aslansari.hypocoin.databinding.FragmentLoginBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class LoginFragment : BaseDialogFragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
        if (arguments != null) {
            // get arguments if exists
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.apply {
            vm = loginViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        setSignupButton()
        loginViewModel.loginUIState.observe(viewLifecycleOwner) { model ->
            when (model) {
                is LoginUIModel.Fail -> {
                    // todo print or handle error
                }
                is LoginUIModel.Complete -> {
                    // todo make complete action, navigate to account fragment
                }
                else -> {}
            }
        }
    }

    private fun setSignupButton() {
        val doNotHaveAccount = getString(R.string.signup_first)
        val signupText = getString(R.string.signup_second)
        val displayText = "$doNotHaveAccount $signupText"
        val spannable = SpannableStringBuilder(displayText)
        val startIdx = displayText.indexOf(signupText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_login_to_register)
                widget.invalidate()
            }
        }
        spannable.setSpan(
            clickableSpan,
            startIdx,
            startIdx + signupText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvSignup.text = spannable
        binding.tvSignup.movementMethod = LinkMovementMethod.getInstance()
    }
}
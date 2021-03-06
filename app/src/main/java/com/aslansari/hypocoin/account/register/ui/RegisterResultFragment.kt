package com.aslansari.hypocoin.account.register.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.register.data.RegisterResultStatus
import com.aslansari.hypocoin.databinding.FragmentRegisterResultBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import kotlinx.coroutines.launch

/**
 *
 */
class RegisterResultFragment : BaseDialogFragment() {

    private val registerViewModel: RegisterViewModel by activityViewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: FragmentRegisterResultBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.HypoCoinTheme)
        arguments?.let {
            email = it.getString("email", "--")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterResultBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = registerViewModel
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.isDark = DarkModeUtil.isDarkMode(requireContext())
        binding.textFieldTos.movementMethod = LinkMovementMethod.getInstance()
        binding.textFieldEmail.text = email

        registerViewModel.viewModelScope.launch {
            registerViewModel.validateInput().collect {
                when (it.error) {
                    RegisterResultStatus.NO_ERROR -> {
                        binding.textFieldPassword.error = null
                        binding.textFieldPasswordConfirm.error = null
                    }
                    RegisterResultStatus.DOES_NOT_MATCH -> {
                        binding.textFieldPassword.error = getString(R.string.error_passwords_does_not_match)
                    }
                    RegisterResultStatus.CONFIRM_YOUR_PASSWORD -> {
                        binding.textFieldPasswordConfirm.error = getString(R.string.confirm_password)
                    }
                    RegisterResultStatus.SHOULD_NOT_BE_EMPTY -> {
                        binding.textFieldPassword.error = getString(R.string.error_password_cannot_be_empty)
                    }
                    RegisterResultStatus.NOT_VALID -> {
                        binding.textFieldPassword.error = getString(R.string.error_invalid_password)
                    }
                    else -> {}
                }
            }
        }

        registerViewModel.registerResultUIStateLiveData.observe(viewLifecycleOwner) {
            when (it.error) {
                RegisterResultStatus.SUCCESS -> {
                    val direction = RegisterResultFragmentDirections.registerSuccess(
                        message = getString(R.string.register_success),
                    )
                    findNavController().navigate(direction)
                }
                else -> {
                    Toast.makeText(requireContext(), it.error.name, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
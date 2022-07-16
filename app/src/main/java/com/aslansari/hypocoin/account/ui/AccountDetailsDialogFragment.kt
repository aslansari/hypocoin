package com.aslansari.hypocoin.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.DialogAccountDetailsBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import com.aslansari.hypocoin.ui.DisplayTextUtil
import kotlinx.coroutines.flow.collectLatest

class AccountDetailsDialogFragment : BaseDialogFragment() {

    private val userProfileViewModel: UserProfileViewModel by activityViewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: DialogAccountDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DialogAccountDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            userProfileViewModel.walletUIState.collectLatest {
                binding.progressBar.isVisible = it is UserWalletUIModel.Loading
                if (it is UserWalletUIModel.Result) {
                    binding.textFieldProfileDisplayName.text = it.user.displayName.ifBlank {
                        "--"
                    }
                    binding.textFieldProfileEmail.text = it.user.email
                    binding.textFieldCreatedAt.text = DisplayTextUtil.Date.getFormattedDate(it.user.createdAt)
                    binding.textFieldLastLogin.text = DisplayTextUtil.Date.getFormattedTime(it.user.lastLogin)
                    binding.buttonSendVerificationEmail.isVisible = it.user.isEmailVerified.not()
                    if (it.user.multiFactorMethods.isEmpty()) {
                        binding.textFieldMultiFactorMethods.text =
                            getString(R.string.no_multi_factor_method)
                        binding.textFieldMultiFactorMethods.setTextColor(getErrorColor())
                    } else {
                        binding.textFieldMultiFactorMethods.text =
                            it.user.multiFactorMethods.joinToString(",")
                    }
                    if (it.user.phoneNumber.isEmpty()) {
                        binding.textFieldPhoneNumber.text = getString(R.string.no_phone_number)
                        binding.textFieldPhoneNumber.setTextColor(getErrorColor())
                    } else {
                        binding.textFieldPhoneNumber.text = it.user.phoneNumber
                    }
                    binding.textFieldEmail.text =
                        if (it.user.isEmailVerified) getString(R.string.email_verified) else getString(R.string.email_not_verified)
                    if (it.user.isEmailVerified.not()) {
                        binding.textFieldEmail.setTextColor(getErrorColor())
                    }
                    binding.buttonChangePassword.isVisible = it.user.hasPassword
                }
            }
        }
        binding.buttonLogout.setOnClickListener {
            userProfileViewModel.logout {
                findNavController().navigate(R.id.action_logout)
            }
        }
        binding.buttonEditAccount.setOnClickListener {
            findNavController().navigate(R.id.edit_account_details)
        }
        binding.buttonChangePassword.setOnClickListener {
            Toast.makeText(context, R.string.feature_not_supported, Toast.LENGTH_LONG).show()
        }
        binding.buttonSendVerificationEmail.setOnClickListener {
            userProfileViewModel.sendVerificationEmail {
                val toastText = if (it.isSuccessful) {
                    getString(R.string.verification_email_sent)
                } else {
                    getString(R.string.verification_email_fail)
                }
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG ).show()
            }
        }
    }

    private fun getErrorColor(): Int = if (DarkModeUtil.isDarkMode(requireContext())) {
        resources.getColor(R.color.md_theme_dark_error)
    } else {
        resources.getColor(R.color.md_theme_light_error)
    }
}
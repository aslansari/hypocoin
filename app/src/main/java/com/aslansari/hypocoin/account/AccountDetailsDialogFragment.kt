package com.aslansari.hypocoin.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.DialogAccountDetailsBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import com.aslansari.hypocoin.viewmodel.account.UserInfoUIModel
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel

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

    override fun onResume() {
        super.onResume()
        userProfileViewModel.getUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileViewModel.userInfoUIModelLiveData.observe(viewLifecycleOwner) {
            if (it is UserInfoUIModel.User) {
                binding.textFieldProfileDisplayName.text = it.data.displayName.ifBlank {
                    "--"
                }
                binding.textFieldProfileEmail.text = it.data.email
                binding.textFieldCreatedAt.text = it.data.createdAt
                binding.textFieldLastLogin.text = it.data.lastLogin
                binding.buttonSendVerificationEmail.isVisible = it.data.isEmailVerified.not()
                if (it.data.multiFactorMethods.isEmpty()) {
                    binding.textFieldMultiFactorMethods.text =
                        getString(R.string.no_multi_factor_method)
                    binding.textFieldMultiFactorMethods.setTextColor(getErrorColor())
                } else {
                    binding.textFieldMultiFactorMethods.text =
                        it.data.multiFactorMethods.joinToString(",")
                }
                if (it.data.phoneNumber.isEmpty()) {
                    binding.textFieldPhoneNumber.text = getString(R.string.no_phone_number)
                    binding.textFieldPhoneNumber.setTextColor(getErrorColor())
                } else {
                    binding.textFieldPhoneNumber.text = it.data.phoneNumber
                }
                binding.textFieldEmail.text =
                    if (it.data.isEmailVerified) getString(R.string.email_verified) else getString(R.string.email_not_verified)
                if (it.data.isEmailVerified.not()) {
                    binding.textFieldEmail.setTextColor(getErrorColor())
                }
                binding.buttonChangePassword.isVisible = it.data.hasPassword
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
            // todo open password set dialog
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
package com.aslansari.hypocoin.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentAccountBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.ui.DisplayTextUtil
import timber.log.Timber

class AccountFragment : BaseFragment() {
    private val userProfileViewModel: UserProfileViewModel by activityViewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileClickListener: (View) -> Unit = {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.account_fragment) {
                navController.navigate(R.id.open_account_details)
            }
        }
        binding.ivProfilePhoto.setOnClickListener(profileClickListener)
        binding.textFieldProfileEmail.setOnClickListener(profileClickListener)
        binding.textFieldProfileDisplayName.setOnClickListener(profileClickListener)

        if (userProfileViewModel.isLoggedIn()) {
            userProfileViewModel.getUserInfo()
        } else {
            findNavController().navigate(R.id.action_account_fragment_to_login_fragment)
        }
        userProfileViewModel.userInfoUIModelLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is UserInfoUIModel.Loading
            when(it) {
                is UserInfoUIModel.Error -> {
                    Timber.d("user info error")
                }
                is UserInfoUIModel.User -> {
                    binding.textFieldProfileEmail.text = it.data.email
                    binding.textFieldProfileDisplayName.text = it.data.displayName
                    binding.textFieldBalance.text = DisplayTextUtil.Amount.getDollarAmount(it.data.balance)
                    binding.textFieldNetWorth.text = DisplayTextUtil.Amount.getAmountForNetWorth(it.data.netWorth)
                    binding.layoutNoAssets.root.isVisible = true
                }
                else -> {}
            }
        }
        binding.layoutNoAssets.buttonInvestCTA.setOnClickListener {
            findNavController().navigate(R.id.action_invest_currency)
        }
    }
}
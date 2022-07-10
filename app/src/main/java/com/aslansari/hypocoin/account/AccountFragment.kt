package com.aslansari.hypocoin.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentAccountBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.ui.DisplayAmountUtil
import com.aslansari.hypocoin.viewmodel.account.UserInfoUIModel
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
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
        binding.buttonLogin.setOnClickListener {
            if (userProfileViewModel.isLoggedIn().not()) {
                findNavController().navigate(R.id.action_account_fragment_to_login_fragment)
            }
        }

        if (userProfileViewModel.isLoggedIn()) {
            userProfileViewModel.getUserInfo()
        } else {
            findNavController().navigate(R.id.action_account_fragment_to_login_fragment)
        }
        userProfileViewModel.userInfoUIModelLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it is UserInfoUIModel.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            when(it) {
                is UserInfoUIModel.Error -> {
                    Timber.d("user info error")
                }
                is UserInfoUIModel.User -> {
                    binding.textFieldProfileEmail.text = it.data.email
                    binding.textFieldProfileDisplayName.text = it.data.displayName
                    binding.textFieldBalance.text = DisplayAmountUtil.getDollarAmount(it.data.balance)
                }
                else -> {}
            }
        }
    }
}
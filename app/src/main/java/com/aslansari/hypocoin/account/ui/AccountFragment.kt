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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentAccountBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.ui.DisplayColorUtil
import com.aslansari.hypocoin.ui.DisplayTextUtil
import com.aslansari.hypocoin.ui.adapters.MarginItemDecorator
import kotlinx.coroutines.flow.collectLatest
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
        binding.apply {
            assetList.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_vertical)
            val horizontalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_horizontal)
            assetList.addItemDecoration(MarginItemDecorator(verticalMargin, horizontalMargin))
            binding.assetList.adapter = AssetsRecyclerAdapter()
        }
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

        if (userProfileViewModel.isLoggedIn().not()) {
            findNavController().navigate(R.id.action_account_fragment_to_login_fragment)
        }
        lifecycleScope.launchWhenStarted {
            userProfileViewModel.walletUIState.collectLatest {
                binding.progressBar.isVisible = it is UserWalletUIModel.Loading
                when(it) {
                    is UserWalletUIModel.Error -> {
                        Timber.d("user info error")
                    }
                    is UserWalletUIModel.Result -> {
                        binding.textFieldProfileEmail.text = it.user.email
                        binding.textFieldProfileDisplayName.text = it.user.displayName
                        bindNetWorth(it.netWorthUIModel)
                        binding.textFieldBalance.text = DisplayTextUtil.Amount.getDollarAmount(it.user.balance)
                        binding.layoutNoAssets.root.isVisible = it.assets.isEmpty()
                        binding.assetList.isVisible = true
                        if (it.assets.isNotEmpty()) {
                            val listAdapter = (binding.assetList.adapter as AssetsRecyclerAdapter)
                            listAdapter.submitList(it.assets) {
                                Timber.d("AssetList is committed ${listAdapter.itemCount}")
                            }
                            Timber.d("AssetList items ${listAdapter.itemCount}")
                        }
                    }
                    else -> {}
                }
            }
        }
        binding.layoutNoAssets.buttonInvestCTA.setOnClickListener {
            findNavController().navigate(R.id.action_invest_currency)
        }
        binding.buttonExchange.setOnClickListener {
            Toast.makeText(requireContext(), R.string.feature_not_supported, Toast.LENGTH_SHORT).show()
        }
        binding.buttonWithdrawal.setOnClickListener {
            Toast.makeText(requireContext(), R.string.feature_not_supported, Toast.LENGTH_SHORT).show()
        }
        binding.buttonDeposit.setOnClickListener {
            Toast.makeText(requireContext(), R.string.feature_not_supported, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindNetWorth(netWorthUIModel: NetWorthUIModel) {
        binding.textFieldNetWorth.text = DisplayTextUtil.Amount.getAmountForNetWorth(netWorthUIModel.netWorth)
        if (netWorthUIModel.roiData.rate.isNaN().not()) {
            binding.roiChip.isVisible = true
            when(netWorthUIModel.roiData.roiType) {
                RoiType.GAIN -> {
                    val chipBackgroundColor = DisplayColorUtil.getGainColor()
                    val chipTextColor = DisplayColorUtil.getGainTextColor(resources)
                    binding.roiChip.setChipBackgroundColorResource(chipBackgroundColor)
                    binding.roiChip.setTextColor(chipTextColor)
                    binding.roiChip.text = getString(R.string.positive_rate, DisplayTextUtil.Amount.getRateFormat(netWorthUIModel.roiData.rate))
                }
                RoiType.LOSS -> {
                    val chipBackgroundColor = DisplayColorUtil.getLossColor(requireContext())
                    val chipTextColor = DisplayColorUtil.getLossTextColor(requireContext(), resources)
                    binding.roiChip.setChipBackgroundColorResource(chipBackgroundColor)
                    binding.roiChip.setTextColor(chipTextColor)
                    binding.roiChip.text = getString(R.string.negative_rate, DisplayTextUtil.Amount.getRateFormat(netWorthUIModel.roiData.rate))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.assetList.adapter = null
    }

}
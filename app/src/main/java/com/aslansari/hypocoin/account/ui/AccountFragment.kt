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
import com.aslansari.hypocoin.currency.ui.MarginItemDecorator
import com.aslansari.hypocoin.databinding.FragmentAccountBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.ui.ChipUtil.updateForRoi
import com.aslansari.hypocoin.ui.DisplayTextUtil
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class AccountFragment : BaseFragment() {
    private val userProfileViewModel: UserProfileViewModel by activityViewModels()

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
            val horizontalMargin =
                resources.getDimensionPixelSize(R.dimen.currency_margin_horizontal)
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
                when (it) {
                    is UserWalletUIModel.Error -> {
                        Timber.d("user info error")
                    }
                    is UserWalletUIModel.Result -> {
                        binding.textFieldProfileEmail.text = it.user.email
                        binding.textFieldProfileDisplayName.text = it.user.displayName
                        bindNetWorth(it.netWorthUIModel)
                        binding.textFieldBalance.text =
                            DisplayTextUtil.Amount.getDollarAmount(it.user.balance)
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
                    is UserWalletUIModel.NotLogin -> {
                        binding.textFieldProfileEmail.text = ""
                        binding.textFieldProfileDisplayName.text = ""
                        bindNetWorth(NetWorthUIModel(0L, RoiChip(RoiType.LOSS, Double.NaN)))
                        binding.textFieldBalance.text = ""
                        binding.layoutNoAssets.root.isVisible = false
                        binding.assetList.isVisible = false
                    }
                    else -> {}
                }
            }
        }
        binding.layoutNoAssets.buttonInvestCTA.setOnClickListener {
            findNavController().navigate(R.id.action_invest_currency)
        }
        binding.buttonExchange.setOnClickListener {
            Toast.makeText(requireContext(), R.string.feature_not_supported, Toast.LENGTH_SHORT)
                .show()
        }
        binding.buttonWithdrawal.setOnClickListener {
            findNavController().navigate(R.id.action_withdraw_dialog)
        }
        binding.buttonDeposit.setOnClickListener {
            findNavController().navigate(R.id.action_deposit_balance)
        }
    }

    private fun bindNetWorth(netWorthUIModel: NetWorthUIModel) {
        binding.textFieldNetWorth.text =
            DisplayTextUtil.Amount.getAmountForNetWorth(netWorthUIModel.netWorth)
        if (netWorthUIModel.roiData.rate.isNaN().not()) {
            binding.roiChip.isVisible = true
            binding.roiChip.updateForRoi(netWorthUIModel.roiData)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.assetList.adapter = null
    }

}
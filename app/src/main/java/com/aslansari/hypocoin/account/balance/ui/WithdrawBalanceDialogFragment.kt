package com.aslansari.hypocoin.account.balance.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.data.UserResult
import com.aslansari.hypocoin.databinding.DialogBalanceWithdrawBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.ChipUtil.setChildrenEnabled
import com.aslansari.hypocoin.ui.DisplayTextUtil

class WithdrawBalanceDialogFragment : BaseDialogFragment() {

    private lateinit var binding: DialogBalanceWithdrawBinding
    private val balanceActionViewModel: BalanceActionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBalanceWithdrawBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            // todo set toolbar title and profile picture
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textWatcher = CurrencyTextWatcher(binding.textFieldDollarAmount.editText!!, "$")
        binding.textFieldDollarAmount.editText?.addTextChangedListener(textWatcher)
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = if (checkedIds.isNotEmpty()) {
                checkedIds.first()
            } else null
            val amount = when (checkedId) {
                binding.chip50dollar.id -> 50.00
                binding.chip100dollar.id -> 100.00
                binding.chip250dollar.id -> 250.00
                binding.chipAllIn.id -> {
                    when (val result = balanceActionViewModel.accountState.value) {
                        is UserResult.User -> result.balance.toDouble() / 100
                        else -> 0.0
                    }
                }
                else -> null
            }
            amount?.let {
                binding.textFieldDollarAmount.editText?.setText(
                    DisplayTextUtil.Amount.decimalFormat(
                        amount
                    )
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            balanceActionViewModel.accountState.collect {
                when (it) {
                    is UserResult.User -> {
                        binding.toolbar.title = it.displayName
                        binding.balance.textFieldBalance.text =
                            DisplayTextUtil.Amount.getDollarAmount(it.balance)
                    }
                    else -> {}
                }
            }
        }

        balanceActionViewModel.withdrawUIModelLiveData.observe(viewLifecycleOwner) {
            binding.progressCircular.isVisible = it is WithdrawUIModel.Loading
            binding.textFieldDollarAmount.isEnabled = it !is WithdrawUIModel.Loading
            binding.chipGroup.setChildrenEnabled(it !is WithdrawUIModel.Loading)
            binding.buttonComplete.isEnabled = it !is WithdrawUIModel.Loading
            if (it !is WithdrawUIModel.Error) {
                binding.textFieldDollarAmount.helperText = null
                binding.textFieldDollarAmount.error = null
                binding.chipGroup.clearCheck()
            }
            when (it) {
                is WithdrawUIModel.Error -> {
                    val errorText =
                        if (it.errorType == WithdrawUIModel.ErrorType.INSUFFICIENT_FUNDS) {
                            getString(R.string.error_insufficient_funds)
                        } else {
                            it.errorType.name
                        }
                    binding.textFieldDollarAmount.error = errorText
                }
                is WithdrawUIModel.Result -> {
                    findNavController().navigate(R.id.action_balance_complete)
                }
                else -> {}
            }
        }

        binding.buttonComplete.setOnClickListener {
            balanceActionViewModel.withdrawBalance(textWatcher.getCurrencyAmount())
        }
    }
}
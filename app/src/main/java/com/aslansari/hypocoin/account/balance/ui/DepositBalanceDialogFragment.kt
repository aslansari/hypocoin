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
import com.aslansari.hypocoin.databinding.DialogBalanceDepositBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.ChipUtil.setChildrenEnabled
import com.aslansari.hypocoin.ui.DisplayTextUtil

class DepositBalanceDialogFragment : BaseDialogFragment() {

    private lateinit var binding: DialogBalanceDepositBinding
    private val balanceActionViewModel: BalanceActionViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBalanceDepositBinding.inflate(inflater, container, false)
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
            val amount = when(checkedId) {
                binding.chip50dollar.id -> 50.00
                binding.chip100dollar.id -> 100.00
                binding.chip250dollar.id -> 250.00
                binding.chip500dollar.id -> 500.00
                else -> null
            }
            amount?.let { binding.textFieldDollarAmount.editText?.setText(DisplayTextUtil.Amount.decimalFormat(amount)) }
        }

        lifecycleScope.launchWhenStarted {
            balanceActionViewModel.accountState.collect {
                when(it) {
                    is UserResult.User -> {
                        binding.toolbar.title = it.displayName
                        binding.balance.textFieldBalance.text = DisplayTextUtil.Amount.getDollarAmount(it.balance)
                    }
                    else -> {}
                }
            }
        }

        balanceActionViewModel.depositUIModelLiveData.observe(viewLifecycleOwner) {
            binding.progressCircular.isVisible = it is DepositUIModel.Loading
            binding.textFieldDollarAmount.isEnabled = it !is DepositUIModel.Loading
            binding.chipGroup.setChildrenEnabled(it !is DepositUIModel.Loading)
            binding.buttonComplete.isEnabled = it !is DepositUIModel.Loading
            if (it !is DepositUIModel.Error) {
                binding.textFieldDollarAmount.error = null
            }
            when(it) {
                is DepositUIModel.Error -> {
                    val errorText = it.errorType.name
                    binding.textFieldDollarAmount.error = errorText
                }
                is DepositUIModel.Result -> {
                    if (it.isSuccess) {
                        val directions = DepositBalanceDialogFragmentDirections.depositComplete(
                            message = getString(R.string.deposit_success),
                            altMessage = DisplayTextUtil.Amount.getDollarAmount(it.depositAmount)
                        )
                        findNavController().navigate(directions)
                    }
                }
                else -> {}
            }
        }

        binding.buttonComplete.setOnClickListener {
            balanceActionViewModel.depositBalance(textWatcher.getCurrencyAmount())
        }
    }

}
package com.aslansari.hypocoin.currency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentCurrencyDetailsBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.ui.ChipUtil.updateForRoi
import com.aslansari.hypocoin.ui.DisplayTextUtil

class CurrencyDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentCurrencyDetailsBinding
    private val currencyDetailsViewModel: CurrencyDetailsViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }
    private val args: CurrencyDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            NavigationUI.setupWithNavController(toolbar, findNavController())
            toolbar.title = args.name
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.favourite -> {
                        if (it.isChecked) {
                            currencyDetailsViewModel.addToFavourite(args.id)
                        } else {
                            currencyDetailsViewModel.removeFromFavourite(args.id)
                        }
                        it.isChecked = it.isChecked.not()
                        it.updateFavouriteIcon()
                    }
                    else -> {}
                }
                true
            }
        }
        return binding.root
    }

    private fun MenuItem.updateFavouriteIcon() {
        icon = if (isChecked) {
            requireContext().getDrawable(R.drawable.ic_bookmark)
        } else {
            requireContext().getDrawable(R.drawable.ic_bookmark_border)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currencyDetailsViewModel.init(args.id)
        currencyDetailsViewModel.currencyDetailsState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is CurrencyDetailsState.Loading
            when (state) {
                is CurrencyDetailsState.Result -> {
                    binding.textFieldCurrentValue.text =
                        DisplayTextUtil.Amount.getDollarAmount(state.currencyValue)
                    binding.textFieldWallet.text =
                        "${DisplayTextUtil.Amount.getCurrencyFormat(state.walletInfo)} ${state.symbol}"
                    binding.textFieldPrice.text =
                        DisplayTextUtil.Amount.getDollarAmount(state.allTimeHigh.price)
                    binding.textFieldDate.text = state.allTimeHigh.date
                    binding.textFieldDays.text = state.allTimeHigh.days.toString()
                    binding.textFieldPercentDown.text = "${state.allTimeHigh.percentDown}%"
                    binding.roiChip.isVisible = true
                    binding.roiChip.updateForRoi(state.roiChip)
                }
                else -> {}
            }
        }
        binding.buttonBuy.setOnClickListener {
            currencyDetailsViewModel.buyCurrency(args.id)
        }
        binding.buttonSell.setOnClickListener {
            currencyDetailsViewModel.sellCurrency(args.id)
        }
    }
}


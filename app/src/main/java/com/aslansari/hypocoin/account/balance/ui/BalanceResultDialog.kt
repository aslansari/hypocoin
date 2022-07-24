package com.aslansari.hypocoin.account.balance.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.DialogBalanceResultBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BalanceResultDialog: BaseDialogFragment() {

    private lateinit var binding: DialogBalanceResultBinding
    private val args: BalanceResultDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.HypoCoinTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogBalanceResultBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            message = args.message
            altMessage = args.altMessage
            isMessageVisible = false
            val animRes = if (DarkModeUtil.isDarkMode(requireContext())) {
                R.raw.add_funds_dark
            } else {
                R.raw.add_funds
            }
            lottieAnimation.setAnimation(animRes)
            lottieAnimation.speed = 1.5f
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lottieAnimation.playAnimation()
        binding.lottieAnimation.addLottieOnCompositionLoadedListener {
            binding.isMessageVisible = true
        }

        lifecycleScope.launch {
            delay(3000)
            findNavController().navigate(R.id.action_balance_complete)
        }
    }
}
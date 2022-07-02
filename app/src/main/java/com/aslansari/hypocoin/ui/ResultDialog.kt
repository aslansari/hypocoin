package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.DialogResultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResultDialog: BaseDialogFragment() {

    private lateinit var binding: DialogResultBinding
    private val args: ResultDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.HypoCoinTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogResultBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            message = args.message
            resultMessage.visibility = View.GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lottieAnimation.playAnimation()
        binding.lottieAnimation.addLottieOnCompositionLoadedListener {
            binding.resultMessage.visibility = View.VISIBLE
        }
        lifecycleScope.launch {
            delay(5000)
            findNavController().navigate(R.id.action_register_result_finished)
        }
    }
}
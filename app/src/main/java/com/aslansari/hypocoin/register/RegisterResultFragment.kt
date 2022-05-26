package com.aslansari.hypocoin.register

import android.content.res.Configuration
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentRegisterResultBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment

/**
 * todo add dialog transition animation
 */
class RegisterResultFragment : BaseDialogFragment() {

    private val registerViewModel: RegisterViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: FragmentRegisterResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.HypoCoinTheme)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterResultBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = registerViewModel
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (requireContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.isDark = true
            }
            else -> {
                binding.isDark = false
            }
        }
        binding.textFieldTos.movementMethod = LinkMovementMethod.getInstance()
    }
}
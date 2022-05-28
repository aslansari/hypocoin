package com.aslansari.hypocoin.register

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentRegisterBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : BaseDialogFragment() {

    private val userProfileViewModel: UserProfileViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })
    private val registerViewModel: RegisterViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: FragmentRegisterBinding
    private var disposables: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
        if (arguments != null) {
        }
        disposables = CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = registerViewModel
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_register_fragment_to_register_fragment_result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables!!.dispose()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): RegisterFragment {
            val fragment = RegisterFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
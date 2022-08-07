package com.aslansari.hypocoin.account.register.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentRegisterBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import com.aslansari.hypocoin.ui.DarkModeUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : BaseDialogFragment() {

    private val registerViewModel: RegisterViewModel by activityViewModels()

    private lateinit var binding: FragmentRegisterBinding
    private var disposables: CompositeDisposable? = null

    private var getSignInResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
        if (arguments != null) {
        }
        disposables = CompositeDisposable()
        getSignInResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                registerViewModel.onGoogleSignInResult(it)
            }
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
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.isDark = DarkModeUtil.isDarkMode(requireContext())
        registerViewModel.registerUIState.observe(viewLifecycleOwner) { state ->
            when (state.error) {
                RegisterStatus.NO_ERROR -> binding.textField.error = null
                RegisterStatus.USER_ALREADY_EXISTS -> binding.textField.error =
                    "User already exists"
                RegisterStatus.INPUT_FORMAT_WRONG -> binding.textField.error =
                    "Please enter a valid email address"
                RegisterStatus.INPUT_EMPTY -> binding.textField.error = "Input required"
                RegisterStatus.SIGN_IN_WITH_GOOGLE -> {
                    getSignInResult?.let {
                        registerViewModel.registerWithGoogle(requireActivity(), it)
                    }
                }
                else -> {
                    binding.textField.error = "error"
                }
            }
            state.onSubmit?.let {
                registerViewModel.setEmail(state.onSubmit.email)
                val data = Bundle()
                data.putString("email", state.onSubmit.email)
                findNavController().navigate(
                    R.id.action_register_fragment_to_register_fragment_result,
                    data
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables!!.dispose()
    }

}
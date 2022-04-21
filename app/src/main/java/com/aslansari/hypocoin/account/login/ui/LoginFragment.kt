package com.aslansari.hypocoin.account.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.login.LoginViewModel
import com.aslansari.hypocoin.databinding.FragmentLoginBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment() {
    private val userProfileViewModel: UserProfileViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }
    private lateinit var binding: FragmentLoginBinding
    private var disposables: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // get arguments if exists
        }
        disposables = CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.apply {
            vm = loginViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        loginViewModel.onRegisterClicked {
            findNavController().navigate(R.id.action_login_to_register)
        }
        loginViewModel.loginUIState.observe(viewLifecycleOwner) { model ->
            when (model) {
                is LoginUIModel.Fail -> {
                    // todo print or handle error
                }
                is LoginUIModel.Complete -> {
                    // todo make complete action, navigate to account fragment
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables!!.dispose()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AccountFragment.
         */
        @JvmStatic
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            // add arguments if necessary
            fragment.arguments = args
            return fragment
        }
    }
}
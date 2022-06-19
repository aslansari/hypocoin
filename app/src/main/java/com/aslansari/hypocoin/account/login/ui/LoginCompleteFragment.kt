package com.aslansari.hypocoin.account.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.login.LoginViewModel
import com.aslansari.hypocoin.databinding.FragmentLoginCompleteBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class LoginCompleteFragment : BaseDialogFragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        viewModelCompositionRoot.viewModelFactory
    }
    private lateinit var binding: FragmentLoginCompleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
        if (arguments != null) {
            // get arguments if exists
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginCompleteBinding.inflate(inflater, container, false)
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
    }
}
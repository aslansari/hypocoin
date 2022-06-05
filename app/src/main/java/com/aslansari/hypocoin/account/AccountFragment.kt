package com.aslansari.hypocoin.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentAccountBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : BaseFragment() {
    private val userProfileViewModel: UserProfileViewModel by activityViewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: FragmentAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // get arguments if exists
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogin.setOnClickListener {
            if (userProfileViewModel.isLoggedIn().not()) {
                findNavController().navigate(R.id.action_account_fragment_to_login_fragment)
            }
        }
    }
}
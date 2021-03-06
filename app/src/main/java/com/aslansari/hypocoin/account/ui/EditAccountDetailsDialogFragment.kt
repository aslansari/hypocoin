package com.aslansari.hypocoin.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.DialogEditAccountDetailsBinding
import com.aslansari.hypocoin.ui.BaseDialogFragment
import kotlinx.coroutines.flow.collectLatest

class EditAccountDetailsDialogFragment : BaseDialogFragment() {

    private val userProfileViewModel: UserProfileViewModel by activityViewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })

    private lateinit var binding: DialogEditAccountDetailsBinding
    private var changeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DialogEditAccountDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var user: UserWalletUIModel.Result? = null
        lifecycleScope.launchWhenStarted {
            userProfileViewModel.walletUIState.collectLatest {
                if (it is UserWalletUIModel.Result) {
                    user = it
                    binding.textFieldProfileDisplayName.editText?.setText(it.user.displayName)
                    binding.textFieldProfileEmail.editText?.setText(it.user.email)
                }
            }
        }
        binding.buttonSaveChanges.setOnClickListener {_->
            binding.buttonSaveChanges.isEnabled = false
            changeCount = 0
            user?.let { it ->
                val textDisplayName = binding.textFieldProfileDisplayName.editText?.text.toString()
                val textEmail = binding.textFieldProfileEmail.editText?.text.toString()
                if (it.user.displayName != textDisplayName) {
                    changeCount++
                    userProfileViewModel.updateDisplayNumber(textDisplayName) { success ->
                        changeCount--
                        if (success.not()) {
                            Toast.makeText(requireContext(), getString(R.string.error_updating_display_name), Toast.LENGTH_LONG).show()
                            binding.textFieldProfileDisplayName.editText?.setText(it.user.displayName)
                            tryReleaseSaveButton()
                        } else {
                            onUpdateResult()
                        }
                    }
                }
                if (it.user.email != textEmail) {
                    changeCount++
                    userProfileViewModel.updateEmail(textEmail) { success ->
                        changeCount--
                        if (success.not()) {
                            Toast.makeText(requireContext(), getString(R.string.error_updating_email), Toast.LENGTH_LONG).show()
                            binding.textFieldProfileEmail.editText?.setText(it.user.email)
                            tryReleaseSaveButton()
                        } else {
                            onUpdateResult()
                        }
                    }
                }
            }
        }
        binding.buttonDiscard.setOnClickListener {
            findNavController().navigate(R.id.action_discard)
        }
    }
    private fun tryReleaseSaveButton() {
        if (changeCount == 0) {
            binding.buttonSaveChanges.isEnabled = true
        }
    }

    private fun onUpdateResult() {
        if (changeCount == 0) {
            findNavController().navigate(R.id.action_discard)
        }
    }
}
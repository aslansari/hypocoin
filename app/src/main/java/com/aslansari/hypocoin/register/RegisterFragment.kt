package com.aslansari.hypocoin.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentRegisterBinding
import com.aslansari.hypocoin.register.dto.RegisterInput
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.register.exception.UserAlreadyExistsException
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.viewmodel.DataStatus
import com.aslansari.hypocoin.viewmodel.Resource
import com.aslansari.hypocoin.viewmodel.Resource.Companion.error
import com.aslansari.hypocoin.viewmodel.Resource.Companion.loading
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : BaseFragment() {

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
        if (arguments != null) {
        }
        disposables = CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        var validateJob: Job? = null
        registerViewModel.passwordSecond.observe(viewLifecycleOwner) {
            binding.etPasswordAgain.error = null
            validateJob?.cancel()
            validateJob = lifecycleScope.launch {
                delay(2000)
                with(registerViewModel) {
                    validateInput(accountId.value ?: "", passwordFirst.value?:"", passwordSecond.value?:"")
                }
            }
        }

        registerViewModel.registerUIState.distinctUntilChanged().observe(viewLifecycleOwner) { state ->
            state.userMessages.forEach {
                when(it.id) {
                    MessageId.PWD_ONE_ERROR -> { binding.etPassword.error = getString(it.messageId) }
                    MessageId.PWD_TWO_ERROR -> { binding.etPasswordAgain.error = getString(it.messageId) }
                    MessageId.USERNAME_ERROR -> { binding.etAccountId.error = getString(it.messageId) }
                    MessageId.REGISTER_ERROR -> { binding.etAccountId.error = getString(it.messageId) }
                }
            }
        }

        disposables!!.add(binding.etPassword.focusChanges().skipInitialValue()
            .mergeWith(binding.etPasswordAgain.focusChanges().skipInitialValue())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { focused: Boolean ->
                if (focused) {
                    if (binding.etPassword.error != null) {
                        binding.etPassword.text = null
                        binding.etPassword.error = null
                        binding.etPasswordAgain.text = null
                        binding.etPasswordAgain.error = null
                    }
                }
            }
            .subscribe()
        )
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
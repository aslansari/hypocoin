package com.aslansari.hypocoin.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentRegisterBinding
import com.aslansari.hypocoin.register.Register
import com.aslansari.hypocoin.register.RegisterViewModel
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
import timber.log.Timber

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables!!.add(binding.buttonRegister.clicks()
            .map {
                RegisterInput(
                    binding.etAccountId.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etPasswordAgain.text.toString())
            }
            .observeOn(Schedulers.io())
            .flatMap { registerInput: RegisterInput? ->
                registerViewModel.validate(registerInput!!)
                    .startWithItem(loading(null))
            }
            .flatMap { registerInputResource: Resource<RegisterInput> ->
                if (DataStatus.COMPLETE === registerInputResource.status) {
                    return@flatMap registerViewModel.register(registerInputResource.value!!)
                } else if (registerInputResource.isLoading) {
                    return@flatMap Observable.just(loading(
                        null as Register?))
                } else {
                    return@flatMap Observable.just(error(
                        null as Register?, registerInputResource.throwable))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Resource<out Register?>?>() {
                override fun onNext(resource: Resource<out Register?>?) {
                    binding.buttonRegister.isEnabled = DataStatus.LOADING !== resource!!.status
                    binding.progressRegister.visibility =
                        if (resource!!.isLoading) View.VISIBLE else View.GONE
                    when (resource.status) {
                        DataStatus.COMPLETE -> {
                            Toast.makeText(context,
                                "Account registered successfully",
                                Toast.LENGTH_LONG).show()
                            userProfileViewModel.register()
                        }
                        DataStatus.ERROR -> if (resource.throwable!!.cause is UserAlreadyExistsException) {
                            binding.etAccountId.error = getString(R.string.user_exists)
                        } else if (resource.throwable.cause is PasswordMismatchException) {
                            binding.etPassword.error = getString(R.string.password_mismatch)
                            binding.etPasswordAgain.error = getString(R.string.password_mismatch)
                        } else {
                            resource.throwable.message?.let {
                                Snackbar.make(binding.coordinator, it, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        else -> {}
                    }
                }

                override fun onError(throwable: Throwable) {
                    Timber.e(throwable)
                    binding.progressRegister.visibility = View.GONE
                    Toast.makeText(context, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {}
            })
        )
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
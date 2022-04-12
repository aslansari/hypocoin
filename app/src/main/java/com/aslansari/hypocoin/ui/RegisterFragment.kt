package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.register.Register
import com.aslansari.hypocoin.register.RegisterViewModel
import com.aslansari.hypocoin.register.dto.RegisterInput
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.register.exception.UserAlreadyExistsException
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
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var userProfileViewModel: UserProfileViewModel? = null
    private var registerViewModel: RegisterViewModel? = null
    private var disposables: CompositeDisposable? = null
    private var etAccountId: EditText? = null
    private var etPassword: EditText? = null
    private var etPasswordAgain: EditText? = null
    private var buttonRegister: Button? = null
    private var progressRegister: ProgressBar? = null
    private var placeSnackbar: CoordinatorLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
        disposables = CompositeDisposable()
        userProfileViewModel =
            (requireContext().applicationContext as HypoCoinApp).appContainer!!.userProfileViewModel
        registerViewModel =
            (requireContext().applicationContext as HypoCoinApp).appContainer!!.registerViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        buttonRegister = view.findViewById(R.id.buttonRegister)
        progressRegister = view.findViewById(R.id.progressLogin)
        etAccountId = view.findViewById(R.id.etAccountId)
        etPassword = view.findViewById(R.id.etPassword)
        etPasswordAgain = view.findViewById(R.id.etPasswordAgain)
        placeSnackbar = view.findViewById(R.id.coordinator)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables!!.add(buttonRegister!!.clicks()
            .map { unit: Unit ->
                RegisterInput(
                    etAccountId!!.text.toString(),
                    etPassword!!.text.toString(),
                    etPasswordAgain!!.text.toString())
            }
            .observeOn(Schedulers.io())
            .flatMap { registerInput: RegisterInput? ->
                registerViewModel!!.validate(registerInput!!)
                    .startWithItem(loading(null))
            }
            .flatMap { registerInputResource: Resource<RegisterInput> ->
                if (DataStatus.COMPLETE === registerInputResource.status) {
                    return@flatMap registerViewModel!!.register(registerInputResource.value!!)
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
                    buttonRegister!!.isEnabled = DataStatus.LOADING !== resource!!.status
                    progressRegister!!.visibility =
                        if (resource!!.isLoading) View.VISIBLE else View.GONE
                    when (resource.status) {
                        DataStatus.COMPLETE -> {
                            Toast.makeText(context,
                                "Account registered successfully",
                                Toast.LENGTH_LONG).show()
                            userProfileViewModel!!.register()
                        }
                        DataStatus.ERROR -> if (resource.throwable!!.cause is UserAlreadyExistsException) {
                            etAccountId!!.error = getString(R.string.user_exists)
                        } else if (resource.throwable.cause is PasswordMismatchException) {
                            etPassword!!.error = getString(R.string.password_mismatch)
                            etPasswordAgain!!.error = getString(R.string.password_mismatch)
                        } else {
                            resource.throwable.message?.let {
                                Snackbar.make(placeSnackbar!!,
                                    it, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onError(throwable: Throwable) {
                    Timber.e(throwable)
                    progressRegister!!.visibility = View.GONE
                    Toast.makeText(context, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {}
            })
        )
        disposables!!.add(etPassword!!.focusChanges().skipInitialValue()
            .mergeWith(etPasswordAgain!!.focusChanges().skipInitialValue())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { focused: Boolean ->
                if (focused) {
                    if (etPassword!!.error != null) {
                        etPassword!!.text = null
                        etPassword!!.error = null
                        etPasswordAgain!!.text = null
                        etPasswordAgain!!.error = null
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
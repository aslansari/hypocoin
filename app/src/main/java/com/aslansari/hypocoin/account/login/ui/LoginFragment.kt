package com.aslansari.hypocoin.account.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aslansari.hypocoin.BR
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.login.LoginViewModel
import com.aslansari.hypocoin.databinding.FragmentLoginBinding
import com.aslansari.hypocoin.ui.BaseFragment
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel.Companion.complete
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment() {
    private val userProfileViewModel: UserProfileViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables!!.add(binding.buttonLogin.clicks() // TODO: 6/19/2021 process login
            .map { complete() }
            .subscribeWith(object : DisposableObserver<LoginUIModel?>() {
                override fun onNext(uiModel: LoginUIModel?) {
                    binding.buttonLogin.isEnabled = !uiModel!!.isLoading
                    binding.progressLogin.visibility = if (uiModel.isLoading) View.VISIBLE else View.GONE
                    if (uiModel.isFailed) {
                        // TODO set a proper error message
                        binding.etAccountId.error = "NOT FOUND"
                    }
                    if (uiModel.isComplete) {
                        userProfileViewModel.login()
                    }
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }

                override fun onComplete() {
                    // TODO trigger listener and change fragment
                    Toast.makeText(context, "test", Toast.LENGTH_LONG).show()
                }
            })
        )
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
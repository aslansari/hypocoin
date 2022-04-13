package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aslansari.hypocoin.R
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
    private var disposables: CompositeDisposable? = null
    private var tvId: TextView? = null
    private var tvRegisterRequest: TextView? = null
    private var etAccountId: EditText? = null
    private var buttonLogin: Button? = null
    private var progressLogin: ProgressBar? = null
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
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        tvId = view.findViewById(R.id.tvId)
        tvRegisterRequest = view.findViewById(R.id.tvRegisterRequest)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        progressLogin = view.findViewById(R.id.progressLogin)
        etAccountId = view.findViewById(R.id.etAccountId)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables!!.add(buttonLogin!!.clicks() // TODO: 6/19/2021 process login
            .map { complete() }
            .subscribeWith(object : DisposableObserver<LoginUIModel?>() {
                override fun onNext(uiModel: LoginUIModel?) {
                    buttonLogin!!.isEnabled = !uiModel!!.isLoading
                    progressLogin!!.visibility = if (uiModel.isLoading) View.VISIBLE else View.GONE
                    if (uiModel.isFailed) {
                        // TODO set a proper error message
                        etAccountId!!.error = "NOT FOUND"
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
        tvRegisterRequest!!.setOnClickListener { userProfileViewModel.registerRequest() }
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
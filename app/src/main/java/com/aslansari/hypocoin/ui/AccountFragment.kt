package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.viewmodel.account.AccountUIModel
import com.aslansari.hypocoin.viewmodel.account.AccountUIModel.Companion.idle
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    private var userProfileViewModel: UserProfileViewModel? = null
    private var disposables: CompositeDisposable? = null
    private var tvId: TextView? = null
    private var tvBalance: TextView? = null
    private val buttonLogin: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // get arguments if exists
        }
        disposables = CompositeDisposable()
        userProfileViewModel =
            (requireActivity().application as HypoCoinApp).appContainer!!.userProfileViewModel
        // TODO set id
        // TODO record to shard preference when an id is added
//        if (account != null) {
//            String accountId = UUID.randomUUID().toString();
//            accountViewModel.createAccount(accountId);
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        tvId = view.findViewById(R.id.tvId)
        tvBalance = view.findViewById(R.id.tvBalance)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userProfileViewModel!!.id != null && !userProfileViewModel!!.id!!.isEmpty()) {
            disposables!!.add(userProfileViewModel!!.account
                .subscribeWith(object : DisposableSingleObserver<Account?>() {
                    override fun onSuccess(account: Account?) {
                        tvId!!.text = account!!.id
                        val balance = account.balance.toDouble() / 100
                        tvBalance!!.text = UserProfileViewModel.AMOUNT_FORMAT.format(balance)
                    }

                    override fun onError(t: Throwable) {
                        Timber.e(t)
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }))
        }
        Observable.just(userProfileViewModel)
            .compose(transformer)
            .observeOn(AndroidSchedulers.mainThread())
            .scan(idle()) { state: AccountUIModel?, result: AccountUIModel? -> idle() }
            .subscribe(object : DisposableObserver<AccountUIModel?>() {
                override fun onNext(accountUIModel: AccountUIModel?) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private val transformer: ObservableTransformer<UserProfileViewModel?, AccountUIModel>
        private get() = ObservableTransformer { upstream: Observable<UserProfileViewModel?> ->
            upstream.flatMap { u: UserProfileViewModel? ->
                Observable.just(AccountUIModel())
                    .startWithItem(idle())
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
        fun newInstance(): AccountFragment {
            val fragment = AccountFragment()
            val args = Bundle()
            // add arguments if necessary
            fragment.arguments = args
            return fragment
        }
    }
}
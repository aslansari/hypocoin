package com.aslansari.hypocoin.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.repository.CoinService
import com.aslansari.hypocoin.repository.CoinService.LocalBinder
import com.aslansari.hypocoin.repository.model.Currency
import com.aslansari.hypocoin.ui.adapters.CurrencyRecyclerAdapter
import com.aslansari.hypocoin.ui.adapters.MarginItemDecorator
import com.aslansari.hypocoin.viewmodel.CoinViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyFragment : BaseFragment() {
    private val coinViewModel: CoinViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })
    private var progressBar: ProgressBar? = null
    private var currencyRecyclerAdapter: CurrencyRecyclerAdapter? = null
    private var disposables: CompositeDisposable? = null
    private var coinService: CoinService? = null
    private var isBoundCoinService = false
    private var serviceConnection: ServiceConnection? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // get params if exists
        }
        disposables = CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_currency, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerCoin)
        progressBar = view.findViewById(R.id.progressBar)
        currencyRecyclerAdapter = CurrencyRecyclerAdapter()
        val layoutManager = LinearLayoutManager(activity,
            RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val verticalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_vertical)
        val horizontalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_horizontal)
        recyclerView.addItemDecoration(MarginItemDecorator(verticalMargin, horizontalMargin))
        recyclerView.adapter = currencyRecyclerAdapter
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                coinService = (service as LocalBinder).service
                disposables!!.add(coinService!!.asyncCurrencies
                    .subscribeWith(object : DisposableSubscriber<List<Currency?>?>() {
                        override fun onNext(currencyList: List<Currency?>?) {
                            currencyRecyclerAdapter!!.updateList(currencyList as List<Currency>)
                        }

                        override fun onError(t: Throwable) {}
                        override fun onComplete() {}
                    })
                )
                coinService!!.start()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                coinService = null
            }
        }
        val context: Context? = activity
        if (context != null) {
            isBoundCoinService = context.bindService(Intent(activity, CoinService::class.java),
                serviceConnection as ServiceConnection,
                Context.BIND_AUTO_CREATE)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables!!.add(coinViewModel.currencyList
            .subscribeWith(object : DisposableSubscriber<Currency?>() {
                override fun onStart() {
                    progressBar!!.visibility = View.VISIBLE
                    super.onStart()
                }

                override fun onNext(currency: Currency?) {
                    progressBar!!.visibility = View.GONE
                    currencyRecyclerAdapter!!.add(currency)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }

                override fun onComplete() {
                    Timber.d("onComplete")
                }
            }))
    }

    override fun onStart() {
        super.onStart()
        if (coinService != null) {
            coinService!!.start()
        }
    }

    override fun onStop() {
        super.onStop()
        coinService!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables!!.dispose()
        currencyRecyclerAdapter = null
        val context: Context? = activity
        if (context != null && isBoundCoinService) {
            context.unbindService(serviceConnection!!)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): CurrencyFragment {
            val fragment = CurrencyFragment()
            val args = Bundle()
            // add params if necessary
            fragment.arguments = args
            return fragment
        }
    }
}
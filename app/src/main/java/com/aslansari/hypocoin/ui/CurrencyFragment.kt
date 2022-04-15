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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.databinding.FragmentCurrencyBinding
import com.aslansari.hypocoin.repository.CoinService
import com.aslansari.hypocoin.repository.CoinService.LocalBinder
import com.aslansari.hypocoin.repository.model.Currency
import com.aslansari.hypocoin.ui.adapters.CurrencyRecyclerAdapter
import com.aslansari.hypocoin.ui.adapters.MarginItemDecorator
import com.aslansari.hypocoin.viewmodel.CoinViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subscribers.DisposableSubscriber

/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyFragment : BaseFragment() {
    private val coinViewModel: CoinViewModel by viewModels(factoryProducer = {
        viewModelCompositionRoot.viewModelFactory
    })
    private var currencyRecyclerAdapter: CurrencyRecyclerAdapter? = null
    private var disposables: CompositeDisposable? = null
    private var coinService: CoinService? = null
    private var isBoundCoinService = false
    private var serviceConnection: ServiceConnection? = null
    private var binding: FragmentCurrencyBinding? = null
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
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        currencyRecyclerAdapter = CurrencyRecyclerAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            recyclerCoin.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_vertical)
            val horizontalMargin = resources.getDimensionPixelSize(R.dimen.currency_margin_horizontal)
            recyclerCoin.addItemDecoration(MarginItemDecorator(verticalMargin, horizontalMargin))
            recyclerCoin.adapter = currencyRecyclerAdapter
            vm = coinViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        coinViewModel.currencyList.observe(viewLifecycleOwner) { currencyList ->
            currencyRecyclerAdapter?.updateList(currencyList)
        }
        coinViewModel.getCurrencyList()
        // TODO: update ui with flowable instead of service 
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                coinService = (service as LocalBinder).service
                disposables!!.add(coinService!!.asyncCurrencies
                    .subscribeWith(object : DisposableSubscriber<List<Currency?>?>() {
                        override fun onNext(currencyList: List<Currency?>?) {
                            // currencyRecyclerAdapter?.updateList(currencyList as List<Currency>)
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
        isBoundCoinService = requireActivity().bindService(Intent(activity, CoinService::class.java),
            serviceConnection as ServiceConnection,
            Context.BIND_AUTO_CREATE)
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
        if (activity != null && isBoundCoinService) {
            requireActivity().unbindService(serviceConnection!!)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): CurrencyFragment {
            val fragment = CurrencyFragment()
            val args = Bundle()
            // add params if necessary
            // TODO: use navigation safe args
            fragment.arguments = args
            return fragment
        }
    }
}
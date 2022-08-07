package com.aslansari.hypocoin.repository

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.aslansari.hypocoin.currency.data.model.Currency
import com.aslansari.hypocoin.currency.ui.CoinViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import java.util.*

class CoinService : BaseService() {
    private var localBinder: LocalBinder? = null
    private val coinViewModel: CoinViewModel by lazy {
        viewModelCompositionRoot.viewModelFactory.create(CoinViewModel::class.java)
    }
    private var behaviorSubject: BehaviorSubject<List<Currency>>? = null
    private var timer: Timer? = null
    private var disposables: CompositeDisposable? = null

    inner class LocalBinder : Binder() {
        val service: CoinService
            get() = this@CoinService
    }

    override fun onCreate() {
        super.onCreate()
        localBinder = LocalBinder()
        behaviorSubject = BehaviorSubject.createDefault(ArrayList())
        disposables = CompositeDisposable()
    }

    override fun onBind(intent: Intent): IBinder? {
        return localBinder
    }

    fun start() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                disposables!!.add(coinViewModel.asyncCurrencyList
                    .doOnNext { t: List<Currency> -> behaviorSubject!!.onNext(t) }
                    .doOnError { t: Throwable? -> Timber.e(t) }
                    .subscribe())
            }
        }, 1000, CURRENCY_FETCH_INTERVAL_MS.toLong())
    }

    fun stop() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
        }
    }

    val asyncCurrencies: Flowable<List<Currency>>
        get() = behaviorSubject!!.toFlowable(BackpressureStrategy.BUFFER)

    companion object {
        const val CURRENCY_FETCH_INTERVAL_MS = 10000
    }
}
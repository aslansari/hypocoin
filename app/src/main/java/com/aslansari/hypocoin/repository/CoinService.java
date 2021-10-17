package com.aslansari.hypocoin.repository;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;
import timber.log.Timber;

public class CoinService extends Service {

    public static final int CURRENCY_FETCH_INTERVAL_MS = 10_000;
    private LocalBinder localBinder;
    private CoinViewModel coinViewModel;
    private BehaviorSubject<List<Currency>> behaviorSubject;
    private Timer timer;
    private CompositeDisposable disposables;

    public class LocalBinder extends Binder {
        public CoinService getService() {
            return CoinService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBinder = new LocalBinder();
        coinViewModel = ((HypoCoinApp) getApplication()).appContainer.coinViewModel;
        behaviorSubject = BehaviorSubject.createDefault(new ArrayList<>());
        disposables = new CompositeDisposable();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                disposables.add(coinViewModel.getAsyncCurrencyList()
                        .doOnNext(behaviorSubject::onNext)
                        .doOnError(Timber::e)
                        .subscribe());
            }
        }, 1_000, CURRENCY_FETCH_INTERVAL_MS);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public Flowable<List<Currency>> getAsyncCurrencies() {
        return behaviorSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}
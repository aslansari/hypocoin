package com.aslansari.hypocoin.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.ui.adapters.CurrencyAdapter;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;

import io.reactivex.MaybeObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CoinViewModel coinViewModel;
    private CurrencyAdapter currencyAdapter;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disposables = new CompositeDisposable();
        recyclerView = findViewById(R.id.recyclerCoin);
        coinViewModel = new CoinViewModel(1);
        currencyAdapter = new CurrencyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(currencyAdapter);

        disposables.add(coinViewModel.getCurrencyList()
                .subscribeWith(new DisposableSubscriber<Currency>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(Currency currency) {
                        currencyAdapter.add(currency);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
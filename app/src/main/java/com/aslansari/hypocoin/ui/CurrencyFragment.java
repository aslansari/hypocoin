package com.aslansari.hypocoin.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.repository.CoinService;
import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.ui.adapters.CurrencyRecyclerAdapter;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrencyFragment extends Fragment {

    private ProgressBar progressBar;
    private CoinViewModel coinViewModel;
    private CurrencyRecyclerAdapter currencyRecyclerAdapter;
    private CompositeDisposable disposables;
    private CoinService coinService;
    private boolean isBoundCoinService;
    private ServiceConnection serviceConnection;

    public CurrencyFragment() {
        // Required empty public constructor
    }

    public static CurrencyFragment newInstance() {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        // add params if necessary
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get params if exists
        }
        coinViewModel = ((HypoCoinApp) getActivity().getApplication()).appContainer.coinViewModel;
        disposables = new CompositeDisposable();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currency, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerCoin);
        progressBar = view.findViewById(R.id.progressBar);
        currencyRecyclerAdapter = new CurrencyRecyclerAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(currencyRecyclerAdapter);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                coinService = ((CoinService.LocalBinder) service).getService();

                disposables.add(coinService.getAsyncCurrencies()
                        .subscribeWith(new DisposableSubscriber<List<Currency>>() {
                            @Override
                            public void onNext(List<Currency> currencyList) {
                                currencyRecyclerAdapter.updateList(currencyList);
                            }

                            @Override
                            public void onError(Throwable t) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        })
                );

                coinService.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                coinService = null;
            }
        };

        Context context = getActivity();
        if (context != null) {
            isBoundCoinService = context.bindService(new Intent(getActivity(), CoinService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposables.add(coinViewModel.getCurrencyList()
                .subscribeWith(new DisposableSubscriber<Currency>() {
                    @Override
                    protected void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        super.onStart();
                    }

                    @Override
                    public void onNext(Currency currency) {
                        progressBar.setVisibility(View.GONE);
                        currencyRecyclerAdapter.add(currency);
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
    public void onStart() {
        super.onStart();
        if (coinService != null) {
            coinService.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        coinService.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        currencyRecyclerAdapter = null;
        Context context = getActivity();
        if (context != null && isBoundCoinService) {
            context.unbindService(serviceConnection);
        }
    }
}
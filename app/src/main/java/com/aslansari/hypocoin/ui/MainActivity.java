package com.aslansari.hypocoin.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disposables = new CompositeDisposable();
        bottomNavigationView = findViewById(R.id.bottomNavBarMain);

        UserProfileViewModel userProfileViewModel = ((HypoCoinApp) getApplication()).appContainer.userProfileViewModel;
        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    // TODO implement
                    break;
                case R.id.nav_currency:
                    fragmentTransaction.replace(R.id.frameLayout, CurrencyFragment.newInstance()).commit();
                    break;
                case R.id.nav_account:
                    if (userProfileViewModel.isLoggedIn()) {
                        fragmentTransaction.replace(R.id.frameLayout, AccountFragment.newInstance()).commit();
                    } else {
                        fragmentTransaction.replace(R.id.frameLayout, LoginFragment.newInstance()).commit();
                    }
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        disposables.add(userProfileViewModel.getPublishSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NotNull String id) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, AccountFragment.newInstance()).commit();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        // TODO: 6/19/2021 snackbar something went wrong
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
package com.aslansari.hypocoin.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aslansari.hypocoin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavBarMain);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()){
                case R.id.nav_home:
                    // TODO implement
                    break;
                case R.id.nav_currency:
                    fragmentTransaction.replace(R.id.frameLayout, CurrencyFragment.newInstance()).commit();
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
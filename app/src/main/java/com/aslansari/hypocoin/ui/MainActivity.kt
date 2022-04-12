package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.viewmodel.account.UserProfileAction
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private var disposables: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposables = CompositeDisposable()
        bottomNavigationView = findViewById(R.id.bottomNavBarMain)
        val userProfileViewModel = (application as HypoCoinApp).appContainer!!.userProfileViewModel
        val fragmentManager = supportFragmentManager
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item: MenuItem ->
            val fragmentTransaction = fragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.nav_home -> {}
                R.id.nav_currency -> fragmentTransaction.replace(R.id.frameLayout,
                    CurrencyFragment.newInstance()).commit()
                R.id.nav_account -> if (userProfileViewModel.isLoggedIn) {
                    fragmentTransaction.replace(R.id.frameLayout, AccountFragment.newInstance())
                        .commit()
                } else {
                    fragmentTransaction.replace(R.id.frameLayout, LoginFragment.newInstance())
                        .commit()
                }
            }
            true
        }
        bottomNavigationView!!.selectedItemId = R.id.nav_home
        disposables!!.add(userProfileViewModel.actionPublishSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<UserProfileAction?>() {
                override fun onNext(userProfileAction: UserProfileAction?) {
                    if (UserProfileAction.LOGIN === userProfileAction) {
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, AccountFragment.newInstance()).commit()
                    } else if (UserProfileAction.REGISTER_REQUEST === userProfileAction) {
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, RegisterFragment.newInstance()).commit()
                    } else if (UserProfileAction.REGISTER === userProfileAction) {
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, LoginFragment.newInstance()).commit()
                    }
                }

                override fun onError(e: Throwable) {
                    // TODO: 6/19/2021 snackbar something went wrong
                }

                override fun onComplete() {}
            })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables!!.dispose()
    }
}
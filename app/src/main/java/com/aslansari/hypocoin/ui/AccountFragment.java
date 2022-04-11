package com.aslansari.hypocoin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.viewmodel.account.AccountUIModel;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private UserProfileViewModel userProfileViewModel;
    private CompositeDisposable disposables;
    private TextView tvId;
    private TextView tvBalance;
    private Button buttonLogin;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        // add arguments if necessary
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get arguments if exists
        }
        disposables = new CompositeDisposable();
        userProfileViewModel = ((HypoCoinApp) getActivity().getApplication()).appContainer.userProfileViewModel;
        // TODO set id
        // TODO record to shard preference when an id is added
//        if (account != null) {
//            String accountId = UUID.randomUUID().toString();
//            accountViewModel.createAccount(accountId);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvId = view.findViewById(R.id.tvId);
        tvBalance = view.findViewById(R.id.tvBalance);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (userProfileViewModel.getId() != null && !userProfileViewModel.getId().isEmpty()) {
            disposables.add(userProfileViewModel.getAccount()
                    .subscribeWith(new DisposableSingleObserver<Account>() {

                        @Override
                        public void onSuccess(@NonNull Account account) {
                            tvId.setText(account.getId());
                            double balance = ((double) account.getBalance()) / 100;
                            tvBalance.setText(UserProfileViewModel.AMOUNT_FORMAT.format(balance));
                        }

                        @Override
                        public void onError(@NotNull Throwable t) {
                            Timber.e(t);
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }));
        }
        Observable.just(userProfileViewModel)
                .compose(getTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .scan(AccountUIModel.idle(), (state, result) -> {
                    return AccountUIModel.idle();
                }).subscribe(new DisposableObserver<AccountUIModel>() {
            @Override
            public void onNext(@NonNull AccountUIModel accountUIModel) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private ObservableTransformer<UserProfileViewModel, AccountUIModel> getTransformer() {
        return upstream -> upstream.flatMap(u -> {
            return Observable.just(new AccountUIModel())
                    .startWithItem(AccountUIModel.idle());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
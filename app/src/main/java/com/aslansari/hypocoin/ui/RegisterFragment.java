package com.aslansari.hypocoin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.viewmodel.DataStatus;
import com.aslansari.hypocoin.viewmodel.Resource;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private UserProfileViewModel userProfileViewModel;
    private CompositeDisposable disposables;
    private EditText etAccountId;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private Button buttonRegister;
    private ProgressBar progressRegister;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        disposables = new CompositeDisposable();
        userProfileViewModel = ((HypoCoinApp) getContext().getApplicationContext()).appContainer.userProfileViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        progressRegister = view.findViewById(R.id.progressLogin);
        etAccountId = view.findViewById(R.id.etAccountId);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordAgain = view.findViewById(R.id.etPasswordAgain);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposables.add(RxView.clicks(buttonRegister)
                .map(unit -> {
                    if (etAccountId.getText().toString().isEmpty()) {
                        return Resource.error("", new IllegalArgumentException("Username can't be empty"));
                    } else {
                        // TODO: 6/20/2021 check if username already taken
                        return Resource.complete("");
                    }
                })
                .map(resource -> {
                    if (resource.getStatus() == DataStatus.ERROR) {
                        return resource;
                    } else {
                        // TODO: 6/20/2021 check passwords match
                        String password = etPassword.getText().toString();
                        String passwordAgain = etPasswordAgain.getText().toString();
                        if (password.isEmpty() || passwordAgain.isEmpty()) {
                            // TODO: 6/20/2021 other password requirement checks
                            return Resource.error("", new IllegalArgumentException("password can't be empty"));
                        }
                        if (password.equals(passwordAgain)) {
                            return Resource.complete("");
                        } else {
                            return Resource.error("", new IllegalArgumentException("passwords does not mach"));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.rxjava3.observers.DisposableObserver<Resource<String>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Resource<String> stringResource) {
                        buttonRegister.setEnabled(DataStatus.LOADING != stringResource.getStatus());
                        progressRegister.setVisibility(stringResource.isLoading() ? View.VISIBLE : View.GONE);
                        switch (stringResource.getStatus()) {
                            case COMPLETE:
                                userProfileViewModel.register();
                                break;
                            case ERROR:
                                Toast.makeText(getContext(), stringResource.getThrowable().getMessage(), Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.dispose();
    }
}
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.register.Register;
import com.aslansari.hypocoin.register.RegisterViewModel;
import com.aslansari.hypocoin.register.dto.RegisterInput;
import com.aslansari.hypocoin.register.exception.PasswordMismatchException;
import com.aslansari.hypocoin.register.exception.UserAlreadyExistsException;
import com.aslansari.hypocoin.viewmodel.DataStatus;
import com.aslansari.hypocoin.viewmodel.Resource;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.view.RxViewGroup;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private UserProfileViewModel userProfileViewModel;
    private RegisterViewModel registerViewModel;
    private CompositeDisposable disposables;
    private EditText etAccountId;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private Button buttonRegister;
    private ProgressBar progressRegister;
    private CoordinatorLayout placeSnackbar;

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
        registerViewModel = ((HypoCoinApp) getContext().getApplicationContext()).appContainer.registerViewModel;
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
        placeSnackbar = view.findViewById(R.id.coordinator);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposables.add(RxView.clicks(buttonRegister)
                .map(unit -> new RegisterInput(etAccountId.getText().toString(),
                        etPassword.getText().toString(),
                        etPasswordAgain.getText().toString()))
                .observeOn(Schedulers.io())
                .flatMap(registerInput -> registerViewModel.validate(registerInput)
                        .startWithItem(Resource.loading(null)))
                .flatMap(registerInputResource -> {
                    if (DataStatus.COMPLETE == registerInputResource.getStatus()) {
                        return registerViewModel.register(registerInputResource.getValue());
                    } else if (registerInputResource.isLoading()) {
                        return Observable.just(Resource.loading(((Register) null)));
                    } else {
                        return Observable.just(Resource.error(((Register) null), registerInputResource.getThrowable()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Resource<Register>>() {
                    @Override
                    public void onNext(@NonNull Resource<Register> resource) {
                        buttonRegister.setEnabled(DataStatus.LOADING != resource.getStatus());
                        progressRegister.setVisibility(resource.isLoading() ? View.VISIBLE : View.GONE);
                        switch (resource.getStatus()) {
                            case COMPLETE:
                                Toast.makeText(getContext(), "Account registered successfully", Toast.LENGTH_LONG).show();
                                userProfileViewModel.register();
                                break;
                            case ERROR:
                                if (resource.getThrowable().getCause() instanceof UserAlreadyExistsException) {
                                    etAccountId.setError(getString(R.string.user_exists));
                                } else if (resource.getThrowable().getCause() instanceof PasswordMismatchException) {
                                    etPassword.setError(getString(R.string.password_mismatch));
                                    etPasswordAgain.setError(getString(R.string.password_mismatch));
                                } else {
                                    Snackbar.make(placeSnackbar, Objects.requireNonNull(resource.getThrowable().getMessage()), Snackbar.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Timber.e(throwable);
                        progressRegister.setVisibility(View.GONE);
                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );

        disposables.add(RxView.focusChanges(etPassword).skipInitialValue()
                .mergeWith(RxView.focusChanges(etPasswordAgain).skipInitialValue())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(focused -> {
                    if (focused) {
                        if (etPassword.getError() != null) {
                            etPassword.setText(null);
                            etPassword.setError(null);
                            etPasswordAgain.setText(null);
                            etPasswordAgain.setError(null);
                        }
                    }
                })
                .subscribe()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.dispose();
    }
}
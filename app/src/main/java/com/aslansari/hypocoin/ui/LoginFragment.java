package com.aslansari.hypocoin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel;
import com.jakewharton.rxbinding4.view.RxView;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private UserProfileViewModel userProfileViewModel;
    private CompositeDisposable disposables;
    private TextView tvId;
    private TextView tvRegisterRequest;
    private EditText etAccountId;
    private Button buttonLogin;
    private ProgressBar progressLogin;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        tvId = view.findViewById(R.id.tvId);
        tvRegisterRequest = view.findViewById(R.id.tvRegisterRequest);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        progressLogin = view.findViewById(R.id.progressLogin);
        etAccountId = view.findViewById(R.id.etAccountId);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposables.add(RxView.clicks(buttonLogin)
                // TODO: 6/19/2021 process login
                .map(unit -> LoginUIModel.complete())
                .subscribeWith(new DisposableObserver<LoginUIModel>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull LoginUIModel uiModel) {
                        buttonLogin.setEnabled(!uiModel.isLoading);
                        progressLogin.setVisibility(uiModel.isLoading ? View.VISIBLE : View.GONE);
                        if (uiModel.isFailed) {
                            // TODO set a proper error message
                            etAccountId.setError("NOT FOUND");
                        }
                        if (uiModel.isComplete) {
                            userProfileViewModel.login();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        // TODO trigger listener and change fragment
                        Toast.makeText(getContext(), "test", Toast.LENGTH_LONG).show();
                    }
                })
        );

        tvRegisterRequest.setOnClickListener(v -> userProfileViewModel.registerRequest());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
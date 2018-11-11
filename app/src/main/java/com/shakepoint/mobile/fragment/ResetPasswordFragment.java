package com.shakepoint.mobile.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.SigninActivity;
import com.shakepoint.mobile.data.req.ResetPasswordRequest;
import com.shakepoint.mobile.data.res.ResetPasswordResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    @BindView(R.id.resetPasswordNew)
    TextInputEditText resetPasswordNewText;

    @BindView(R.id.resetPasswordConfirm)
    TextInputEditText resetPasswordConfirmText;

    @BindView(R.id.resetPasswordButton)
    Button resetPasswordButton;

    private String token;
    private ProgressDialog progressDialog;
    private View currentView;
    private SecurityClient securityClient;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.resetPasswordButton)
    public void resetPassword() {
        //get values for passwords
        final String newPassword = resetPasswordNewText.getText().toString();
        final String confirmPassword = resetPasswordConfirmText.getText().toString();
        if (newPassword == null || newPassword.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Debes de proporcionar tu nueva contraseña", Snackbar.LENGTH_LONG).show();
            return;
        } else if (confirmPassword == null || confirmPassword.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Debes de confirmar tu nueva contraseña", Snackbar.LENGTH_LONG).show();
            return;
        }

        //check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "La contraseña debe de coincidir", Snackbar.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Procesando");
        progressDialog.setCancelable(false);
        progressDialog.show();

        securityClient.resetPassword(new ResetPasswordRequest(token, newPassword))
                .enqueue(new Callback<ResetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                        progressDialog.dismiss();
                        switch(response.code()) {
                            case 200:
                                //password have been changed successfully, start sign in activity
                                Intent intent = new Intent(getActivity(), SigninActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            case 400:
                                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.request_error, Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.setToken(token);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_reset_password, container, false);
            ButterKnife.bind(this, currentView);
            securityClient = RetroFactory.retrofit(getActivity()).create(SecurityClient.class);
        }
        return currentView;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

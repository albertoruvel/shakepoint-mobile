package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shakepoint.mobile.data.res.ForgotPasswordResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.forgotPasswordEmail)
    TextInputEditText emailText;

    @BindView(R.id.forgotPasswordConfirm)
    Button confirmButton;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private SecurityClient securityClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        securityClient = RetroFactory.retrofit(this).create(SecurityClient.class);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString();
                if (email == null || email.isEmpty()){
                    Snackbar.make(coordinatorLayout, "Debes de proporcionar un correo electrónico", Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.setMessage("Enviando petición");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                securityClient.forgotPasswordRequest(email).enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        progressDialog.dismiss();
                        switch(response.code()) {
                            case 200:
                                Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class));
                                break;
                            case 400:
                                Snackbar.make(coordinatorLayout, "El correo no fue encontrado, intenta de nuevo", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shakepoint.mobile.data.res.AuthenticationResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {

    @BindView(R.id.signinEmail)
    EditText email;

    @BindView(R.id.signinPassword)
    EditText password;

    @BindView(R.id.signinButton)
    Button signinButton;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private final SecurityClient securityClient = RetroFactory.retrofit().create(SecurityClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (! b){
                    //lost focus
                    if (SharedUtils.isEmailValid(email.getText().toString())){
                        //email valid
                        email.setError(null);
                    }else {
                        email.setError("Formato de email inválido");
                    }
                }
            }
        });
    }

    @OnClick(R.id.signinButton)
    public void signin() {
        final String emailValue = email.getText().toString();
        final String passwordValue = password.getText().toString();

        if (emailValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_email), Snackbar.LENGTH_LONG).show();
            return;
        } else if (passwordValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_password), Snackbar.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
        progressDialog.setMessage(getString(R.string.signing_in));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        securityClient.signin(emailValue, passwordValue)
                .enqueue(new Callback<AuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isSuccess()){
                                    SharedUtils.setAuthenticationToken(SigninActivity.this, response.body().getAuthenticationToken());
                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Snackbar.make(coordinatorLayout, getString(R.string.incorrect_credentials), Snackbar.LENGTH_LONG).show();
                                }
                                break;
                            case 500:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                        Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }
}

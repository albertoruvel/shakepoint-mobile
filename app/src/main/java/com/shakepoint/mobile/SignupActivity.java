package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.shakepoint.mobile.data.req.SignupRequest;
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

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.signupName)
    EditText name;

    @BindView(R.id.signupEmail)
    EditText email;

    @BindView(R.id.signupPassword)
    EditText password;

    @BindView(R.id.signupPassword2)
    EditText password2;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private SecurityClient securityClient;
    private ProgressDialog progressDialog;
    private boolean isEmailValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityClient  = RetroFactory.retrofit(this).create(SecurityClient.class);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (! b){
                    if (validateEmail()){
                        email.setError(null);
                        isEmailValid = true;
                    }else {
                        email.setError("Formato de correo inválido");
                        isEmailValid = false;
                    }
                }
            }
        });
    }

    private boolean validateEmail() {
        final String emailValue = email.getText().toString();
        return SharedUtils.isEmailValid(emailValue);
    }

    @OnClick(R.id.signupButton)
    public void signup() {
        final String nameValue = name.getText().toString();
        final String emailValue = email.getText().toString();
        final String password1Value = password.getText().toString();
        final String password2Value = password2.getText().toString();

        //validate
        if (nameValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_name), Snackbar.LENGTH_LONG).show();
            return;
        } else if (emailValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_email), Snackbar.LENGTH_LONG).show();
            return;
        } else if (! isEmailValid) {
            Snackbar.make(coordinatorLayout, "Formato de correo inválido", Snackbar.LENGTH_LONG).show();
            return;
        } else if (password1Value.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_password), Snackbar.LENGTH_LONG).show();
            return;
        } else if (password2Value.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_password_confirmation), Snackbar.LENGTH_LONG).show();
            return;
        } else if (!password1Value.equals(password2Value)) {
            Snackbar.make(coordinatorLayout, getString(R.string.invalid_passwords), Snackbar.LENGTH_LONG).show();
            return;
        }

        SignupRequest request = new SignupRequest(nameValue, emailValue, password1Value);
        progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
        progressDialog.setMessage(getString(R.string.signing_up));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        securityClient.signup(request)
                .enqueue(new Callback<AuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                        switch (response.code()) {
                            case 200:
                                SharedUtils.setAuthenticationToken(SignupActivity.this, response.body().getAuthenticationToken());
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            case 500:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                return;
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

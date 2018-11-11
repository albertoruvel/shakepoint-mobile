package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shakepoint.mobile.admin.AdminMainActivity;
import com.shakepoint.mobile.data.req.SignInRequest;
import com.shakepoint.mobile.data.res.AuthenticationResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;
import com.shakepoint.mobile.util.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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

    @BindView(R.id.signinForgotPassword)
    TextView forgotPassword;

    @BindView(R.id.login_button)
    LoginButton facebookLoginButton;

    private ProgressDialog progressDialog;
    private SecurityClient securityClient;
    private boolean isEmailValid;
    private String facebookId;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         securityClient = RetroFactory.retrofit(this).create(SecurityClient.class);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //get current profile
                getFacebookData(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
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
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SigninActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFacebookData(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("Facebook me response", "This is not an error\n" + object.toString());
                try{
                    final String id = (String)object.get("id");
                    facebookId = id;
                    signIn(true);
                }catch(JSONException ex){

                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean validateEmail() {
        final String emailValue = email.getText().toString();
        return SharedUtils.isEmailValid(emailValue);
    }

    private void executeSignInRequest(SignInRequest request) {
        progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
        progressDialog.setMessage(getString(R.string.signing_in));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        securityClient.signin(request)
                .enqueue(new Callback<AuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isSuccess()) {
                                    Intent intent;
                                    if (response.body().getRole().equals(SharedUtils.ADMIN_ROLE)) {
                                        SharedUtils.setAuthenticationToken(SigninActivity.this, response.body().getAuthenticationToken(),
                                                response.body().getRole());
                                        intent = new Intent(SigninActivity.this, AdminMainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    } else {
                                        SharedUtils.setAuthenticationToken(SigninActivity.this, response.body().getAuthenticationToken(),
                                                response.body().getRole());
                                        intent = new Intent(SigninActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    }
                                    startActivity(intent);
                                } else {
                                    if (facebookId != null) {
                                        Snackbar.make(coordinatorLayout, "El usuario de Facebook no existe, regístrate y continúa", Snackbar.LENGTH_LONG).show();
                                    }else{
                                        Snackbar.make(coordinatorLayout, getString(R.string.incorrect_credentials), Snackbar.LENGTH_LONG).show();
                                    }
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

    private void signIn(boolean facebookLogin) {
        if (facebookLogin) {
            executeSignInRequest(new SignInRequest(facebookId));
        } else {
            final String emailValue = email.getText().toString();
            final String passwordValue = password.getText().toString();

            if (emailValue.isEmpty()) {
                Snackbar.make(coordinatorLayout, getString(R.string.required_email), Snackbar.LENGTH_LONG).show();
                return;
            } else if (!isEmailValid){
                Snackbar.make(coordinatorLayout, "Formato de correo inválido", Snackbar.LENGTH_LONG).show();
                return;
            }else if (passwordValue.isEmpty()) {
                Snackbar.make(coordinatorLayout, getString(R.string.required_password), Snackbar.LENGTH_LONG).show();
                return;
            }

            executeSignInRequest(new SignInRequest(emailValue, passwordValue));
        }
    }

    @OnClick(R.id.signinButton)
    public void signin() {
        signIn(false);
    }
}

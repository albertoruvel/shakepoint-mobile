package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shakepoint.mobile.data.req.SignupRequest;
import com.shakepoint.mobile.data.res.AuthenticationResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;
import com.shakepoint.mobile.util.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

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

    @BindView(R.id.signupBirthDate)
    EditText birthDate;

    @BindView(R.id.signupPassword)
    EditText password;

    @BindView(R.id.signupPassword2)
    EditText password2;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.login_button)
    LoginButton facebookLoginButton;

    private SecurityClient securityClient;
    private ProgressDialog progressDialog;
    private boolean isEmailValid;
    private String facebookId;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityClient = RetroFactory.retrofit(this).create(SecurityClient.class);
        setContentView(R.layout.activity_signup);
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
                if (!b) {
                    if (validateEmail()) {
                        email.setError(null);
                        isEmailValid = true;
                    } else {
                        email.setError("Formato de correo inválido");
                        isEmailValid = false;
                    }
                }
            }
        });
        birthDate.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    birthDate.setText(current);
                    birthDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getFacebookData(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("Facebook me response", "This is not an error\n" + object.toString());
                try{
                    final String id = (String)object.get("id");
                    final String facebookEmail = (String)object.get("email");
                    facebookId = id;
                    email.setText(facebookEmail);

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

    @OnClick(R.id.signupButton)
    public void signup() {
        final String nameValue = name.getText().toString();
        final String emailValue = email.getText().toString();
        final String password1Value = password.getText().toString();
        final String password2Value = password2.getText().toString();
        final String birthDateValue = birthDate.getText().toString();

        //validate
        if (nameValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_name), Snackbar.LENGTH_LONG).show();
            return;
        } else if (emailValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_email), Snackbar.LENGTH_LONG).show();
            return;
        } else if (!isEmailValid) {
            Snackbar.make(coordinatorLayout, "Formato de correo inválido", Snackbar.LENGTH_LONG).show();
            return;
        } else if (birthDateValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, "Debes de ingresar fecha de nacimiento", Snackbar.LENGTH_LONG).show();
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

        SignupRequest request = new SignupRequest(nameValue, emailValue, password1Value, birthDateValue);
        progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
        progressDialog.setMessage(getString(R.string.signing_up));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (facebookId != null) {
            request.setFacebookId(facebookId);
        }
        securityClient.signup(request)
                .enqueue(new Callback<AuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                        switch (response.code()) {
                            case 200:
                                SharedUtils.setAuthenticationToken(SignupActivity.this, response.body().getAuthenticationToken(),
                                        response.body().getRole());
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            case 500:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                return;
                            case 400:
                                Snackbar.make(coordinatorLayout, "Revisa tus datos e intenta de nuevo", Snackbar.LENGTH_LONG).show();
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

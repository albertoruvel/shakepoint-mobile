package com.shakepoint.mobile.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.ValidateForgotPasswordTokenResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.SecurityClient;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ValidateSecurityTokenFragment extends Fragment {

    @BindView(R.id.validateSecurityTokenText)
    TextInputEditText securityTokenText;

    @BindView(R.id.validateSecurityTokenLayout)
    LinearLayout validateSecurityTokenLayout;

    @BindView(R.id.validateSecurityTokenMessage)
    TextView validateSecurityTokenMessage;


    private View view;

    private SecurityClient securityClient;
    private TokenValidationCallback callback;

    public ValidateSecurityTokenFragment() {
        // Required empty public constructor

    }

    public void setCallback(TokenValidationCallback callback) {
        this.callback = callback;
    }

    public static ValidateSecurityTokenFragment newInstance(TokenValidationCallback callback) {
        ValidateSecurityTokenFragment fragment = new ValidateSecurityTokenFragment();
        fragment.setCallback(callback);
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
        if (view == null) {
            securityClient = RetroFactory.retrofit(getActivity()).create(SecurityClient.class);
            view = inflater.inflate(R.layout.fragment_validate_security_token, container, false);
            ButterKnife.bind(this, view);
            securityTokenText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_GO){
                        //check text
                        final String token = securityTokenText.getText().toString();
                        if (token != null && !token.isEmpty()) {
                            //validate
                            validateSecurityToken(token);
                        }
                    }
                    return false;
                }
            });
        }
        return view;
    }

    private void validateSecurityToken(final String token) {
        validateSecurityTokenLayout.setVisibility(View.VISIBLE);
        securityClient.validateForgotPasswordToken(token).enqueue(new Callback<ValidateForgotPasswordTokenResponse>() {
            @Override
            public void onResponse(Call<ValidateForgotPasswordTokenResponse> call, Response<ValidateForgotPasswordTokenResponse> response) {
                switch(response.code()){
                    case 200:
                        if (response.body().isTokenValid()) {
                            //change to another fragment
                            callback.onTokenValidationSuccess(token);
                        } else {
                            validateSecurityTokenMessage.setText("El código de seguridad es inválido, vuelve a intentarlo");
                        }
                        break;
                    case 400:
                        validateSecurityTokenMessage.setText("Intenta de nuevo");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ValidateForgotPasswordTokenResponse> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                validateSecurityTokenLayout.setVisibility(View.GONE);
            }
        });
    }

    public interface TokenValidationCallback{
        void onTokenValidationSuccess(String token);
    }

}

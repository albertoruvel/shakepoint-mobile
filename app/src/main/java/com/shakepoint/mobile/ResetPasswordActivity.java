package com.shakepoint.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.shakepoint.mobile.fragment.ResetPasswordFragment;
import com.shakepoint.mobile.fragment.ValidateSecurityTokenFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {


    private ValidateSecurityTokenFragment validateSecurityTokenFragment;
    private ResetPasswordFragment resetPasswordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        validateSecurityTokenFragment = ValidateSecurityTokenFragment.newInstance(new ValidateSecurityTokenFragment.TokenValidationCallback() {
            @Override
            public void onTokenValidationSuccess(String token) {
                resetPasswordFragment = ResetPasswordFragment.newInstance(token);
                getSupportFragmentManager().beginTransaction().remove(validateSecurityTokenFragment).commit();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.resetPasswordFrameLayout, resetPasswordFragment).commit();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.resetPasswordFrameLayout, validateSecurityTokenFragment).commit();
    }
}

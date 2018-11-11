package com.shakepoint.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.admin.AdminMainActivity;
import com.shakepoint.mobile.data.internal.SecurityRole;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.welcomeSignup)
    Button signupButton;

    @BindView(R.id.welcomeSignin)
    TextView signinButton;

    private static int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        if (!SharedUtils.areTermsAccepted(this)) {
            //go to terms activity
            Intent intent = new Intent(this, ApplicationTermsActivity.class);
            intent.putExtra(ApplicationTermsActivity.SHOW_ACTION_BUTTONS, Boolean.TRUE);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (!SharedUtils.getAuthenticationToken(this).isEmpty()) {
            //start main activity
            Intent intent;
            if (SharedUtils.getAuthenticationRole(this) == SecurityRole.ADMIN) {
                intent = new Intent(WelcomeActivity.this, AdminMainActivity.class);
            } else {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**@Override
    public void onResume() {
        super.onResume();
        if (!SharedUtils.areTermsAccepted(this)) {
            Toast.makeText(this, "Para poder disfrutar de Shakepoint, hay que aceptar los terminos y condiciones.", Toast.LENGTH_LONG).show();
            finishAffinity();
        }
    }**/

    @OnClick(R.id.welcomeSignup)
    public void signup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.welcomeSignin)
    public void signin() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(ApplicationTermsActivity.TERMS_RESULT, false)) {
                SharedUtils.setAcceptedTerms(this, Boolean.TRUE);
            } else {
                Toast.makeText(this, "Para poder disfrutar de Shakepoint, hay que aceptar los terminos y condiciones.", Toast.LENGTH_LONG).show();
                finishAffinity();
            }
        }
    }
}

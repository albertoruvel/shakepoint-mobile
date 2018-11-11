package com.shakepoint.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplicationTermsActivity extends AppCompatActivity {

    @BindView(R.id.termsWebView)
    WebView webView;

    @BindView(R.id.termsActionLayout)
    LinearLayout termsActionLayout;

    public static final String TERMS_RESULT = "termsResult";
    public static final String SHOW_ACTION_BUTTONS = "showActionButtons";
    private static final String TERMS_URL = "https://sites.google.com/view/shakepoint-privacy-policy-home/p%C3%A1gina-principal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_terms);
        ButterKnife.bind(this);
        showTerms();
    }

    @OnClick(R.id.termsAccept)
    public void acceptTerms() {
        Intent data = new Intent();
        data.putExtra(TERMS_RESULT, Boolean.TRUE);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.termsDecline)
    public void declineTerms() {
        Intent data = new Intent();
        data.putExtra(TERMS_RESULT, Boolean.FALSE);
        setResult(RESULT_OK, data);
        finish();
    }

    private void showTerms() {
        if (getIntent().getBooleanExtra(SHOW_ACTION_BUTTONS, false)) {
            //show linear layout with buttons
            termsActionLayout.setVisibility(View.VISIBLE);
        } else {
            //hide linear layout with buttons
            termsActionLayout.setVisibility(View.GONE);
        }
        webView.loadUrl(TERMS_URL);
    }
}

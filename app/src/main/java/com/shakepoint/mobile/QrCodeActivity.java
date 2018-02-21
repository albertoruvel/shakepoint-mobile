package com.shakepoint.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QrCodeActivity extends AppCompatActivity {

    public static final String QR_CODE_URL = "qrCodeUrl";

    @BindView(R.id.qrCodeImage)
    ImageView qrCode;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        final String qrCodeUrl = getIntent().getStringExtra(QR_CODE_URL);
        ButterKnife.bind(this);
        Picasso.with(this).load(qrCodeUrl).into(qrCode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

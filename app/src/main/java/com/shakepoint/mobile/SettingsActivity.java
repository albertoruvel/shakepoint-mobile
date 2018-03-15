package com.shakepoint.mobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.shakepoint.mobile.data.internal.CardInfo;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settingsCardTermination)
    TextView cardTermination;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final int CARD_ACTIVITY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        CardInfo cardInfo = SharedUtils.getCardInfo(this);
        setCardInfo(cardInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setCardInfo(CardInfo cardInfo) {
        if (cardInfo != null) {
            final String[] groups = cardInfo.getCardNumber().split("-");
            //get last group of digits
            cardTermination.setText("Terminación de tarjeta *" + groups[3]);
        } else {
            cardTermination.setText("Aún no tienes una tarjeta preferida");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.settingsPaymentMethod)
    public void paymentMethodClick() {
        Intent intent = new Intent(this, CardActivity.class);
        startActivityForResult(intent, CARD_ACTIVITY_REQUEST_CODE);
    }

    @OnClick(R.id.settingsProfile)
    public void profileClick() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @OnClick(R.id.settingsContact)
    public void profileContact() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alexfp1107@gmail.com", "m.industrialdesigns@gmail.com", "albertoruvel@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Shakepoint application contact");
        intent.putExtra(Intent.EXTRA_TEXT, "Escribe tu mensaje :)");
        startActivity(intent);
    }

    @OnClick(R.id.settingsSignout)
    public void profileSignOut() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_question))
                .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedUtils.clear(SettingsActivity.this);
                        Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CARD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            if (data.getBooleanExtra(CardActivity.CARD_SAVED, Boolean.FALSE)){
                //change text
                CardInfo cardInfo = SharedUtils.getCardInfo(this);
                setCardInfo(cardInfo);
            }
        }
    }
}

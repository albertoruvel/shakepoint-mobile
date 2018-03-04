package com.shakepoint.mobile;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shakepoint.mobile.util.SharedUtils;
import com.shakepoint.mobile.watcher.BankCardFormatWatcher;
import com.shakepoint.mobile.watcher.ExpirationDateFormatWatcher;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardActivity extends AppCompatActivity {

    @BindView(R.id.cardNumber)
    TextInputEditText cardNumber;

    @BindView(R.id.cardExpiration)
    TextInputEditText cardExpirationDate;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardNumber.addTextChangedListener(new BankCardFormatWatcher());
        cardExpirationDate.addTextChangedListener(new ExpirationDateFormatWatcher());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.saveCard:
                saveCard();
                break;
        }
        return true;
    }

    private void saveCard() {
        final String cardNumberValue = cardNumber.getText().toString();
        final String cardExpirationDateValue = cardExpirationDate.getText().toString();

        if (cardNumberValue.length() != 19) {
            //show error message
            showErrorMessage("El número de tarjeta es incorrecto, favor de verificarlo");
        } else if (cardExpirationDateValue.length() != 5) {
            //show error message
            showErrorMessage("La fecha de expiración es incorrecta, favor de verificarla");
        } else {
            //show alert message
            new AlertDialog.Builder(this)
                    .setMessage("Seguro que deseas guardar esta tarjeta?")
                    .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedUtils.saveCardInfo(CardActivity.this, cardNumberValue, cardExpirationDateValue);
                            Toast.makeText(CardActivity.this, "Se han guardados los datos de tu tarjeta", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        }
    }

    private void showErrorMessage(String s) {
        new AlertDialog.Builder(this)
                .setMessage(s)
                .setNeutralButton("Aceptar", null)
                .create().show();
    }
}

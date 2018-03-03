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
        cardExpirationDate.addTextChangedListener(new TextWatcher() {
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
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        //day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d", mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    cardExpirationDate.setText(current);
                    cardExpirationDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

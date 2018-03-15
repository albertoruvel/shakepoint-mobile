package com.shakepoint.mobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.util.SharedUtils;
import com.shakepoint.mobile.watcher.BankCardFormatWatcher;
import com.shakepoint.mobile.watcher.ExpirationDateFormatWatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardActivity extends AppCompatActivity {

    public static final String CARD_SAVED = "cardSavedResult";

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardNumber.addTextChangedListener(new BankCardFormatWatcher());
        cardExpirationDate.addTextChangedListener(new TextWatcher() {
            private String mLastInput = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
                Calendar expiryDateDate = Calendar.getInstance();

                try {
                    Integer currentYear = expiryDateDate.get(Calendar.YEAR);
                    expiryDateDate.setTime(formatter.parse(input));
                    //check if year is in the past
                    Integer expirationyear = Integer.parseInt(s.toString().split("/")[1]);
                    expirationyear += 2000; //we are on 2018, so..
                    Integer expirationMonth = Integer.parseInt(s.toString().split("/")[0]);
                    Integer currentMonth = expiryDateDate.get(Calendar.MONTH) + 1;

                    if (expirationyear < currentYear){
                        cardExpirationDate.setError("La tarjeta ya ha expirado");
                    }else if (expirationyear == currentYear){
                        //same year, must check month
                        if (expirationMonth < currentMonth){
                            cardExpirationDate.setError("La tarjeta ya ha expirado");
                        }else{
                            cardExpirationDate.setError(null);
                        }
                        cardExpirationDate.setError(null);
                    }
                } catch (ParseException e) {
                    if (s.length() == 2 && !mLastInput.endsWith("/")) {
                        int month = Integer.parseInt(input);
                        if (month <= 12) {
                            cardExpirationDate.setText(cardExpirationDate.getText().toString() + "/");
                            cardExpirationDate.setSelection(cardExpirationDate.getText().toString().length());
                        } else {
                            cardExpirationDate.setText("");
                            cardExpirationDate.setSelection(cardExpirationDate.getText().toString().length());
                        }
                    } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                        int month = Integer.parseInt(input);
                        if (month <= 12) {
                            cardExpirationDate.setText(cardExpirationDate.getText().toString().substring(0, 1));
                            cardExpirationDate.setSelection(cardExpirationDate.getText().toString().length());
                        } else {
                            cardExpirationDate.setText("");
                            cardExpirationDate.setSelection(cardExpirationDate.getText().toString().length());
                        }
                    } else if (s.length() == 1) {
                        try{
                            int month = Integer.parseInt(input);
                            if (month > 1) {
                                cardExpirationDate.setText("0" + cardExpirationDate.getText().toString() + "/");
                                cardExpirationDate.setSelection(cardExpirationDate.getText().toString().length());
                            }
                        }catch(NumberFormatException ex){
                            //not valid character
                            Toast.makeText(CardActivity.this, "Sólo se permiten valores numéricos", Toast.LENGTH_LONG).show();
                            s.clear();
                        }
                    }

                    mLastInput = cardExpirationDate.getText().toString();
                    return;

                }
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
                setResult(RESULT_CANCELED);
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
                            Intent intent = new Intent();
                            intent.putExtra(CARD_SAVED, Boolean.TRUE);
                            setResult(RESULT_OK, intent);
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

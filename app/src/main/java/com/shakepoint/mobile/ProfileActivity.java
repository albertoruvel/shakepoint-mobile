package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shakepoint.mobile.data.req.ProfileRequest;
import com.shakepoint.mobile.data.res.ProfileResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.profileName)
    TextView name;

    @BindView(R.id.profileAge)
    TextView age;

    @BindView(R.id.profileWeight)
    TextInputEditText weight;

    @BindView(R.id.profileHeight)
    TextInputEditText height;

    @BindView(R.id.profileUserSince)
    TextView userSince;

    @BindView(R.id.profileTotalPurchases)
    TextView totalPurchases;

    @BindView(R.id.profileContentLayout)
    LinearLayout contentLayout;

    @BindView(R.id.profileProgressBar)
    ProgressBar progressBar;


    @BindView(R.id.profileBirthDate)
    TextInputEditText birthDate;

    @BindView(R.id.profileCmi)
    TextView cmi;

    @BindView(R.id.profileEmail)
    TextView email;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private static final ShopClient shopClient = RetroFactory.retrofit().create(ShopClient.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    age.setText("" + getAge(current));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getProfile();
    }

    private Integer getAge(String current) {
        try {
            Date birthdate = SharedUtils.SIMPLE_DATE_FORMAT.parse(current);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthdate);
            Calendar today = Calendar.getInstance();
            int curYear = today.get(Calendar.YEAR);
            int dobYear = dob.get(Calendar.YEAR);
            int age = curYear - dobYear;
            // if dob is month or day is behind today's month or day
            // reduce age by 1
            int curMonth = today.get(Calendar.MONTH);
            int dobMonth = dob.get(Calendar.MONTH);

            if (dobMonth > curMonth) { // this year can't be counted!
                age--;
            } else if (dobMonth == curMonth) { // same month? check for day
                int curDay = today.get(Calendar.DAY_OF_MONTH);
                int dobDay = dob.get(Calendar.DAY_OF_MONTH);
                if (dobDay > curDay) { // this year can't be counted!
                    age--;
                }
            }
            return age;

        } catch (ParseException ex) {
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.saveProfile:
                saveProfile();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfile() {
        shopClient.getUserProfile(SharedUtils.getAuthenticationHeader(this))
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isAvailableProfile()) {
                                    populateProfile(response.body());
                                } else {
                                    //set only name, email, cmi user since
                                    name.setText(response.body().getUserName());
                                    email.setText(response.body().getEmail());
                                    try {
                                        final Date date = dateFormat.parse(response.body().getUserSince());
                                        userSince.setText(dateFormat.format(date));
                                    } catch (ParseException ex) {
                                        userSince.setText("N/A");
                                    }

                                    totalPurchases.setText("$" + response.body().getPurchasesTotal());
                                    cmi.setText("" + SharedUtils.round(response.body().getCmi(), 2));
                                }
                                contentLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {

                    }
                });
    }

    private void populateProfile(ProfileResponse body) {
        name.setText(body.getUserName());
        age.setText("" + getAge(body.getBirthday()));
        weight.setText("" + body.getWeight());
        height.setText("" + body.getHeight());
        userSince.setText("" + body.getUserSince());
        totalPurchases.setText("" + body.getPurchasesTotal());
        email.setText(body.getEmail());

        cmi.setText("" + SharedUtils.round(body.getCmi(), 2));
        birthDate.setText(body.getBirthday());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        name.setText("");
        email.setText("");
        age.setText("");
        weight.setText("");
        height.setText("");
        birthDate.setText("");
        userSince.setText("");
        totalPurchases.setText("");
        cmi.setText("");
    }

    public void saveProfile() {
        final String weightValue = weight.getText().toString();
        final String heightValue = height.getText().toString();
        final String birthDateValue = birthDate.getText().toString();

        if (weightValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_weight), Snackbar.LENGTH_LONG).show();
            return;
        } else if (heightValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_height), Snackbar.LENGTH_LONG).show();
            return;
        } else {
            //validate birthdate
            try {
                Date date = dateFormat.parse(birthDateValue); //this will throw exception when no birthdate is set
                progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
                progressDialog.setMessage("Guardando perfil");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                //save profile
                shopClient.saveProfile(SharedUtils.getAuthenticationHeader(this),
                        new ProfileRequest(Double.parseDouble(heightValue), Double.parseDouble(weightValue), birthDateValue))
                        .enqueue(new Callback<ProfileResponse>() {
                            @Override
                            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                                switch (response.code()) {
                                    case 200:
                                        cmi.setText("" + SharedUtils.round(response.body().getCmi(), 2));
                                        totalPurchases.setText("$" + response.body().getPurchasesTotal());
                                        birthDate.setText(response.body().getBirthday());
                                        Snackbar.make(coordinatorLayout, "Perfil actualizado correctamente", Snackbar.LENGTH_LONG).show();
                                        View view = getCurrentFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        break;
                                    case 500:
                                        Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                                progressDialog.dismiss();
                            }
                        });
            } catch (ParseException ex) {
                //invalid date
                Snackbar.make(coordinatorLayout, getString(R.string.required_birthdate), Snackbar.LENGTH_LONG).show();
                return;
            }
        }
    }
}

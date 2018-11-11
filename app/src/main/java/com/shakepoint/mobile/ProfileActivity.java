package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.Context;
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
    TextView birthDate;

    @BindView(R.id.profileCmi)
    TextView cmi;

    @BindView(R.id.profileEmail)
    TextView email;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private static ShopClient shopClient;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopClient = RetroFactory.retrofit(this).create(ShopClient.class);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getProfile();
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
                                        userSince.setText(getShortDate(response.body().getUserSince()));
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

    private String getShortDate(String userSince) throws ParseException {
        final Date date = dateFormat.parse(userSince);
        String dateValue = SharedUtils.SIMPLE_DATE_FORMAT.format(date);
        return dateValue;
    }

    private void populateProfile(ProfileResponse body) {
        name.setText(body.getUserName());
        age.setText("" + SharedUtils.getAge(body.getBirthday()));
        weight.setText("" + body.getWeight());
        height.setText("" + body.getHeight());
        try{
            userSince.setText(getShortDate(body.getUserSince()));
        }catch(ParseException ex){

        }
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

        if (weightValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_weight), Snackbar.LENGTH_LONG).show();
            return;
        } else if (heightValue.isEmpty()) {
            Snackbar.make(coordinatorLayout, getString(R.string.required_height), Snackbar.LENGTH_LONG).show();
            return;
        } else {
            progressDialog = new ProgressDialog(this, R.style.ColoredProgressDialog);
            progressDialog.setMessage("Guardando perfil");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            //save profile
            shopClient.saveProfile(SharedUtils.getAuthenticationHeader(this),
                    new ProfileRequest(Double.parseDouble(heightValue), Double.parseDouble(weightValue)))
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
        }
    }
}

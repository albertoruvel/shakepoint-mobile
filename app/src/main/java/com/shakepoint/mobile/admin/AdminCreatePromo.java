package com.shakepoint.mobile.admin;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.internal.PromotionType;
import com.shakepoint.mobile.data.req.CreatePromoCodeRequest;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.admin.CreatePromoCodeResponse;
import com.shakepoint.mobile.data.res.admin.Product;
import com.shakepoint.mobile.data.res.admin.Trainer;
import com.shakepoint.mobile.filter.MinMaxInputFilter;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreatePromo extends AppCompatActivity {

    @BindView(R.id.createPromoDate)
    TextInputEditText expirationDate;

    @BindView(R.id.createPromoDiscount)
    TextInputEditText discount;

    @BindView(R.id.createPromoDescription)
    TextInputEditText description;

    @BindView(R.id.createPromoType)
    Spinner type;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.createPromoExtraLayout)
    LinearLayout extraLayout;

    @BindView(R.id.createPromoExtra)
    Spinner extraSpinner;

    @BindView(R.id.createPromoExtraMessage)
    TextView extraMessage;

    private AdminClient adminClient;
    private List<ProductResponse> products;
    private List<Trainer> trainers;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_promo);
        ButterKnife.bind(this);
        discount.setFilters(new InputFilter[]{new MinMaxInputFilter(1, 100)});
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);
        adminClient.getProducts(SharedUtils.getAuthenticationToken(this))
                .enqueue(new Callback<List<ProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                        //save products to tiny cache
                        products = response.body();
                    }

                    @Override
                    public void onFailure(Call<List<ProductResponse>> call, Throwable t) {

                    }
                });
        adminClient.getAllTrainers(SharedUtils.getAuthenticationToken(this))
                .enqueue(new Callback<List<Trainer>>() {
                    @Override
                    public void onResponse(Call<List<Trainer>> call, Response<List<Trainer>> response) {
                        trainers = response.body();
                    }

                    @Override
                    public void onFailure(Call<List<Trainer>> call, Throwable t) {

                    }
                });
        expirationDate.addTextChangedListener(new TextWatcher() {
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
                    expirationDate.setText(current);
                    expirationDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setupPromoTypes();
    }

    private void setupPromoTypes() {
        final List<PromotionType> promotionTypes = Arrays.asList(PromotionType.values());
        ArrayAdapter<PromotionType> promotionTypeAdapter =
                new ArrayAdapter<PromotionType>(this, android.R.layout.simple_list_item_1, PromotionType.values());
        type.setAdapter(promotionTypeAdapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PromotionType type = promotionTypes.get(position);
                switch (type) {
                    case SEASON_SINGLE_PRODUCT:
                        //show products spinner here...
                        //change message
                        extraMessage.setText("Selecciona un producto");
                        extraLayout.setVisibility(View.VISIBLE);
                        showProducts();
                        break;
                    case ALL_PRODUCTS:
                        //hide extra spinner
                        extraLayout.setVisibility(View.GONE);
                        break;
                    case SEASON:
                        //hide extra spinner
                        extraLayout.setVisibility(View.GONE);
                        break;
                    case SINGLE_PRODUCT:
                        //show products spinner here...
                        extraMessage.setText("Selecciona un producto");
                        extraLayout.setVisibility(View.VISIBLE);
                        showProducts();
                        break;
                    case TRAINER:
                        //show trainers spinner here...
                        extraMessage.setText("Selecciona un entrenador");
                        extraLayout.setVisibility(View.VISIBLE);
                        showTrainers();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTrainers() {
        ArrayAdapter<Trainer> adapter =
                new ArrayAdapter<Trainer>(this, android.R.layout.simple_list_item_1, trainers);
        extraSpinner.setAdapter(adapter);
    }

    private void showProducts() {
        ArrayAdapter<ProductResponse> adapter =
                new ArrayAdapter<ProductResponse>(this, android.R.layout.simple_list_item_1, products);
        extraSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.adminCreatePromoSave:
                createPromo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createPromo() {
        CreatePromoCodeRequest request = new CreatePromoCodeRequest();
        String expirationDateValue = expirationDate.getText().toString();
        String discountValue = discount.getText().toString();
        String descValue = description.getText().toString();
        PromotionType promotionTypeEnum = (PromotionType) type.getSelectedItem();
        request.setDescription(descValue);
        request.setDiscount(Integer.parseInt(discountValue));
        request.setExpirationDate(expirationDateValue);
        request.setPromotionType(promotionTypeEnum.getValue());
        ProductResponse product;
        Trainer trainer;
        switch (promotionTypeEnum) {
            case ALL_PRODUCTS:
                request.setAllProducts(true);
                break;
            case SEASON:
                request.setAllProducts(true);
                break;
            case SEASON_SINGLE_PRODUCT:
                product = (ProductResponse) extraSpinner.getSelectedItem();
                request.setProductId(product.getId());
                break;
            case SINGLE_PRODUCT:
                product = (ProductResponse) extraSpinner.getSelectedItem();
                request.setProductId(product.getId());
                break;
            case TRAINER:
                trainer = (Trainer)extraSpinner.getSelectedItem();
                request.setTrainerId(trainer.getId());
                break;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creando promoción");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        adminClient.createPromoCode(SharedUtils.getAuthenticationToken(this), request)
                .enqueue(new Callback<CreatePromoCodeResponse>() {
                    @Override
                    public void onResponse(Call<CreatePromoCodeResponse> call, Response<CreatePromoCodeResponse> response) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminCreatePromo.this, "La promoción ha sido creada exitosamente", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<CreatePromoCodeResponse> call, Throwable t) {
                        Toast.makeText(AdminCreatePromo.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_promo, menu);
        return true;
    }
}

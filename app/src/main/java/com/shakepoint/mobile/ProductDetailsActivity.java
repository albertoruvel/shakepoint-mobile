package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.adapter.ProductDetailsAdapter;
import com.shakepoint.mobile.adapter.TabbedSectionAdapter;
import com.shakepoint.mobile.data.internal.CardInfo;
import com.shakepoint.mobile.data.req.ConfirmPurchaseRequest;
import com.shakepoint.mobile.data.res.AvailablePurchaseResponse;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.PurchaseConfirmationResponse;
import com.shakepoint.mobile.data.res.SimpleQRCodeResponse;
import com.shakepoint.mobile.fragment.NutritionalDataFragment;
import com.shakepoint.mobile.fragment.ProductDetailsFragment;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    public static final String PRODUCT_ID = "productId";

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.productDetailsViewPager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.productDetailsBuyButton)
    Button buyButton;

    private ProgressDialog progressDialog;
    private String productId;
    private ShopClient shopClient;

    private ProductDetailsAdapter adapter;

    public ProductDetailsActivity(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopClient = RetroFactory.retrofit(this).create(ShopClient.class);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productId = getIntent().getStringExtra(PRODUCT_ID);
        //setup view pager
        setupViewPager();
        getProductDetails(productId);
    }

    private void setupViewPager() {
        adapter = new ProductDetailsAdapter(getSupportFragmentManager(), productId);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getAvailablePurchase(final String cvvValue) {
        shopClient.getAvailablePurchaseForMachine(SharedUtils.getAuthenticationHeader(this),
                productId, SharedUtils.getPreferredMachine(this).getMachineId())
                .enqueue(new Callback<AvailablePurchaseResponse>() {
                    @Override
                    public void onResponse(Call<AvailablePurchaseResponse> call, Response<AvailablePurchaseResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().getPurchaseId() != null) {
                                    progressDialog.setMessage(getString(R.string.syncing_server));
                                    confirmPurchase(response.body().getPurchaseId(), cvvValue);
                                } else {
                                    progressDialog.dismiss();
                                    //no purchases available for machine
                                    new AlertDialog.Builder(ProductDetailsActivity.this)
                                            .setMessage(getString(R.string.offline_machine))
                                            .setPositiveButton(getString(R.string.machine_change), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startActivity(new Intent(ProductDetailsActivity.this, SearchMachineActivity.class));
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.cancelled), Toast.LENGTH_LONG).show();
                                                    ProductDetailsActivity.this.finish();
                                                }
                                            })
                                            .create().show();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<AvailablePurchaseResponse> call, Throwable t) {
                        Snackbar.make(ProductDetailsActivity.this.findViewById(R.id.coordinatorLayout), getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void confirmPurchase(String purchaseId, String cvvValue) {
        CardInfo cardInfo = SharedUtils.getCardInfo(ProductDetailsActivity.this);
        if (cardInfo == null || cardInfo.getCardNumber() == null) {
            Toast.makeText(ProductDetailsActivity.this, "Aún no tienes una tarjeta configurada", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ProductDetailsActivity.this, CardActivity.class));
        } else {
            shopClient.confirmPurchase(SharedUtils.getAuthenticationHeader(ProductDetailsActivity.this),
                    new ConfirmPurchaseRequest(purchaseId, cardInfo.getCardNumber().replaceAll("-", ""), cardInfo.getCardExpirationDate(), cvvValue))
                    .enqueue(new Callback<PurchaseConfirmationResponse>() {
                        @Override
                        public void onResponse(Call<PurchaseConfirmationResponse> call, Response<PurchaseConfirmationResponse> response) {
                            switch (response.code()) {
                                case 200:
                                    if (response.body().isSuccess()) {
                                        Toast.makeText(ProductDetailsActivity.this, getString(R.string.thanks), Toast.LENGTH_LONG).show();
                                        //get url
                                        Intent intent = new Intent(ProductDetailsActivity.this, QrCodeActivity.class);
                                        intent.putExtra(QrCodeActivity.QR_CODE_URL, response.body().getImageUrl());
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                    } else {
                                        //show message
                                        Toast.makeText(ProductDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<PurchaseConfirmationResponse> call, Throwable t) {
                            Snackbar.make(ProductDetailsActivity.this.findViewById(R.id.coordinatorLayout), getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void getProductDetails(String productId) {
        shopClient.getProductDetails(SharedUtils.getAuthenticationHeader(this), productId)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        switch (response.code()) {
                            case 200:
                                //loadProductImage(response);
                                loadProductDetails(response.body());
                                break;
                            case 500:
                                Toast.makeText(ProductDetailsActivity.this, getString(R.string.request_error), Toast.LENGTH_LONG).show();
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailsActivity.this, getString(R.string.request_error), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void loadProductDetails(ProductResponse body) {
        getSupportActionBar().setTitle(body.getName());
        buyButton.setText("Comprar por $" + ((Double)body.getPrice()).intValue());
        ProductDetailsFragment fragment = (ProductDetailsFragment)adapter.getItem(0);
        fragment.setProductDetails(body);
        NutritionalDataFragment nutritionalDataFragment = (NutritionalDataFragment)adapter.getItem(1);
        nutritionalDataFragment.setProductNutritionalData(body.getNutritionalDataUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.productDetailsBuyButton)
    public void buy() {
        CardInfo cardInfo = SharedUtils.getCardInfo(ProductDetailsActivity.this);
        if (cardInfo != null && cardInfo.getCardNumber() != null) {
            showCVVAlertDialog(cardInfo);
        } else {
            startActivity(new Intent(ProductDetailsActivity.this, CardActivity.class));
        }
    }

    private void showCVVAlertDialog(CardInfo cardInfo) {
        View cvvInputView = LayoutInflater.from(ProductDetailsActivity.this).inflate(R.layout.cvv_input, coordinatorLayout, false);
        final TextInputEditText editText = (TextInputEditText) cvvInputView.findViewById(R.id.inputCvv);
        final TextView inputHelp = (TextView)cvvInputView.findViewById(R.id.inputCvvHelp);
        inputHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.cvvHelpMessage), BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction("Aceptar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                TextView snackbartext = (TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackbartext.setMaxLines(4);
                snackbar.show();
            }
        });
        new AlertDialog.Builder(ProductDetailsActivity.this)
                .setTitle("Ingresa el código CVV de tu tarjeta")
                .setView(cvvInputView)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String cvvValue = editText.getText().toString();
                        if (cvvValue.length() != 3) {
                            Snackbar.make(coordinatorLayout, "Código CVV inválido, intenta de nuevo", BaseTransientBottomBar.LENGTH_LONG).show();
                            return;
                        } else {
                            progressDialog = new ProgressDialog(ProductDetailsActivity.this);
                            progressDialog.setMessage(getString(R.string.verifying_product));
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                            getAvailablePurchase(cvvValue);
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }
}

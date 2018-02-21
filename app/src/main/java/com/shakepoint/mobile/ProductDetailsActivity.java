package com.shakepoint.mobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.data.req.ConfirmPurchaseRequest;
import com.shakepoint.mobile.data.res.AvailablePurchaseResponse;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.SimpleQRCodeResponse;
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

    @BindView(R.id.productDetailsImage)
    ImageView productImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.productDetailsProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.productDetailsMainLayout)
    LinearLayout layout;

    @BindView(R.id.productDetailsName)
    TextView productName;

    @BindView(R.id.productDetailsDescription)
    TextView productDescription;

    @BindView(R.id.productDetailsPrice)
    TextView productPrice;

    @BindView(R.id.productDetailsBuyButton)
    Button buyButton;

    private String productId;
    private final ShopClient shopClient = RetroFactory.retrofit().create(ShopClient.class);
    private ProductResponse productResponse;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productId = getIntent().getStringExtra(PRODUCT_ID);
        getProductDetails(productId);
        setTitle(getString(R.string.product_details));
    }

    private void getProductDetails(String productId) {
        shopClient.getProductDetails(SharedUtils.getAuthenticationHeader(this), productId)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        switch (response.code()) {
                            case 200:
                                productResponse = response.body();
                                showProductDetails();
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

    @OnClick(R.id.productDetailsBuyButton)
    public void buy() {
        Toast.makeText(this, getString(R.string.imagine), Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.buy_product))
                .setPositiveButton(getString(R.string.buy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        progressDialog = new ProgressDialog(ProductDetailsActivity.this);
                        progressDialog.setMessage(getString(R.string.verifying_product));
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        shopClient.getAvailablePurchaseForMachine(SharedUtils.getAuthenticationHeader(ProductDetailsActivity.this),
                                productId, SharedUtils.getPreferredMachine(ProductDetailsActivity.this).getMachineId())
                                .enqueue(new Callback<AvailablePurchaseResponse>() {
                                    @Override
                                    public void onResponse(Call<AvailablePurchaseResponse> call, Response<AvailablePurchaseResponse> response) {
                                        switch (response.code()) {
                                            case 200:
                                                if (response.body().getPurchaseId() != null) {
                                                    progressDialog.setMessage(getString(R.string.syncing_server));
                                                    shopClient.confirmPurchase(SharedUtils.getAuthenticationHeader(ProductDetailsActivity.this), new ConfirmPurchaseRequest(response.body().getPurchaseId(), "#SOME REFERENCE"))
                                                            .enqueue(new Callback<SimpleQRCodeResponse>() {
                                                                @Override
                                                                public void onResponse(Call<SimpleQRCodeResponse> call, Response<SimpleQRCodeResponse> response) {
                                                                    switch (response.code()) {
                                                                        case 200:

                                                                            Toast.makeText(ProductDetailsActivity.this, getString(R.string.thanks), Toast.LENGTH_LONG).show();
                                                                            //get url
                                                                            Intent intent = new Intent(ProductDetailsActivity.this, QrCodeActivity.class);
                                                                            intent.putExtra(QrCodeActivity.QR_CODE_URL, response.body().getImageUrl());
                                                                            progressDialog.dismiss();
                                                                            startActivity(intent);
                                                                            break;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<SimpleQRCodeResponse> call, Throwable t) {
                                                                    Snackbar.make(coordinatorLayout, getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
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
                                                                    finish();
                                                                }
                                                            })
                                                            .create().show();
                                                }
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<AvailablePurchaseResponse> call, Throwable t) {
                                        Snackbar.make(coordinatorLayout, getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();

    }

    private void showProductDetails() {
        productName.setText(productResponse.getName());
        Picasso.with(this)
                .load(productResponse.getLogoUrl())
                .into(productImage);
        productDescription.setText(productResponse.getDescription());
        productPrice.setText("$" + productResponse.getPrice());
        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
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
}

package com.shakepoint.mobile.admin;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.admin.ProductFlavorAdapter;
import com.shakepoint.mobile.data.req.admin.CreateFlavorRequest;
import com.shakepoint.mobile.data.res.admin.ProductFlavor;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AdminProductFlavorsActivity extends AppCompatActivity {

    public static final String PARAM_PRODUCT_ID = "productId";
    public static final String PARAM_PRODUCT_IMAGE_URL = "productImageUrl";
    public static final String PARAM_PRODUCT_NAME = "productName";


    private AdminClient adminClient;
    private ProductFlavorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_flavors);
        ButterKnife.bind(this);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);

        loadFlavors();
    }


    private void loadFlavors() {
        adminClient.getFlavors(SharedUtils.getAuthenticationToken(this)).enqueue(new Callback<List<ProductFlavor>>() {
            @Override
            public void onResponse(Call<List<ProductFlavor>> call, Response<List<ProductFlavor>> response) {
                //create adapter
                adapter = new ProductFlavorAdapter(AdminProductFlavorsActivity.this, response.body());
                spinner.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ProductFlavor>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.productFlavorsSelectColor)
    public void selectColorForNewFlavor() {
        new AmbilWarnaDialog(this, android.R.color.white, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String hexColor = SharedUtils.getHexColorFromInteger(color);
                productFlavorColorPreview.setBackgroundColor(color);
                currentColor = hexColor;
            }
        }).show();
    }
}

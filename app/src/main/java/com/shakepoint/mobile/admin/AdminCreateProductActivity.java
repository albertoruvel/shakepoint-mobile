package com.shakepoint.mobile.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateProductActivity extends AppCompatActivity {


    @BindView(R.id.adminCreateProductName)
    TextInputEditText productName;

    @BindView(R.id.adminCreateProductPrice)
    TextInputEditText productPrice;

    @BindView(R.id.adminCreateProductTime)
    TextInputEditText productTime;

    @BindView(R.id.adminCreateProductDescription)
    TextInputEditText productDescription;

    @BindView(R.id.adminCreateProductProductType)
    Spinner productType;

    @BindView(R.id.adminCreateProductImageUrl)
    TextInputEditText url;

    @BindView(R.id.adminCreateProductImage)
    ImageView image;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final MediaType TEXT_PLAIN = MediaType.parse("text/plain");
    private static final MediaType IMAGE = MediaType.parse("image/*");
    private static final Integer PICK_IMAGE = 1234;

    private AdminClient adminClient;
    private File file;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_product);
        ButterKnife.bind(this);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setProductTypes();
    }

    public void setProductTypes() {
        List<String> types = new ArrayList<>();
        types.add("Proteína");
        types.add("Aminoácido");
        types.add("Pre-Entreno");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types);
        productType.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.adminCreateProductSave:
                //create product
                saveProduct();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.adminCreateproductSelectImage)
    public void selectImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(intent, "Información nutrimental");
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uriData = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriData);
                image.setImageBitmap(bitmap);
                //save bitmap to file
                this.file = SharedUtils.saveBitmap(bitmap, this);
            } catch(IOException ex) {

            }
        }
    }

    private void saveProduct() {
        final String name = productName.getText().toString();
        final String price = productPrice.getText().toString();
        final String time = productTime.getText().toString();
        final String description = productDescription.getText().toString();
        final String productTypeValue = (String) productType.getSelectedItem();
        final String imageUrl = url.getText().toString();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creando nuevo producto");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        adminClient.createProduct(
                SharedUtils.getAuthenticationToken(this),
                RequestBody.create(TEXT_PLAIN, name),
                RequestBody.create(TEXT_PLAIN, price),
                RequestBody.create(TEXT_PLAIN, description),
                RequestBody.create(TEXT_PLAIN, imageUrl),
                RequestBody.create(TEXT_PLAIN, time),
                RequestBody.create(TEXT_PLAIN, productTypeValue),
                RequestBody.create(IMAGE, file)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(AdminCreateProductActivity.this, "El producto ha sido creado", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case 401:
                        Toast.makeText(AdminCreateProductActivity.this, "Ha ocurrido un error inesperado, sentimos la molestia", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        break;
                    case 400:
                        Snackbar.make(coordinatorLayout, "Revisa los datos del producto e intenta nuevamente", Snackbar.LENGTH_LONG).show();
                        break;
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }
}

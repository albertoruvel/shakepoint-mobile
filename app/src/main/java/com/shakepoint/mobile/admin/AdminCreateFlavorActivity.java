package com.shakepoint.mobile.admin;

import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.admin.CreateFlavorRequest;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AdminCreateFlavorActivity extends AppCompatActivity {

    @BindView(R.id.createFlavorName)
    TextInputEditText nameText;

    @BindView(R.id.createFlavorColorView)
    View view;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private AdminClient adminClient;
    private String currentHexColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_flavor);
        ButterKnife.bind(this);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);
    }

    @OnClick(R.id.createFlavorColorLayout)
    public void onSelectColor() {
        new AmbilWarnaDialog(this, R.color.colorAccent, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String strColor = String.format("#%06X", 0xFFFFFF & color);
                currentHexColor = strColor;
                view.setBackgroundColor(color);
            }
        });
    }


    public void createFlavor() {
        String flavorName = nameText.getText().toString();
        if (flavorName == null || flavorName.isEmpty()) {
            Snackbar.make(coordinatorLayout, "Debes de proporcionar un nombre", Snackbar.LENGTH_LONG).show();
            return;
        } else if (currentHexColor == null) {
            Snackbar.make(coordinatorLayout, "Debes de proporcionar un color representativo para el sabor", Snackbar.LENGTH_LONG).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creando nuevo sabor");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        CreateFlavorRequest request = new CreateFlavorRequest(flavorName, currentHexColor);
        adminClient.createFlavor(SharedUtils.getAuthenticationToken(this), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();
                        switch (response.code()) {
                            case 200:
                                Toast.makeText(AdminCreateFlavorActivity.this, "Se ha creado el sabor", Toast.LENGTH_LONG).show();
                                finish();
                                break;
                            default:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_flavor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.adminCreateFlavorSave:
                //create flavor
                createFlavor();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.shakepoint.mobile.admin;

import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.admin.NewVendingRequest;
import com.shakepoint.mobile.data.res.admin.Partner;
import com.shakepoint.mobile.data.res.admin.Trainer;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateVendingActivity extends AppCompatActivity {


    private String currentLocation;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.adminCreateVendingName)
    TextInputEditText name;

    @BindView(R.id.adminCreateVendingDesc)
    TextInputEditText description;

    @BindView(R.id.adminCreateVendingTrainers)
    Spinner trainersSpinner;

    @BindView(R.id.adminCreateVendingLocation)
    TextView position;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private AdminClient adminClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_vending);
        ButterKnife.bind(this);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.adminCreateVendingMapFragment);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //set location to Obregon, Sonora
                Double latitude = 27.4682361;
                Double longitude = -110.0026847;
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Arrastrar a posición de la vending").draggable(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        currentLocation = marker.getPosition().latitude + "," + marker.getPosition().longitude;
                        position.setText("Posición " + currentLocation);
                    }
                });
            }
        });
        getPartners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_vending, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.adminCreateVendingSave:
                //create product
                saveVending();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveVending() {
        String nameValue = name.getText().toString();
        String descValue = description.getText().toString();
        Partner trainer = (Partner)trainersSpinner.getSelectedItem();
        if (currentLocation == null){
            Snackbar.make(coordinatorLayout, "Debes de proporcionar una ubicación para la vending", Snackbar.LENGTH_LONG).show();
            return;
        }

        NewVendingRequest request = new NewVendingRequest(nameValue, descValue, trainer.getId(), currentLocation);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creando vending");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        adminClient.createVending(SharedUtils.getAuthenticationToken(this), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();
                        switch(response.code()){
                            case 400:
                                Snackbar.make(coordinatorLayout, "Intenta de nuevo, revisa tus datos", Snackbar.LENGTH_LONG).show();
                                break;
                            case 200:
                                Toast.makeText(AdminCreateVendingActivity.this, "Se ha creado la vending", Toast.LENGTH_LONG).show();
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(coordinatorLayout, "Error desconocido, intenta de nuevo", Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    public void getPartners() {
        adminClient.getPartners(SharedUtils.getAuthenticationToken(this))
                .enqueue(new Callback<List<Partner>>() {
                    @Override
                    public void onResponse(Call<List<Partner>> call, Response<List<Partner>> response) {
                        ArrayAdapter<Partner> adapter = new ArrayAdapter<Partner>(AdminCreateVendingActivity.this,
                                android.R.layout.simple_list_item_1, response.body());
                        trainersSpinner.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Partner>> call, Throwable t) {

                    }
                });
    }
}

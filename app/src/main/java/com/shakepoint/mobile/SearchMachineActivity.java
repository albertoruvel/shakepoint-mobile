package com.shakepoint.mobile;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.adapter.MachinesAdapter;
import com.shakepoint.mobile.data.res.MachineSearchResponse;
import com.shakepoint.mobile.decorator.SpaceDividerItemDecorator;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMachineActivity extends AppCompatActivity {



    @BindView(R.id.searchMachineCard)
    CardView searchMachineCard;

    @BindView(R.id.searchMachineResult)
    CardView searchMachineResult;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.searchMachineMessage)
    TextView searchMachineMessage;

    @BindView(R.id.searchMachineProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.searchMachineName)
    TextView machineName;

    @BindView(R.id.searchMachineDistance)
    TextView machineDistance;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.searchMachineRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.searchMachineMainProgressBar)
    ProgressBar mainProgressBar;

    @BindView(R.id.searchMachineMainMessage)
    TextView mainMessage;

    private static final int SEARCH_BY_NAME = 0;
    private static final int SEARCH_BY_LOCATION = 1;
    private int currentSearch = 0;
    private MachinesAdapter adapter;

    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final ShopClient shopClient = RetroFactory.retrofit().create(ShopClient.class);
    private MachineSearchResponse machineSearchResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_machine);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    try{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 5f, locationListener);
                    }catch(SecurityException ex){
                        Snackbar.make(coordinatorLayout, getString(R.string.location_error), BaseTransientBottomBar.LENGTH_INDEFINITE).show();
                        return;
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    searchMachineMessage.setText(getString(R.string.permissions_error));
                }
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.searchByLocation:
                currentSearch = SEARCH_BY_LOCATION;
                invalidateOptionsMenu();
                recyclerView.setVisibility(View.GONE);
                searchMachineCard.setVisibility(View.VISIBLE);
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                        LOCATION_PERMISSION_REQUEST_CODE);
                mainMessage.setVisibility(View.GONE);
                break;
            case R.id.searchByName:
                currentSearch = SEARCH_BY_NAME;
                invalidateOptionsMenu();
                showSearchByNameView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        switch (currentSearch){
            case SEARCH_BY_LOCATION:
                getMenuInflater().inflate(R.menu.menu_search_machine_location, menu);
                break;
            case SEARCH_BY_NAME:
                getMenuInflater().inflate(R.menu.menu_search_machine, menu);
                SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
                SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setMaxWidth(Integer.MAX_VALUE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query.isEmpty())return false;
                        searchMachines(query);
                        hideKeyboard();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty())return false;
                        searchMachines(newText);
                        return false;
                    }
                });
                break;
        }
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchMachines(String query) {
        shopClient.searchMachinesByName(SharedUtils.getAuthenticationHeader(this), query)
                .enqueue(new Callback<List<MachineSearchResponse>>() {
                    @Override
                    public void onResponse(Call<List<MachineSearchResponse>> call, Response<List<MachineSearchResponse>> response) {
                        switch(response.code()){
                            case 200:
                                if (response.body().isEmpty()){

                                }else{
                                    adapter = new MachinesAdapter(response.body(), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int position = recyclerView.getChildLayoutPosition(view);
                                            SharedUtils.setPreferredMachine(SearchMachineActivity.this, adapter.getMachine(position));
                                            Toast.makeText(SearchMachineActivity.this, String.format(getString(R.string.preferredMachineSet),
                                                    adapter.getMachine(position).getMachineName()), Toast.LENGTH_LONG).show();
                                            setResult(RESULT_OK, null);
                                            finish();
                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    showSearchByNameView();
                                }
                                break;
                            case 500:
                                Snackbar.make(coordinatorLayout, getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MachineSearchResponse>> call, Throwable t) {
                        Snackbar.make(coordinatorLayout, getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.searchMachineSelectButton)
    public void searchMachineSelectButtonClick(){
        SharedUtils.setPreferredMachine(this, machineSearchResponse);
        Toast.makeText(this, String.format(getString(R.string.preferredMachineSet),
                machineSearchResponse.getMachineName()), Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //get location
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            shopClient.searchClosestMachine(SharedUtils.getAuthenticationHeader(SearchMachineActivity.this), latitude, longitude)
                .enqueue(new Callback<MachineSearchResponse>() {
                    @Override
                    public void onResponse(Call<MachineSearchResponse> call, Response<MachineSearchResponse> response) {
                        switch(response.code()){
                            case 200:
                                //show
                                showClosestMachine(response.body());
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<MachineSearchResponse> call, Throwable t) {

                    }
                });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void showClosestMachine(MachineSearchResponse body) {
        if (currentSearch == SEARCH_BY_NAME){
            return;
        }
        machineName.setText(body.getMachineName());
        machineDistance.setText(String.format(getString(R.string.machine_distance), String.valueOf(SharedUtils.round(body.getDistance(), 2))));
        searchMachineCard.setVisibility(View.GONE);
        searchMachineResult.setVisibility(View.VISIBLE);
        machineSearchResponse = body;
    }

    private void showLocationView(){
        //hide recycler view
        recyclerView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.GONE);
        mainMessage.setVisibility(View.GONE);
        searchMachineCard.setVisibility(View.VISIBLE);
    }

    private void showSearchByNameView(){
        searchMachineCard.setVisibility(View.GONE);
        searchMachineResult.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.GONE);
        mainMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}

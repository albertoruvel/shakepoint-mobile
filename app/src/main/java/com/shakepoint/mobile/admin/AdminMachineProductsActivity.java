package com.shakepoint.mobile.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.admin.VendingProductsAdapter;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.admin.GetVendingProducts;
import com.shakepoint.mobile.data.res.admin.Product;
import com.shakepoint.mobile.data.res.admin.VendingProductDetails;
import com.shakepoint.mobile.data.res.admin.VendingProductResponse;
import com.shakepoint.mobile.decorator.HorizontalSpaceDividerDecorator;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMachineProductsActivity extends AppCompatActivity {

    public static final String VENDING_ID = "vendingIdParam";
    public static final String VENDING_NAME = "vendingNameParam";

    private String vendingId;
    private String vendingName;

    @BindView(R.id.adminVendingProductsAll)
    Spinner allProductsSpinner;

    @BindView(R.id.adminVendingProductsSlots)
    Spinner availableSlots;

    @BindView(R.id.adminVendingProductsRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.adminVendingProductsAdd)
    Button button;

    private ProductResponse product;
    private String slotNumber;

    private AdminClient adminClient;
    private VendingProductsAdapter adapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_machine_products);
        ButterKnife.bind(this);
        adminClient = RetroFactory.retrofit(this).create(AdminClient.class);
        this.vendingId = getIntent().getStringExtra(VENDING_ID);
        this.vendingName = getIntent().getStringExtra(VENDING_NAME);

        allProductsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProductResponse product = (ProductResponse) parent.getItemAtPosition(position);
                AdminMachineProductsActivity.this.product = product;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        availableSlots.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String slot = (String)parent.getItemAtPosition(position);
                slotNumber = slot;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new HorizontalSpaceDividerDecorator());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVendingProduct();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(vendingName);

        getAllProducts();
        getVendingProducts();
    }

    public void addVendingProduct() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Agregando producto a la vending");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        adminClient.addVendingProduct(SharedUtils.getAuthenticationToken(this), vendingId, product.getId(), Integer.parseInt(slotNumber))
                .enqueue(new Callback<VendingProductResponse>() {
                    @Override
                    public void onResponse(Call<VendingProductResponse> call, Response<VendingProductResponse> response) {
                        progressDialog.dismiss();
                        getVendingProducts();
                    }

                    @Override
                    public void onFailure(Call<VendingProductResponse> call, Throwable t) {

                    }
                });

    }

    private void getVendingProducts() {
        adminClient.getVendingProducts(SharedUtils.getAuthenticationToken(this), vendingId)
                .enqueue(new Callback<GetVendingProducts>() {
                    @Override
                    public void onResponse(Call<GetVendingProducts> call, Response<GetVendingProducts> response) {
                        adapter = new VendingProductsAdapter(response.body().getProducts(), new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                int position = recyclerView.getChildLayoutPosition(v);
                                final VendingProductDetails details = adapter.getProduct(position);
                                new AlertDialog.Builder(AdminMachineProductsActivity.this)
                                        .setMessage("Deseas eliminar " + details.getName() + " de la vending?")
                                        .setCancelable(false)
                                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        progressDialog = new ProgressDialog(AdminMachineProductsActivity.this);
                                                        progressDialog.setMessage("Eliminando producto de la vending");
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.setIndeterminate(true);
                                                        progressDialog.show();


                                                        //remove product to vending
                                                        adminClient.removeVendingProduct(SharedUtils.getAuthenticationToken(AdminMachineProductsActivity.this), details.getId())
                                                                .enqueue(new Callback<Void>() {
                                                                    @Override
                                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                                        progressDialog.dismiss();
                                                                        getVendingProducts();
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<Void> call, Throwable t) {

                                                                    }
                                                                });

                                                    }
                                                }
                                        )
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create().show();
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        //calculate available slots
                        calculateAvailableSlots(response.body().getProducts());
                    }

                    @Override
                    public void onFailure(Call<GetVendingProducts> call, Throwable t) {

                    }
                });
    }

    private void calculateAvailableSlots(List<VendingProductDetails> products) {

        List<String> slots = getSlotsList();
        for (VendingProductDetails details : products) {
            slots.remove(String.valueOf(details.getSlot()));
        }

        //set available slots to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, slots);
        availableSlots.setAdapter(adapter);
    }

    private List<String> getSlotsList() {
        //list of slots
        List<String> slots = new ArrayList<>();
        //add all slots
        for (int i = 1; i <= 6; i ++) {
            slots.add(String.valueOf(i));
        }
        return slots;
    }

    public void getAllProducts() {
        //get all products
        adminClient.getProducts(SharedUtils.getAuthenticationToken(this))
                .enqueue(new Callback<List<ProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                        switch (response.code()) {
                            case 200:
                                List<ProductResponse> list = new ArrayList<>();
                                for (ProductResponse p : response.body()) {
                                    list.add(p);
                                }
                                ArrayAdapter<ProductResponse> adapter = new ArrayAdapter<ProductResponse>(AdminMachineProductsActivity.this, android.R.layout.simple_list_item_1, list);
                                allProductsSpinner.setAdapter(adapter);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductResponse>> call, Throwable t) {

                    }
                });
    }


}

package com.shakepoint.mobile.fragment.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.ProductsAdapter;
import com.shakepoint.mobile.admin.AdminProductFlavorsActivity;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.admin.Product;
import com.shakepoint.mobile.decorator.SpaceDividerItemDecorator;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment {

    private View currentView;
    private ProductsAdapter adapter;
    private AdminClient adminClient;

    @BindView(R.id.adminProductsRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.adminProductsMessage)
    TextView message;

    @BindView(R.id.adminProductsProgressBar)
    ProgressBar progressBar;

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance() {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_admin_products, container, false);
            adminClient = RetroFactory.retrofit(getActivity()).create(AdminClient.class);
            ButterKnife.bind(this, currentView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
        }
        getProducts();
        return currentView;
    }

    public void getProducts() {
        adminClient.getProducts(SharedUtils.getAuthenticationToken(getActivity()))
                .enqueue(new Callback<List<ProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                        switch (response.code()) {
                            case 200:
                                adapter = new ProductsAdapter(response.body(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int index = recyclerView.getChildAdapterPosition(v);
                                        ProductResponse product = adapter.getProduct(index);
                                        //create dialog
                                        new AlertDialog.Builder(getActivity())
                                                .setItems(new String[]{"Sabores"}, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {
                                                            case 0:
                                                                startActivity(new Intent(getActivity(), AdminProductFlavorsActivity.class));
                                                                break;
                                                        }
                                                    }
                                                }).create().show();
                                    }
                                }, false);
                                recyclerView.setAdapter(adapter);
                                showProducts();
                                break;
                            case 401:
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductResponse>> call, Throwable t) {

                    }
                });
    }

    private void showProducts() {
        message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}

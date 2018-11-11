package com.shakepoint.mobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.ProductDetailsActivity;
import com.shakepoint.mobile.R;
import com.shakepoint.mobile.SearchMachineActivity;
import com.shakepoint.mobile.adapter.ProductsAdapter;
import com.shakepoint.mobile.data.res.MachineSearchResponse;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.ProductResponseWrapper;
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

import static android.app.Activity.RESULT_OK;

public class ProductsFragment extends Fragment {

    public static final int SEARCH_MACHINE_REQUEST_CODE = 121;

    @BindView(R.id.productsMessage)
    TextView message;

    @BindView(R.id.productsProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.productsRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.productsRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.productsCurrentMachineCard)
    CardView currentMachineCard;

    @BindView(R.id.productsMachineName)
    TextView machineName;

    private View currentView;
    private ShopClient shopClient;
    private ProductsAdapter adapter;
    private boolean loading;

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance() {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.productsChangeMachineButton)
    public void changeMachine() {
        Intent intent = new Intent(getActivity(), SearchMachineActivity.class);
        startActivityForResult(intent, SEARCH_MACHINE_REQUEST_CODE, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
         shopClient = RetroFactory.retrofit(getActivity()).create(ShopClient.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_products, container, false);
            ButterKnife.bind(this, currentView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getProducts();
                }
            });
        }
        //get products
        getProducts();

        return currentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getProducts() {
        loading = false;
        MachineSearchResponse preferredMachine = SharedUtils.getPreferredMachine(getActivity());
        //check if there is a preferred machine
        if (preferredMachine != null) {
            //set current machine
            currentMachineCard.setVisibility(View.VISIBLE);
            machineName.setText(SharedUtils.getPreferredMachine(getActivity()).getMachineName());
            //get products from id
            String machineId = SharedUtils.getPreferredMachine(getActivity()).getMachineId();
            shopClient.getProducts(SharedUtils.getAuthenticationHeader(getActivity()), machineId)
                    .enqueue(new Callback<List<ProductResponse>>() {
                        @Override
                        public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                            switch (response.code()) {
                                case 200:
                                    loading = false;
                                    adapter = new ProductsAdapter(response.body(), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int position = recyclerView.getChildLayoutPosition(view);
                                            String productId = adapter.getMachineId(position);
                                            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                            intent.putExtra(ProductDetailsActivity.PRODUCT_ID, productId);
                                            startActivity(intent);
                                        }
                                    }, true);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    message.setVisibility(View.GONE);
                                    break;
                                case 500:
                                    progressBar.setVisibility(View.GONE);
                                    message.setText(getString(R.string.request_error));
                                    message.setVisibility(View.VISIBLE);
                                    break;
                            }
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            message.setText(getString(R.string.request_error));
                            message.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
        } else if (loading) {
            return;
        } else {
            currentMachineCard.setVisibility(View.VISIBLE);
            machineName.setText("Debes de seleccionar una máquina");

            //show message
            message.setText("No hay productos que mostrar");
            progressBar.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_MACHINE_REQUEST_CODE && resultCode == RESULT_OK) {
            //a machine have been set
            getProducts();
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Actualizando productos", Snackbar.LENGTH_LONG).show();
        }
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDetach() {
        recyclerView.setAdapter(null);
        super.onDetach();
    }
}

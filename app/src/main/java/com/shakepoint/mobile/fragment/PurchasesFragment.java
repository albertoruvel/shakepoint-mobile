package com.shakepoint.mobile.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.PurchasesAdapter;
import com.shakepoint.mobile.data.res.PurchaseResponse;
import com.shakepoint.mobile.decorator.SpaceDividerItemDecorator;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasesFragment extends Fragment {

    @BindView(R.id.purchasesRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.purchasesMessage)
    TextView message;

    @BindView(R.id.purchasesProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.purchasesRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private View currentView;
    private ShopClient shopClient;
    private Object purchases;
    private PurchasesAdapter adapter;

    public PurchasesFragment() {
        // Required empty public constructor
    }

    public static PurchasesFragment newInstance() {
        PurchasesFragment fragment = new PurchasesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
            currentView = inflater.inflate(R.layout.fragment_purchases, container, false);
            ButterKnife.bind(this, currentView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getPurchases();
                }
            });
        }

        getPurchases();
        return currentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getPurchases();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getPurchases() {
        shopClient.getUserPurchases(SharedUtils.getAuthenticationHeader(getActivity()))
                .enqueue(new Callback<List<PurchaseResponse>>() {
                    @Override
                    public void onResponse(Call<List<PurchaseResponse>> call, Response<List<PurchaseResponse>> response) {
                        switch(response.code()){
                            case 200:
                                if (response.body().isEmpty()){
                                    message.setText("Aún no tienes compras registradas en Shakepoint, inicia por configurar tu máquina preferida para comprar sus productos");
                                    message.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }else{
                                    adapter = new PurchasesAdapter(response.body(), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    message.setVisibility(View.GONE);
                                }
                                break;
                            default:
                                recyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                message.setText(getString(R.string.request_error));
                                message.setVisibility(View.VISIBLE);
                                break;
                        }
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<List<PurchaseResponse>> call, Throwable t) {
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), BaseTransientBottomBar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        message.setText(getString(R.string.request_error));
                        message.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }
}

package com.shakepoint.mobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.shakepoint.mobile.QrCodeActivity;
import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.QrCodesAdapter;
import com.shakepoint.mobile.data.res.QrCodeResponse;
import com.shakepoint.mobile.decorator.SpaceDividerItemDecorator;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveCodesFragment extends Fragment {

    @BindView(R.id.activeCodesRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.activeCodesRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activeCodesProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.activeCodesMessage)
    TextView message;

    private View currentView;
    private final ShopClient shopClient = RetroFactory.retrofit().create(ShopClient.class);
    private QrCodesAdapter adapter;

    public ActiveCodesFragment() {
        // Required empty public constructor
    }

    public static ActiveCodesFragment newInstance() {
        ActiveCodesFragment fragment = new ActiveCodesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActiveCodes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_active_codes, container, false);
            ButterKnife.bind(this, currentView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActiveCodes();
                }
            });
        }
        getActiveCodes();

        return currentView;
    }

    private void getActiveCodes() {
        if (SharedUtils.getPreferredMachine(getActivity()) != null) {
            shopClient.getActiveCodesByMachine(SharedUtils.getAuthenticationHeader(getActivity()), SharedUtils.getPreferredMachine(getActivity()).getMachineId())
                    .enqueue(new Callback<List<QrCodeResponse>>() {
                        @Override
                        public void onResponse(Call<List<QrCodeResponse>> call, Response<List<QrCodeResponse>> response) {
                            switch (response.code()) {
                                case 200:
                                    //check if there are items
                                    if (response.body().isEmpty()) {
                                        showMessage("No tienes ningún código activo en la máquina Shakepoint actual.\n");
                                    } else {
                                        adapter = new QrCodesAdapter(response.body(), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                int position = recyclerView.getChildLayoutPosition(view);
                                                startQrCodeActivity(position);
                                            }
                                        });
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        message.setVisibility(View.GONE);
                                        refreshLayout.setRefreshing(false);
                                    }
                                    break;
                                case 500:
                                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    message.setText(getString(R.string.request_error));
                                    message.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                    break;
                            }
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<List<QrCodeResponse>> call, Throwable t) {
                            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            message.setText(getString(R.string.request_error));
                            message.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
        } else {
            //no preferred machine is set
            showMessage("No has configurado una máquina preferida, solo puedes ver códigos activos de la máquina que tengas activa.");
        }
    }

    private void startQrCodeActivity(int position) {
        QrCodeResponse qrCode = adapter.getQrCode(position);
        Intent intent = new Intent(getActivity(), QrCodeActivity.class);
        intent.putExtra(QrCodeActivity.QR_CODE_URL, qrCode.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        recyclerView.setAdapter(null);
        super.onDetach();
    }

    private void showMessage(String messageString) {
        progressBar.setVisibility(View.GONE);
        message.setText(messageString);
        message.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }
}

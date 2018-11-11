package com.shakepoint.mobile.fragment.admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.admin.AdminVendingsAdapter;
import com.shakepoint.mobile.admin.AdminMachineProductsActivity;
import com.shakepoint.mobile.data.res.admin.Vending;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendingsFragment extends Fragment {


    @BindView(R.id.adminVendingsProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.adminVendingsRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.adminVendingsMessage)
    TextView message;

    private View currentView;
    private final AdminClient adminClient;
    private AdminVendingsAdapter adapter;

    public VendingsFragment() {
        // Required empty public constructor
        adminClient = RetroFactory.retrofit(getActivity()).create(AdminClient.class);
    }

    public static VendingsFragment newInstance() {
        VendingsFragment fragment = new VendingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
            currentView = inflater.inflate(R.layout.fragment_vendings, container, false);
            ButterKnife.bind(this, currentView);
        }

        //get vendings
        final String authHeader = SharedUtils.getAuthenticationToken(getActivity());
        adminClient.getVendings(authHeader)
                .enqueue(new Callback<List<Vending>>() {
                    @Override
                    public void onResponse(Call<List<Vending>> call, Response<List<Vending>> response) {
                        switch (response.code()) {
                            case 200:
                                adapter = new AdminVendingsAdapter(response.body(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int position = recyclerView.getChildLayoutPosition(v);
                                        String vendingId = adapter.getVendingId(position).getId();
                                        String vendingName = adapter.getVendingId(position).getName();
                                        Intent intent = new Intent(getActivity(), AdminMachineProductsActivity.class);
                                        intent.putExtra(AdminMachineProductsActivity.VENDING_ID, vendingId);
                                        intent.putExtra(AdminMachineProductsActivity.VENDING_NAME, vendingName);
                                        startActivity(intent);
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setHasFixedSize(true);

                                showList();
                                break;
                            case 401:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Vending>> call, Throwable t) {

                    }
                });

        return currentView;
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

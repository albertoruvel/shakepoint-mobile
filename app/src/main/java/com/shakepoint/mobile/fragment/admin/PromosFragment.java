package com.shakepoint.mobile.fragment.admin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.adapter.admin.AdminVendingsAdapter;
import com.shakepoint.mobile.adapter.admin.PromosAdapter;
import com.shakepoint.mobile.data.res.admin.Promotion;
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

public class PromosFragment extends Fragment {

    @BindView(R.id.adminPromosProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.adminPromosRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.adminPromosMessage)
    TextView message;

    private View currentView;
    private AdminClient adminClient;
    private PromosAdapter adapter;

    public PromosFragment() {
        // Required empty public constructor
    }

    public static PromosFragment newInstance() {
        PromosFragment fragment = new PromosFragment();
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
            currentView = inflater.inflate(R.layout.fragment_promos, container, false);
            ButterKnife.bind(this, currentView);
            adminClient = RetroFactory.retrofit(getActivity()).create(AdminClient.class);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new SpaceDividerItemDecorator());
        }

        getPromos();
        return currentView;
    }

    private void getPromos() {
        adminClient.getActivePromos(SharedUtils.getAuthenticationToken(getActivity()))
                .enqueue(new Callback<List<Promotion>>() {
                    @Override
                    public void onResponse(Call<List<Promotion>> call, Response<List<Promotion>> response) {
                        adapter = new PromosAdapter(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = recyclerView.getChildAdapterPosition(v);
                                final Promotion promotion = adapter.getPromo(position);
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Deseas desactivar esta promoción?")
                                        .setPositiveButton("Desactivar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                                progressDialog.setMessage("Desactivando...");
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                adminClient.deactivatePromoCode(SharedUtils.getAuthenticationToken(getActivity()), promotion.getId())
                                                        .enqueue(new Callback<Void>() {
                                                            @Override
                                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                                progressDialog.dismiss();
                                                                switch (response.code()) {
                                                                    case 200:
                                                                        Toast.makeText(getActivity(), "La promoción ha sido desactivada, actualiza los productos", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case 400:
                                                                        Toast.makeText(getActivity(), getString(R.string.request_error), Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case 500:
                                                                        Toast.makeText(getActivity(), getString(R.string.request_error), Toast.LENGTH_LONG).show();
                                                                        break;
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Void> call, Throwable t) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getActivity(), getString(R.string.request_error), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create().show();
                            }
                        }, response.body());
                        recyclerView.setAdapter(adapter);
                        showList();
                    }

                    @Override
                    public void onFailure(Call<List<Promotion>> call, Throwable t) {

                    }
                });
    }

    private void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
    }
}

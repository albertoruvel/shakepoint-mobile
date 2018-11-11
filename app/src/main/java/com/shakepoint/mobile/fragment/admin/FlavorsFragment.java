package com.shakepoint.mobile.fragment.admin;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.admin.CreateFlavorRequest;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlavorsFragment extends Fragment {


    private View currentView;
    private String currentColor;
    private ProgressDialog progressDialog;
    private AdminClient adminClient;

    @BindView(R.id.productFlavorsSelectColor)
    Button colorButton;

    @BindView(R.id.productFlavorColorPreview)
    View productFlavorColorPreview;

    @BindView(R.id.productFlavorName)
    TextInputEditText flavorName;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    public FlavorsFragment() {
        // Required empty public constructor
    }

    public static FlavorsFragment newInstance(String param1, String param2) {
        FlavorsFragment fragment = new FlavorsFragment();
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
            currentView = inflater.inflate(R.layout.fragment_flavors, container, false);
            ButterKnife.bind(this, currentView);
        }
        return currentView;
    }

    @OnClick(R.id.productFlavorSaveButton)
    public void saveNewColor() {
        //get name
        String flavorNameValue = flavorName.getText().toString();
        if (currentColor == null) {
            Snackbar.make(coordinatorLayout, "Es necesario seleccionar un color para el sabor", Snackbar.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creando nuevo sabor para productos");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        CreateFlavorRequest request = new CreateFlavorRequest(flavorNameValue, currentColor);
        adminClient.createFlavor(SharedUtils.getAuthenticationToken(getActivity()), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();
                        switch (response.code()) {
                            case 200:

                                break;
                            case 401:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });


    }

}

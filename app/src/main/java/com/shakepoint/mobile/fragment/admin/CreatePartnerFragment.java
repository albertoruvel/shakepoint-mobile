package com.shakepoint.mobile.fragment.admin;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.CreatePartnerRequest;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePartnerFragment extends Fragment {

    private View currentView;

    @BindView(R.id.adminCreatePartnerName)
    TextInputEditText name;

    @BindView(R.id.adminCreatePartnerEmail)
    TextInputEditText email;

    @BindView(R.id.adminCreatePartnerPassword)
    TextInputEditText password;

    private AdminClient adminClient;
    private ProgressDialog progressDialog;

    @OnClick(R.id.adminCreatePartnerButton)
    public void onClick() {
        String nameValue = name.getText().toString();
        String emailValue = email.getText().toString();
        String pass = password.getText().toString();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creando nuevo socio");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        //create request
        CreatePartnerRequest req = new CreatePartnerRequest(nameValue, emailValue, pass);
        adminClient.createPartner(SharedUtils.getAuthenticationToken(getActivity()), req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch(response.code()) {
                            case 200:
                                Toast.makeText(getActivity(), "Se ha creado el socio correctamente", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
    }

    public CreatePartnerFragment() {
        // Required empty public constructor
    }

    public static CreatePartnerFragment newInstance() {
        CreatePartnerFragment fragment = new CreatePartnerFragment();
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
            currentView = inflater.inflate(R.layout.fragment_create_partner, container, false);
            ButterKnife.bind(this, currentView);
            adminClient = RetroFactory.retrofit(getActivity()).create(AdminClient.class);
        }
        return currentView;
    }

}

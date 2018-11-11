package com.shakepoint.mobile.fragment.admin;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.admin.CreateTrainerRequest;
import com.shakepoint.mobile.data.res.admin.Partner;
import com.shakepoint.mobile.retro.AdminClient;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.util.SharedUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTrainerFragment extends Fragment {


    @BindView(R.id.createTrainerName)
    TextInputEditText name;

    @BindView(R.id.createTrainerEmail)
    TextInputEditText email;

    @BindView(R.id.createTrainerPassword)
    TextInputEditText password;

    @BindView(R.id.createTrainerBirthDate)
    TextInputEditText birthDate;

    @BindView(R.id.createTrainerPartners)
    Spinner spinner;

    private View currentView;
    private AdminClient adminClient;
    ProgressDialog progressDialog;

    @OnClick(R.id.createTrainerSave)
    public void createTrainer() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creando entrenador");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        String birthDateValue = birthDate.getText().toString();
        String nameValue = name.getText().toString();
        String emailValue = email.getText().toString();
        String pass = password.getText().toString();
        Partner partner = (Partner) spinner.getSelectedItem();

        CreateTrainerRequest request = new CreateTrainerRequest(partner.getId(), nameValue, emailValue, pass, birthDateValue);
        adminClient.createTrainer(SharedUtils.getAuthenticationToken(getActivity()), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "El entrenador ha sido creado", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    public CreateTrainerFragment() {
        // Required empty public constructor
    }

    public static CreateTrainerFragment newInstance() {
        CreateTrainerFragment fragment = new CreateTrainerFragment();
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
            currentView = inflater.inflate(R.layout.fragment_create_trainer, container, false);
            ButterKnife.bind(this, currentView);
            adminClient = RetroFactory.retrofit(getActivity()).create(AdminClient.class);
            birthDate.addTextChangedListener(new TextWatcher() {
                private String current = "";
                private String ddmmyyyy = "DDMMYYYY";
                private Calendar cal = Calendar.getInstance();

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        birthDate.setText(current);
                        birthDate.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        showPartners();
        return currentView;
    }

    private void showPartners() {
        adminClient.getPartners(SharedUtils.getAuthenticationToken(getActivity()))
                .enqueue(new Callback<List<Partner>>() {
                    @Override
                    public void onResponse(Call<List<Partner>> call, Response<List<Partner>> response) {
                        ArrayAdapter<Partner> adapter = new ArrayAdapter<Partner>(getActivity(), android.R.layout.simple_list_item_1, response.body());
                        spinner.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Partner>> call, Throwable t) {

                    }
                });
    }

}

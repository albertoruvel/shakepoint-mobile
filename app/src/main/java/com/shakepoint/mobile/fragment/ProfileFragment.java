package com.shakepoint.mobile.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.req.ProfileRequest;
import com.shakepoint.mobile.data.res.ProfileResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.shakepoint.mobile.util.SharedUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profileName)
    TextView name;

    @BindView(R.id.profileAge)
    TextInputEditText age;

    @BindView(R.id.profileWeight)
    TextInputEditText weight;

    @BindView(R.id.profileHeight)
    TextInputEditText height;

    @BindView(R.id.profileUserSince)
    TextView userSince;

    @BindView(R.id.profileTotalPurchases)
    TextView totalPurchases;

    @BindView(R.id.profileContentLayout)
    LinearLayout contentLayout;

    @BindView(R.id.profileProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.profileMessage)
    TextView message;

    @BindView(R.id.profileBirthDate)
    TextInputEditText birthDate;

    @BindView(R.id.profileCmi)
    TextView cmi;

    @BindView(R.id.profileEmail)
    TextView email;

    private View currentView;
    private ProgressDialog progressDialog;
    private static final ShopClient shopClient = RetroFactory.retrofit().create(ShopClient.class);

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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

            currentView = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this, currentView);
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

        getProfile();
        return currentView;
    }

    private void getProfile(){
        shopClient.getUserProfile(SharedUtils.getAuthenticationHeader(getActivity()))
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isAvailableProfile()) {
                                    populateProfile(response.body());
                                } else {
                                    //set only name, email, cmi user since
                                    name.setText(response.body().getUserName());
                                    email.setText(response.body().getEmail());
                                    userSince.setText(response.body().getUserSince());
                                    totalPurchases.setText("" + response.body().getPurchasesTotal());
                                    cmi.setText("" + SharedUtils.round(response.body().getCmi(), 2));
                                    message.setVisibility(View.VISIBLE);
                                    message.setText("Actualmente no cuentas con un perfil, actualiza tus datos. ");
                                }
                                contentLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {

                    }
                });
    }


    private void populateProfile(ProfileResponse body) {
        name.setText(body.getUserName());
        age.setText("" + body.getAge());
        weight.setText("" + body.getWeight());
        height.setText("" + body.getHeight());
        userSince.setText("" + body.getUserSince());
        totalPurchases.setText("" + body.getPurchasesTotal());
        email.setText(body.getEmail());

        cmi.setText("" + SharedUtils.round(body.getCmi(), 2));
        birthDate.setText(body.getBirthday());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        name.setText("");
        email.setText("");
        age.setText("");
        weight.setText("");
        height.setText("");
        birthDate.setText("");
        userSince.setText("");
        totalPurchases.setText("");
        cmi.setText("");
        super.onDetach();
    }

    public void saveProfile() {
        final String ageValue = age.getText().toString();
        final String weightValue = weight.getText().toString();
        final String heightValue = height.getText().toString();
        final String birthDateValue = birthDate.getText().toString();

        //validate
        if (ageValue.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_age), Snackbar.LENGTH_LONG).show();
            return;
        } else if (weightValue.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_weight), Snackbar.LENGTH_LONG).show();
            return;
        } else if (heightValue.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_height), Snackbar.LENGTH_LONG).show();
            return;
        } else {
            //validate birthdate
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(birthDateValue); //this will throw exception when no birthdate is set
                progressDialog = new ProgressDialog(getActivity(), R.style.ColoredProgressDialog);
                progressDialog.setMessage("Guardando perfil");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                //save profile
                shopClient.saveProfile(SharedUtils.getAuthenticationHeader(getActivity()),
                        new ProfileRequest(Integer.parseInt(ageValue), Double.parseDouble(heightValue), Double.parseDouble(weightValue), birthDateValue))
                        .enqueue(new Callback<ProfileResponse>() {
                            @Override
                            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                                switch (response.code()) {
                                    case 200:
                                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Perfil actualizado correctamente", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case 500:
                                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.request_error), Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                                progressDialog.dismiss();
                            }
                        });
            } catch (ParseException ex) {
                //invalid date
                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_birthdate), Snackbar.LENGTH_LONG).show();
                return;
            }
        }
    }
}

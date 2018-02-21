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
    Button birthDate;

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
        }

        //get profile
        shopClient.getUserProfile(SharedUtils.getAuthenticationHeader(getActivity()))
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body().isAvailableProfile()) {
                                    populateProfile(response.body());
                                } else {
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
        return currentView;
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
        }else if (weightValue.isEmpty()){
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_weight), Snackbar.LENGTH_LONG).show();
            return;
        } else if (heightValue.isEmpty()){
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_height), Snackbar.LENGTH_LONG).show();
            return;
        }else {
            //validate birthdate
            try{
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
                                switch(response.code()){
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
            }catch(ParseException ex){
                //invalid date
                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.required_birthdate), Snackbar.LENGTH_LONG).show();
                return;
            }
        }
    }

    @OnClick(R.id.profileBirthDate)
    public void setBirthDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String date = String.format("%d/%d/%d", d, m, y);
                birthDate.setText(date);
                message.setVisibility(View.GONE);
            }
        },
                cal.get(Calendar.YEAR) - 16,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Selecciona tu fecha de nacimiento");
        datePicker.show();
    }
}

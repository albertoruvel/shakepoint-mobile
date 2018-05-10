package com.shakepoint.mobile.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shakepoint.mobile.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class NutritionalDataFragment extends Fragment {

    private View currentView;
    private String productNutritionalData;

    @BindView(R.id.nutritionalDataImage)
    ImageView nutritionalData;

    private PhotoViewAttacher attacher;

    public NutritionalDataFragment() {
        // Required empty public constructor
    }

    public void setProductNutritionalData(String productNutritionalData) {
        this.productNutritionalData = productNutritionalData;
        if (currentView != null){
            loadNutritionalDataImage();
        }
    }

    private void loadNutritionalDataImage() {
        if (productNutritionalData != null){
            Picasso.with(getActivity())
                    .load(productNutritionalData)
                    .into(nutritionalData);
            attacher = new PhotoViewAttacher(nutritionalData, true);
            attacher.setScaleType(ImageView.ScaleType.FIT_XY);
            attacher.update();
        }

    }

    public static NutritionalDataFragment newInstance() {
        NutritionalDataFragment fragment = new NutritionalDataFragment();
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
            currentView = inflater.inflate(R.layout.fragment_nutritional_data, container, false);
            ButterKnife.bind(this, currentView);
        }
        loadNutritionalDataImage();
        return currentView;
    }

}

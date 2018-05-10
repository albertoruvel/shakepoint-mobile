package com.shakepoint.mobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.retro.RetroFactory;
import com.shakepoint.mobile.retro.ShopClient;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 03/23/2018.
 */

public class ProductDetailsFragment extends Fragment {

    public static final String PRODUCT_ID = "productId";

    @BindView(R.id.productDetailsDescription)
    TextView productDescription;

    @BindView(R.id.productDetailsType)
    TextView productType;

    @BindView(R.id.productDetailsImage)
    ImageView image;

    private ProductResponse productResponse;
    private View currentView;
    private ShopClient shopClient;
    private String productId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (currentView == null){
            currentView = inflater.inflate(R.layout.fragment_product_details, container, false);
            ButterKnife.bind(this, currentView);
            shopClient = RetroFactory.retrofit(getActivity()).create(ShopClient.class);
        }
        if (productResponse != null){
            showProductDetails();
        }
        return currentView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get product id
        productId = getArguments().getString(PRODUCT_ID, "");
    }



    public void setProductDetails(ProductResponse response) {
        this.productResponse = response;
        if (currentView != null){
            showProductDetails();
        }
    }

    private void showProductDetails(){
        productDescription.setText(productResponse.getDescription());
        productType.setText(productResponse.getProductType());
        loadProductImage(productResponse.getLogoUrl());

    }

    private void loadProductImage(String logoUrl) {
        Picasso.with(getActivity())
                .load(logoUrl)
                .into(image);
    }

    public static ProductDetailsFragment newInstance(String productId){
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PRODUCT_ID, productId);
        fragment.setArguments(bundle);
        return fragment;
    }

}

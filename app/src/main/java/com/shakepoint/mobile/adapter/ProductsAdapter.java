package com.shakepoint.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductResponse> products;
    private View.OnClickListener onClickListener;
    private String[] orderedCategories = new String[]{"Proteína", "Aminoácido", "Pre-Entreno"};

    public ProductsAdapter(List<ProductResponse> products, View.OnClickListener listener) {
        this.products = products;
        this.onClickListener = listener;
        orderProducts();
    }

    private void orderProducts() {
        List<ProductResponse> orderedProducts = new ArrayList();
        for (String type : orderedCategories) {
            //get all products from type
            for (ProductResponse pr : products) {
                if (pr.getProductType().equals(type)){
                    orderedProducts.add(pr);
                }
            }
        }

        //re-assign ordered products
        products = orderedProducts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        view.setOnClickListener(onClickListener);
        viewHolder = new ProductsAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //product
        ProductResponse response = (ProductResponse) products.get(position);
        ProductsAdapterViewHolder productViewHolder = (ProductsAdapterViewHolder) holder;
        productViewHolder.productName.setText(response.getName());
        productViewHolder.productDescription.setText(response.getProductType());
        try {
            Picasso.with(productViewHolder.productImage.getContext())
                    .load(response.getLogoUrl())
                    .into(productViewHolder.productImage);
        } catch (IllegalArgumentException ex) {
            Picasso.with(productViewHolder.productImage.getContext())
                    .load(R.mipmap.ic_launcher)
                    .into(productViewHolder.productImage);
        }

        productViewHolder.productPrice.setText("$" + ((Double)response.getPrice()).intValue());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductsAdapterViewHolder extends ViewHolder {

        @BindView(R.id.productItemName)
        TextView productName;

        @BindView(R.id.productItemDescription)
        TextView productDescription;

        @BindView(R.id.productItemImage)
        ImageView productImage;

        @BindView(R.id.productItemPrice)
        TextView productPrice;

        public ProductsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String getMachineId(int position) {
        return ((ProductResponse) products.get(position)).getId();
    }

}

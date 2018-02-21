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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsAdapterViewHolder> {

    private List<ProductResponse> products;
    private View.OnClickListener onClickListener;

    public ProductsAdapter(List<ProductResponse> products, View.OnClickListener listener) {
        this.products = products;
        this.onClickListener = listener;
    }

    @Override
    public ProductsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        view.setOnClickListener(onClickListener);
        return new ProductsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsAdapterViewHolder holder, int position) {
        ProductResponse response = products.get(position);
        holder.productName.setText(response.getName());
        holder.productDescription.setText(response.getDescription());
        try{
            Picasso.with(holder.productImage.getContext())
                    .load(response.getLogoUrl())
                    .into(holder.productImage);
        }catch(IllegalArgumentException ex){
            Picasso.with(holder.productImage.getContext())
                    .load(R.mipmap.ic_launcher)
                    .into(holder.productImage);
        }

        holder.productPrice.setText("$" + response.getPrice());
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

    public String getMachineId(int position){
        return products.get(position).getId();
    }

    public interface OnItemClicked {
        public void onItemClicked(String machineId);
    }
}

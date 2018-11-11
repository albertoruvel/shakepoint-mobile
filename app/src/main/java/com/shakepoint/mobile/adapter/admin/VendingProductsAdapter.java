package com.shakepoint.mobile.adapter.admin;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.admin.VendingProductDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendingProductsAdapter  extends RecyclerView.Adapter<VendingProductsAdapter.VendingProductViewHolder>{


    private List<VendingProductDetails> list;
    private View.OnClickListener removeClickListener;

    public VendingProductsAdapter(List<VendingProductDetails> list, View.OnClickListener removeClickListener) {
        this.list = list;
        this.removeClickListener = removeClickListener;
    }

    @Override
    public VendingProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_vending_product_item, parent, false);
        view.setOnClickListener(removeClickListener);
        return new VendingProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendingProductViewHolder holder, int position) {
        Picasso.with(holder.image.getContext())
                .load(Uri.parse(list.get(position).getImageUrl()))
        .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public VendingProductDetails getProduct(int position) {
        return list.get(position);
    }

    static class VendingProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adminVendingProductsImage)
        ImageView image;

        public VendingProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

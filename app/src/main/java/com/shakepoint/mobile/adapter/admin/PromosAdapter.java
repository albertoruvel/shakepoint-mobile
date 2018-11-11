package com.shakepoint.mobile.adapter.admin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.admin.Promotion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PromosAdapter  extends RecyclerView.Adapter<PromosAdapter.PromoViewHolder>{

    private View.OnClickListener listener;
    private List<Promotion> promos;

    public PromosAdapter(View.OnClickListener listener, List<Promotion> promos) {
        this.listener = listener;
        this.promos = promos;
    }

    public Promotion getPromo(int position){
        return promos.get(position);
    }

    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_item, parent, false);
        view.setOnClickListener(listener);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromoViewHolder holder, int position) {
        Promotion promo = promos.get(position);
        holder.code.setText(promo.getCode());
        holder.expiration.setText("Expira " + promo.getExpirationDate());
        holder.type.setText(promo.getType());
        if (promo.getProduct() != null) {
            holder.extraData.setText(promo.getProduct().getName());
        } else if (promo.getTrainer() != null){
            holder.extraData.setText("Entrenador " + promo.getTrainer().getName());
        } else {
            holder.extraData.setVisibility(View.GONE);
        }
        holder.description.setText(promo.getDescription());
    }

    @Override
    public int getItemCount() {
        return promos.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.promoItemCode)
        TextView code;

        @BindView(R.id.promoItemExpiration)
        TextView expiration;

        @BindView(R.id.promoItemExtra)
        TextView extraData;

        @BindView(R.id.promoItemType)
        TextView type;

        @BindView(R.id.promoItemDescription)
        TextView description;

        public PromoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

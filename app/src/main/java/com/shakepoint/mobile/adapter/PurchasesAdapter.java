package com.shakepoint.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.PurchaseResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder>{

    private List<PurchaseResponse> purchases;
    private View.OnClickListener clickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PurchasesAdapter(List<PurchaseResponse> purchases, View.OnClickListener clickListener) {
        this.purchases = purchases;
        this.clickListener = clickListener;
    }

    @Override
    public PurchaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        view.setOnClickListener(clickListener);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseViewHolder holder, int position) {
        Date date = new Date();
        PurchaseResponse response = purchases.get(position);
        holder.machineName.setText(response.getMachineName());
        holder.productName.setText(response.getProductName());
        try{
            Date purchaseDate = dateFormat.parse(response.getPurchaseDate());
            long diff = date.getTime() - purchaseDate.getTime();
            if (diff < 180000){
                //purchase has less than 3 minutes
                holder.purchaseDate.setText("Hace un momento");
            } else if (diff > 180000 && diff < (60000 * 60)){
                //less than an hour
                holder.purchaseDate.setText(String.format("Hace %d minutos", (diff / 60000)));
            } else if (diff > (60000 * 60) && diff < (60000 * 60) * 24){
                //less than a day
                holder.purchaseDate.setText(String.format("Hace %d horas", (diff / 3600000)));
            } else {
                holder.purchaseDate.setText(dateFormat.format(purchaseDate));
            }
        }catch(ParseException ex){

        }

        holder.purchaseTotal.setText("$" + response.getTotal());
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.purchaseItemDate)
        TextView purchaseDate;

        @BindView(R.id.purchaseItemProductName)
        TextView productName;

        @BindView(R.id.purchaseItemMachineName)
        TextView machineName;

        @BindView(R.id.purchaseItemPrice)
        TextView purchaseTotal;


        public PurchaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

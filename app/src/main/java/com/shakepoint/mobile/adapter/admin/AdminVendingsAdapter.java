package com.shakepoint.mobile.adapter.admin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.admin.Vending;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminVendingsAdapter extends RecyclerView.Adapter<AdminVendingsAdapter.VendingViewHolder> {

    private List<Vending> vendings;
    private View.OnClickListener listener;

    public AdminVendingsAdapter(List<Vending> list, View.OnClickListener listener) {
        this.vendings = list;
        this.listener = listener;
    }


    @Override
    public VendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_vending, parent, false);
        view.setOnClickListener(listener);
        return new VendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendingViewHolder holder, int position) {
        if (vendings.get(position).isActive()) {
            holder.activeView.setBackgroundColor(holder.activeView.getContext().getColor(R.color.colorAccent));
        } else {
            holder.activeView.setBackgroundColor(holder.activeView.getContext().getColor(android.R.color.black));
        }
        holder.vendingName.setText(vendings.get(position).getName());
        holder.partnerName.setText(vendings.get(position).getPartnerName());
        holder.port.setText("" + vendings.get(position).getWorkingPort());
        holder.id.setText(vendings.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return vendings.size();
    }

    public Vending getVendingId(int position) {
        return vendings.get(position);
    }

    static class VendingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adminVendingActive)
        View activeView;

        @BindView(R.id.adminVendingName)
        TextView vendingName;

        @BindView(R.id.adminVendingPartnerName)
        TextView partnerName;

        @BindView(R.id.adminVendingPort)
        TextView port;

        @BindView(R.id.adminVendingId)
        TextView id;

        public VendingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

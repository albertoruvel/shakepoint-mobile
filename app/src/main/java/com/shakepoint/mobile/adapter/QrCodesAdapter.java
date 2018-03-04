package com.shakepoint.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.QrCodeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 02/16/2018.
 */

public class QrCodesAdapter extends RecyclerView.Adapter<QrCodesAdapter.QrCodeViewHolder>{

    private List<QrCodeResponse> qrCodes;
    private View.OnClickListener onClickListener;

    public QrCodesAdapter(List<QrCodeResponse> qrCodes, View.OnClickListener onClickListener) {
        this.qrCodes = qrCodes;
        this.onClickListener = onClickListener;
    }

    @Override
    public QrCodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_code_item, parent, false);
        view.setOnClickListener(onClickListener);

        return new QrCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QrCodeViewHolder holder, int position) {
        QrCodeResponse response = qrCodes.get(position);
        final String validMessage = String.format(holder.machineName.getContext().getString(R.string.valid_only), response.getMachineName());
        holder.machineName.setText(validMessage);
        holder.productName.setText(response.getProductName());
        final String purchaseDateMessage = String.format(holder.purchaseDate.getContext().getString(R.string.purchase_date), response.getPurchaseDate());
        holder.purchaseDate.setText(purchaseDateMessage);
        Picasso.with(holder.qrCode.getContext())
                .load(R.drawable.qrcodeexample)
                .into(holder.qrCode);
    }

    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    public QrCodeResponse getQrCode(int position) {
        return qrCodes.get(position);
    }

    static class QrCodeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.qrCodeItemImage)
        ImageView qrCode;

        @BindView(R.id.qrCodeItemProductName)
        TextView productName;

        @BindView(R.id.qrCodeItemMachineData)
        TextView machineName;

        @BindView(R.id.qrCodeItemDate)
        TextView purchaseDate;

        public QrCodeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

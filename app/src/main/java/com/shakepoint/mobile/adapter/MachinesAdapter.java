package com.shakepoint.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.MachineSearchResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 02/15/2018.
 */

public class MachinesAdapter extends RecyclerView.Adapter<MachinesAdapter.MachinesViewHolder>{

    private List<MachineSearchResponse> machines;
    private View.OnClickListener listener;

    public MachinesAdapter(List<MachineSearchResponse> machines, View.OnClickListener listener) {
        this.machines = machines;
        this.listener = listener;
    }

    @Override
    public MachinesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.machine_item, parent, false);
        view.setOnClickListener(listener);
        return new MachinesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MachinesViewHolder holder, int position) {
        MachineSearchResponse response = machines.get(position);
        holder.name.setText(response.getMachineName());
    }

    @Override
    public int getItemCount() {
        return machines.size();
    }

    public MachineSearchResponse getMachine(int position) {
        return machines.get(position);
    }

    static class MachinesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.machineItemName)
        TextView name;

        public MachinesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

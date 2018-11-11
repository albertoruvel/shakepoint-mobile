package com.shakepoint.mobile.adapter.admin;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shakepoint.mobile.R;
import com.shakepoint.mobile.data.res.admin.ProductFlavor;

import java.util.List;

public class ProductFlavorAdapter extends ArrayAdapter<ProductFlavor> {

    private List<ProductFlavor> list;

    public ProductFlavorAdapter(@NonNull Context context, List<ProductFlavor> flavors) {
        super(context, R.layout.flavor_item);
        this.list = flavors;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ProductFlavor getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flavor_item, parent, false);
        View colorView = view.findViewById(R.id.flavorItemColor);
        TextView flavorName = view.findViewById(R.id.flavorItemName);

        colorView.setBackgroundColor(Color.parseColor(list.get(position).getHexColor()));
        flavorName.setText(list.get(position).getName());
        return view;
    }
}

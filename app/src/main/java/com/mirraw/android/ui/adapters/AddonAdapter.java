package com.mirraw.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.models.productDetail.AddonTypeValue;

import java.util.List;

/**
 * Created by Gaurav on 8/26/2015.
 */
public class AddonAdapter extends BaseAdapter {

    //private List<AddonType> mAddonTypes;
    private List<AddonTypeValue> mAddonTypeValues;
    private String strSymbol;

    public AddonAdapter(List<AddonTypeValue> addonTypeValues, String strSymbol) {
        this.mAddonTypeValues = addonTypeValues;
        this.strSymbol = strSymbol;
    }

    @Override
    public int getCount() {
        return mAddonTypeValues.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddonTypeValue addonType = mAddonTypeValues.get(position);

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addon_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.addonTextView = (TextView) view.findViewById(R.id.addontextView);
            viewHolder.priceTextView = (TextView) view.findViewById(R.id.pricetext);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = addonType.getPName();
        String price = strSymbol + " " + String.valueOf(addonType.getPrice());


        viewHolder.addonTextView.setText(name);
        viewHolder.priceTextView.setText(price);


        return view;
    }


    public static class ViewHolder {
        public TextView addonTextView;
        public TextView priceTextView;
    }


}
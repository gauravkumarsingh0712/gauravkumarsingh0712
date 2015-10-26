package com.mirraw.android.ui.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.models.productDetail.AddonOptionType;
import com.mirraw.android.models.productDetail.AddonOptionValue;
import com.mirraw.android.models.productDetail.AddonTypeValue;

import java.util.List;

/**
 * Created by abc on 8/26/2015.
 */
public class AddonChildAdapter extends BaseAdapter {

    private List<AddonOptionValue> mAddonOptionValues;


    public AddonChildAdapter(List<AddonOptionValue> addonOptionValues) {
        mAddonOptionValues = addonOptionValues;
    }

    @Override
    public int getCount() {
        return mAddonOptionValues.size();
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
        AddonOptionValue addonType = mAddonOptionValues.get(position);

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stitching_spiner_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.addonTextView = (TextView) view.findViewById(R.id.addtextView);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = addonType.getPName();

        viewHolder.addonTextView.setText(name);
        //viewHolder.addonTextView.setOnClickListener(this);

        return view;
    }


    public static class ViewHolder {
        public TextView addonTextView;
    }


}
package com.mirraw.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.models.address.Country;

import java.util.List;

/**
 * Created by Nimesh Luhana on 21-07-2015.
 */
public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {
    public interface CountryClickListener {
        void onCountryClicked(View v, int position);

    }

    List<Country> mCountry;

    static CountryClickListener mCountryClickListener;

    public CountriesAdapter(CountryClickListener countryClickListener, List<Country> country) {
        mCountryClickListener = countryClickListener;
        mCountry = country;
    }


    @Override
    public CountriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_country, parent, false);



        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CountriesAdapter.ViewHolder holder, int position) {

        holder.countryName.setText(mCountry.get(position).getName());
        holder.countryName.setTag(mCountry.get(position).getId());

    }

    @Override
    public int getItemCount() {
        return mCountry.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements RippleView.OnRippleCompleteListener {
        TextView countryName;
        RippleView countryTextRippleContainer;


        public ViewHolder(View view) {
            super(view);
            countryName = (TextView) view.findViewById(R.id.countryName);
            countryTextRippleContainer = (RippleView) view.findViewById(R.id.countryTextRippleContainer);
            countryTextRippleContainer.setOnRippleCompleteListener(this);
            //view.findViewById(R.id.countryName);

        }


        @Override
        public void onComplete(RippleView rippleView) {
            mCountryClickListener.onCountryClicked(rippleView, (Integer) rippleView.findViewById(R.id.countryName).getTag());
        }
    }


}

package com.mirraw.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.models.address.Country;
import com.mirraw.android.models.address.State;

import java.util.List;

/**
 * Created by Nimesh Luhana on 21-07-2015.
 */
public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.ViewHolder> {
    public interface StateClickListener {
        void onStateClicked(View v, int position);

    }

    List<State> mState;

    static StateClickListener mStateClickListener;

    public StatesAdapter(StateClickListener stateClickListener, List<State> state) {
        mStateClickListener = stateClickListener;
        mState = state;
    }


    @Override
    public StatesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_state, parent, false);



        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StatesAdapter.ViewHolder holder, int position) {

        holder.stateName.setText(mState.get(position).getName());
        holder.stateName.setTag(mState.get(position).getId());

    }

    @Override
    public int getItemCount() {
        return mState.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements RippleView.OnRippleCompleteListener {
        TextView stateName;
        RippleView stateTextRippleContainer;

        public ViewHolder(View view) {
            super(view);
            stateName = (TextView) view.findViewById(R.id.stateName);
            //view.findViewById(R.id.countryName);
            stateTextRippleContainer = (RippleView) view.findViewById(R.id.stateTextRippleContainer);
            stateTextRippleContainer.setOnRippleCompleteListener(this);

        }

        @Override
        public void onComplete(RippleView rippleView) {

            mStateClickListener.onStateClicked(rippleView, (Integer) rippleView.findViewById(R.id.stateName).getTag());
        }
    }


}

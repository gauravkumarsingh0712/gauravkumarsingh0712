package com.mirraw.android.ui.adapters.filters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.models.searchResults.List_;

/**
 * Created by vihaan on 20/7/15.
 */
public class IdFilterAdapter extends RecyclerView.Adapter<IdFilterAdapter.ViewHolder>{

    private  IdFilterListener mIdFilterListener;
    private java.util.List<List_> mList;
    private Context context;

    public interface IdFilterListener
    {
        public void onIdFilterClicked(View view, int position);
    }


    public IdFilterAdapter(IdFilterListener listener, java.util.List<List_> list,Context context)
    {
        mIdFilterListener = listener;
        mList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_id_filter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        List_ list = mList.get(position);
        if(list.getSelected())
        {
            holder.idFilterCheckBox.setChecked(true);

            mIdFilterListener.onIdFilterClicked(holder.getView(), position);
        }
        else
        {
            holder.idFilterCheckBox.setChecked(false);
        }
        holder.idFilterTextView.setText(list.getName());
        holder.idFilterCountTextView.setText("("+ list.getCount() +")");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView idFilterTextView, idFilterCountTextView;
        public CheckBox idFilterCheckBox;

        private View mView;
        public ViewHolder(View view) {
            super(view);
            idFilterCheckBox = (CheckBox) view.findViewById(R.id.idFilterCheckBox);
            idFilterTextView = (TextView) view.findViewById(R.id.idFilterTextView);
            idFilterCountTextView = (TextView) view.findViewById(R.id.idFilterCountTextView);
            view.setOnClickListener(this);
            mView = view;

        }

        public View getView()
        {
            return mView;
        }
        @Override
        public void onClick(View v) {
            int position = getPosition();
            List_ list =  mList.get(position);

            if(list.getSelected())
            {
                list.setSelected(false);
                idFilterCheckBox.setChecked(false);
            }
            else
            {
                list.setSelected(true);
                idFilterCheckBox.setChecked(true);
            }

            mIdFilterListener.onIdFilterClicked(v, position);
        }
    }
}

package com.mirraw.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.models.menus.MenuColumn;
import com.mirraw.android.models.menus.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by varun on 10/10/15.
 */
public class SingleCategoryMenuAdapter extends RecyclerView.Adapter<SingleCategoryMenuAdapter.ViewHolder> {


    public interface SingleCategoryMenuClickListener {
        public void onMenuItemClicked(String strTitle, String strKey, String strValue);
    }

    List<MenuItem> menuItems = new ArrayList<>();
    private static SingleCategoryMenuClickListener sSingleCategoryMenuClickListener;

    public void setColumns(MenuColumn columns) {
        menuItems = columns.getMenuItems();
    }

    public SingleCategoryMenuAdapter(MenuColumn column, SingleCategoryMenuClickListener singleCategoryMenuClickListener) {
        menuItems = column.getMenuItems();
        sSingleCategoryMenuClickListener = singleCategoryMenuClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.strTitle = menuItems.get(position).getTitle();
        holder.strKey = menuItems.get(position).getKey();
        holder.strValue = menuItems.get(position).getValue();
        holder.mTextView.setText(menuItems.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public String strTitle, strKey, strValue;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textTitle);
            strTitle = "";
            strKey = "";
            strValue = "";
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sSingleCategoryMenuClickListener.onMenuItemClicked(strTitle, strKey, strValue);
        }
    }
}

package com.mirraw.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.models.menus.Menu;

import java.util.List;

public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.ViewHolder> {

    private List<Menu> mMenus;
    private static MenuClickListener sMenuClickListener;

    public interface MenuClickListener
    {
        public void onMenuClicked(View view, int position);
    }

    public MenusAdapter(MenuClickListener clickListener, List<Menu> blocks) {

        mMenus = blocks;
        sMenuClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Menu menu = mMenus.get(position);
        String menuName = menu.getTitle();
        holder.menuTextView.setText(menuName);

    }

    @Override
    public int getItemCount() {
        return mMenus.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView arrowImageView;
        public TextView menuTextView;

        public ViewHolder(View view) {
            super(view);
            arrowImageView = (ImageView) view.findViewById(R.id.arrowImageView);
            menuTextView = (TextView) view.findViewById(R.id.menuTextView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            sMenuClickListener.onMenuClicked(v, position);
        }
    }
}

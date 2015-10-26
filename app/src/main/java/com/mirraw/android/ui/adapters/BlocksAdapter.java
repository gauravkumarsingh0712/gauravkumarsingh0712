package com.mirraw.android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.Block;
import com.mirraw.android.sharedresources.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by vihaan on 30/6/15.
 */
public class BlocksAdapter extends RecyclerView.Adapter<BlocksAdapter.ViewHolder> {

    private List<Block> mBlocks;
    private static BlockClickListener sBlockClickListener;


    public interface BlockClickListener {
        public void onBlockClicked(View view, int position);
    }

    public BlocksAdapter(List<Block> blocks, BlockClickListener blockClickListener) {
        mBlocks = blocks;
        sBlockClickListener = blockClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_block, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Block block = mBlocks.get(position);
        String photoUrl = block.getPhoto().getSizes().getMain();
        String blockName = block.getName();

        Context context = holder.blockImageView.getContext();
        /*Picasso.with(context).load(Utils.addHttpSchemeIfMissing(photoUrl)).placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image).into(holder.blockImageView);*/

        String strPhotoUrl = Utils.addHttpSchemeIfMissing(photoUrl);
        ImageLoader.getInstance()
                .displayImage(strPhotoUrl, holder.blockImageView, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Logger.d("SIZE", "Bitmap size: " + loadedImage.getRowBytes() * loadedImage.getHeight());
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
        holder.blockTextView.setText(blockName);

    }

    @Override
    public int getItemCount() {
        return mBlocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RippleView.OnRippleCompleteListener {
        public ImageView blockImageView;
        public TextView blockTextView;
        RippleView rippleView;

        public ViewHolder(View view) {
            super(view);
            blockImageView = (ImageView) view.findViewById(R.id.blockImageView);
            blockTextView = (TextView) view.findViewById(R.id.blocktextView);
            rippleView = (RippleView) view.findViewById(R.id.rippleView);
            rippleView.setOnClickListener(this);
            rippleView.setOnRippleCompleteListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onComplete(RippleView rippleView) {
            int position = getPosition();
            sBlockClickListener.onBlockClicked(rippleView, position);
        }
    }
}

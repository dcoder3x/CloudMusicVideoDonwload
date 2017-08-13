package com.aaa3.show_result;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.aaa3.R;
import com.aaa3.model.FileModel;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<FileModel> mList;

    public interface Listener {
        void playVideo(String fileDir);
    }

    RVAdapter(Context context, List<FileModel> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false);
        if (viewType == 1) {
            v = LayoutInflater.from(mContext).inflate(R.layout.guide, parent, false);
            return new MyHolder2(v);
        }
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof  MyHolder) {
            final FileModel model = mList.get(position);
            MyHolder myHolder = (MyHolder) holder;
            myHolder.mTitle.setText(model.filename);
            myHolder.mDesc.setText(model.desc);
            myHolder.mPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof Listener) {
                        Listener listener = (Listener) mContext;
                        listener.playVideo(model.dir);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        }
        return mList.size();
    }

    /**
     * remove item and return item model
     */
    FileModel removeItem(int position) {
        FileModel model = mList.get(position);
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
        return model;
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mDesc;
        ImageView mPlay;

        MyHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title_tv);
            mDesc = (TextView) itemView.findViewById(R.id.desc_tv);
            mPlay = (ImageView) itemView.findViewById(R.id.play_btn);
        }
    }

    static class MyHolder2 extends RecyclerView.ViewHolder {

        public MyHolder2(View itemView) {
            super(itemView);
        }
    }
}

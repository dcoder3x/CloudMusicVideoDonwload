package com.aaa3.show_result;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.aaa3.R;
import com.aaa3.model.FileModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DownloadFragment extends Fragment {


    private DownloadManager mDownloadManager;

    private RecyclerView mRecyclerView;

    private RVAdapter mAdapter;

    private List<FileModel> mList;

    private Paint mPaint;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        mAdapter = new RVAdapter(getActivity(), mList);
        mPaint = new Paint();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv);

        loadRecord();
        initView();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDownloadManager = null;
    }




    public void loadRecord(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            FileModel model = new FileModel();
            if (cursor.getLong(cursor.getColumnIndex("_id")) == id)
                model.id = id;
            model.dir = cursor.getString(cursor.getColumnIndex("local_filename"));
            model.filename = cursor.getString(cursor.getColumnIndex("title"));
            model.desc = cursor.getString(cursor.getColumnIndex("description"));
            mList.add(model);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void loadRecord() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cursor = mDownloadManager.query(query);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FileModel model = new FileModel();
            model.id = cursor.getLong(cursor.getColumnIndex("_id"));
            model.dir = cursor.getString(cursor.getColumnIndex("local_filename"));
            model.filename = cursor.getString(cursor.getColumnIndex("title"));
            model.desc = cursor.getString(cursor.getColumnIndex("description"));
            mList.add(model);
            cursor.moveToNext();
        }
    }

    private void deleteRecord(final int position) {
        FileModel model = mAdapter.removeItem(position);
        final long id = model.id;


        new AlertDialog.Builder(getActivity()).setTitle("删除下载记录")
                .setCancelable(false)
                .setMessage("同时将删除已下载的文件！")
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO how to refresh recyclerview while item removed,but adapter not changed ?
                    }
                })
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDownloadManager.remove(id);
                    }
                })
                .create()
                .show();
    }

    private void initView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.SimpleCallback mItemCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof RVAdapter.MyHolder2) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteRecord(position);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    int height = itemView.getBottom() - itemView.getTop();
                    int width = height / 3;

                    if (dX > 0) {
                        mPaint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, mPaint);

                        Rect icon_dest = new Rect(itemView.getLeft() + width, itemView.getTop() + width, itemView.getLeft() + 2 * width, itemView.getBottom() - width);
                        Drawable drawable;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            drawable = getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp, null);
                        } else {
                            drawable = getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp);
                        }
                        drawable.setBounds(icon_dest);
                        drawable.draw(c);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}

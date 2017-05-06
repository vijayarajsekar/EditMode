package com.southernbox.editlayout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.southernbox.editlayout.R;
import com.southernbox.editlayout.widget.EditLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SouthernBox on 2016/10/25 0025.
 * 主页面适配器
 */

public class EditAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mList;

    public static boolean isEdit;  //是否处于编辑状态
    private List<EditLayout> allItems = new ArrayList<>();
    private EditLayout mRightOpenItem;  //向右展开的删除项，只会存在一项

    public EditAdapter(Context context, List<String> List) {
        this.mContext = context;
        this.mList = List;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final EditLayout layout = viewHolder.editLayout;

        if (!allItems.contains(layout)) {
            allItems.add(layout);
        }

        viewHolder.tvName.setText(mList.get(position));

        viewHolder.vPreDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mRightOpenItem != null) {
                            mRightOpenItem.openLeft();
                        }
                }
                return false;
            }
        });

        viewHolder.vPreDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.openRight();
            }
        });

        viewHolder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                mList.remove(position);
                mRightOpenItem = null;
                notifyItemRemoved(position);
                if (position != mList.size()) {
                    notifyItemRangeChanged(position, mList.size() - position);
                }
            }
        });

        layout.setOnDragStateChangeListener(new EditLayout.OnStateChangeListener() {

            @Override
            public void onLeftOpen(EditLayout layout) {
                if (mRightOpenItem == layout) {
                    mRightOpenItem = null;
                }
            }

            @Override
            public void onRightOpen(EditLayout layout) {
                if (mRightOpenItem != layout) {
                    mRightOpenItem = layout;
                }
            }

            @Override
            public void onClose(EditLayout layout) {
                if (mRightOpenItem == layout) {
                    mRightOpenItem = null;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 设置编辑状态
     *
     * @param isEdit 是否为编辑状态
     */
    public void setEdit(boolean isEdit) {
        EditAdapter.isEdit = isEdit;
    }

    /**
     * 关闭所有 item
     */
    public void closeAll() {
        for (EditLayout layout : allItems) {
            layout.close();
        }
    }

    /**
     * 将所有 item 向左展开
     */
    public void openLeftAll() {
        for (EditLayout layout : allItems) {
            layout.openLeft();
        }
    }

    /**
     * 获取向右展开的 item
     *
     * @return 向右展开的 item
     */
    public EditLayout getRightOpenItem() {
        return mRightOpenItem;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        EditLayout editLayout;
        TextView tvName;
        View vPreDelete;
        View vDelete;

        ViewHolder(View itemView) {
            super(itemView);
            editLayout = (EditLayout) itemView.findViewById(R.id.edit_layout);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            vPreDelete = itemView.findViewById(R.id.fl_pre_delete);
            vDelete = itemView.findViewById(R.id.fl_delete);
        }
    }
}

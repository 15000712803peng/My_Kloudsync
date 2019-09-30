package com.kloudsync.techexcel.adapter.expand;

import android.view.View;

/**
 * describe：RecyclerViewListener
 */

public interface OnRecyclerViewListener {

    /**
     * 单击事件
     */
    interface OnItemClickListener {
        /**
         * position 当前在列表中的position
         */
        void onGroupItemClick(int position, int groupPosition, View view);

        void onChildItemClick(int position, int groupPosition, int childPosition, View view);
    }

    /**
     * 双击事件
     */
    interface OnItemLongClickListener {
        void onGroupItemLongClick(int position, int groupPosition, View view);

        void onChildItemLongClick(int position, int groupPosition, int childPosition, View view);
    }


}
package com.kloudsync.techexcel.viewtree;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.viewtree.mode.TreeNode;


public class SimpleViewHolder extends TreeNode.BaseNodeViewHolder<Object> {

    public SimpleViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        final TextView tv = new TextView(context);
        tv.setText(String.valueOf(value));
        return tv;
    }

    @Override
    public void highlightText(TreeNode node) {

    }

    @Override
    public void toggle(boolean active) {

    }
}
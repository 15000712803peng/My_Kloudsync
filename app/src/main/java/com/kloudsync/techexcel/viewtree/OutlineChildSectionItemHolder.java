package com.kloudsync.techexcel.viewtree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChildSectionItem;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;

public class OutlineChildSectionItemHolder extends TreeNode.BaseNodeViewHolder<OutlineChildSectionItem>{


    public OutlineChildSectionItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, OutlineChildSectionItem value) {
        return null;
    }
}
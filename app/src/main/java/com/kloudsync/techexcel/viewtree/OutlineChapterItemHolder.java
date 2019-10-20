package com.kloudsync.techexcel.viewtree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChapterItem;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;

public class OutlineChapterItemHolder extends TreeNode.BaseNodeViewHolder<OutlineChapterItem>{

    TextView titleText;
    ImageView iconImage;
    private OutlineChapterItem chapterItem;


    public OutlineChapterItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, OutlineChapterItem value) {

        final View view = LayoutInflater.from(context).inflate(R.layout.outline_chapter_item, null, false);
        chapterItem = value;
        titleText = (TextView)view.findViewById(R.id.txt);
        titleText.setText(value.getChapterTitle());
        iconImage = view.findViewById(R.id.image_outline_arrow);
        return view;
    }

    @Override
    public void highlightText(TreeNode node) {
        if(titleText == null){
            return;
        }
        titleText.setTextColor(node.isHight() ? context.getResources().getColor(R.color.blue1) : context.getResources().getColor(R.color.white));

    }

    @Override
    public void toggle(boolean active) {

        super.toggle(active);
        if(chapterItem != null){
            chapterItem.setToggle(active);
            Log.e("toggle","chapterItem:" + chapterItem);
        }
        iconImage.setImageDrawable(context.getResources().getDrawable(!active ? R.drawable.outline_collapse : R.drawable.outline_expand));

    }
}
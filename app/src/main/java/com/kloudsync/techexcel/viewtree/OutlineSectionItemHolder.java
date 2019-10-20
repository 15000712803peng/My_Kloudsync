package com.kloudsync.techexcel.viewtree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChapterItem;
import com.kloudsync.techexcel.bean.OutlineSectionItem;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;

public class OutlineSectionItemHolder extends TreeNode.BaseNodeViewHolder<OutlineSectionItem>{


    public OutlineSectionItemHolder(Context context) {
        super(context);
    }

    TextView titleText;
    ImageView iconImage;
    OutlineSectionItem sectionItem;

    @Override
    public View createNodeView(TreeNode node, OutlineSectionItem value) {
        final View view = LayoutInflater.from(context).inflate(R.layout.outline_section_item, null, false);
        titleText = (TextView)view.findViewById(R.id.txt);
        titleText.setText(value.getSectionTitle());
        sectionItem = value;
        iconImage = view.findViewById(R.id.image_outline_arrow);
        if(value.getChildSectionItems() == null || value.getChildSectionItems().size() <= 0){
            iconImage.setVisibility(View.INVISIBLE);
        }else {
            iconImage.setVisibility(View.VISIBLE);
        }
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
        if(sectionItem != null){
            sectionItem.setToggle(active);
        }
        iconImage.setImageDrawable(context.getResources().getDrawable(!active ? R.drawable.outline_collapse : R.drawable.outline_expand));

    }
}
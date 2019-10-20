package com.kloudsync.techexcel.viewtree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChildSectionItem;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;

public class OutlineChildChildChildSectionItemHolder extends TreeNode.BaseNodeViewHolder<OutlineChildSectionItem>{


    public OutlineChildChildChildSectionItemHolder(Context context) {
        super(context);
    }

    TextView titleText;
    ImageView iconImage;
    OutlineChildSectionItem sectionItem;
    @Override
    public View createNodeView(TreeNode node, OutlineChildSectionItem value) {
        final View view = LayoutInflater.from(context).inflate(R.layout.outline_child_child_childsection_item, null, false);
        titleText = (TextView)view.findViewById(R.id.txt);
        sectionItem = value;
        titleText.setText(value.getSectionTitle());
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
//       titleText.setTextColor(active ? R.color.white :R.color.pi_bg_change);

    }
}
package com.kloudsync.techexcel.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.tool.TextTool;

import java.util.List;


public class OrganizationAdapter extends HeaderRecyclerAdapter<School> implements View.OnClickListener {
    private int schoolId;
    String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDatas(List<School> datas, int schoolId) {
        this.schoolId = schoolId;
        super.setDatas(datas);
    }

    @Override
    public void onClick(View view) {

    }

    public OrganizationAdapter(int schoolId) {
        this.schoolId = schoolId;
    }

    public void setSelectedId(int schoolId) {
        this.schoolId = schoolId;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, final int realPosition, final School data) {
        ItemHolder holder = (ItemHolder) viewHolder;
        if (!TextUtils.isEmpty(keyword)) {
            holder.tv_school.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), data.getSchoolName(), keyword));
        } else {
            holder.tv_school.setText(data.getSchoolName());
        }
        holder.img_choosen.setVisibility((schoolId != data.getSchoolID()) ? View.GONE : View.VISIBLE);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_school;
        ImageView img_choosen;
        LinearLayout itemLayout;

        ItemHolder(View view) {
            super(view);
            tv_school = (TextView) view.findViewById(R.id.tv_school);
            img_choosen = (ImageView) view.findViewById(R.id.img_choosen);
            itemLayout = view.findViewById(R.id.layout_item);
        }
    }

}

package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.CompanyContact;
import com.kloudsync.techexcel.tool.TextTool;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class InviteFromCompanyAdapter extends HeaderRecyclerAdapter<CompanyContact> {

    Context context;
    String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public InviteFromCompanyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_contact, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, final CompanyContact contact) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        String url2 = contact.getAvatarUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
        }

        if (!TextUtils.isEmpty(keyword)) {
            holder.tv_name.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), contact.getUserName(), keyword));
            holder.phoneText.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), contact.getPhone(), keyword));
        } else {
            holder.tv_name.setText(contact.getUserName());
            holder.phoneText.setText(contact.getPhone());
        }
        holder.contactImage.setImageURI(imageUri2);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contact.setSelected(isChecked);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        SimpleDraweeView contactImage;
        TextView phoneText;
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.txt_name);
            contactImage = (SimpleDraweeView) view.findViewById(R.id.image_contact);
            phoneText = view.findViewById(R.id.txt_phone);
            checkBox = view.findViewById(R.id.checkbox);
        }
    }

    public List<CompanyContact> getSelectedContacts() {
        List<CompanyContact> selContacts = new ArrayList<>();
        for (CompanyContact c : mDatas) {
            if (c.isSelected()) {
                selContacts.add(c);
            }
        }
        return selContacts;
    }

}

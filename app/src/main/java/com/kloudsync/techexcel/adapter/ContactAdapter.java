package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.PinyinComparator;

import java.util.Collections;
import java.util.List;

public class ContactAdapter extends CommonAdapter<Customer> {

    private List<Customer> list = null;
    private Context mContext;
//    public ImageLoader imageLoader;

    public ContactAdapter(Context mContext, List<Customer> list2) {
        super(mContext, list2);
        this.mContext = mContext;
    }

    private void AddRobetinfo(List<Customer> list2) {
        SortCustomers(list2);
        Customer cus = new Customer();
        cus.setName(AppConfig.RobotName);
        cus.setSortLetters(AppConfig.Robot);
        this.list = list2;
//        this.list.add(0, cus);
    }

    public void updateListView(List<Customer> list2) {
        AddRobetinfo(list2);
        updateAdapter(list);
    }

    public void updateListView2(List<Customer> list2) {
        this.list = list2;
        updateAdapter(list);
    }

    @SuppressLint("NewApi")
    @Override
    public void convert(ViewHolder holder, Customer customer, int position) {
        holder.setText(R.id.tv_name, customer.getName())
                .setText(R.id.tv_peertimeid, "null")
                .setText(R.id.tv_sort, customer.getSortLetters());
        final SimpleDraweeView img = holder.getView(R.id.img_head);
        String url = customer.getUrl();
        Uri imageUri = null;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
        img.setImageURI(imageUri);
        int sectionVisible = SideBarSortHelp.getPositionForSection(list,
                customer.getSortLetters().charAt(0));
        if (sectionVisible == position) {
            holder.setViewVisible(R.id.tv_sort, View.VISIBLE);
        } else {
            holder.setViewVisible(R.id.tv_sort, View.GONE);
        }
        holder.setViewVisible(R.id.img_chat, customer.isEnableChat() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getLayout(int position) {
        // TODO Auto-generated method stub
        return R.layout.contact_item;
    }

    public void SortCustomers(List<Customer> list2) {
        Collections.sort(list2, new PinyinComparator());
    }

}

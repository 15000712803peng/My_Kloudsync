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

public class SchoolContactAdapter extends CommonAdapter<Customer> {

    private List<Customer> list = null;
    private Context mContext;
//    public ImageLoader imageLoader;

    public SchoolContactAdapter(Context mContext, List<Customer> list2) {
        super(mContext, list2);
        this.mContext = mContext;
        AddRobetinfo(list2);
    }

    private void AddRobetinfo(List<Customer> list2) {
        SortCustomers(list2);
        Customer cus = new Customer();
        cus.setName(AppConfig.RobotName);
        cus.setSortLetters(AppConfig.Robot);
        this.list = list2;
        this.list.add(0, cus);
    }

    public void updateListView(List<Customer> list2) {
        AddRobetinfo(list2);
        updateAdapter(list);
    }

    public void updateListView2(List<Customer> list2) {
        this.list = list2;
        updateAdapter(list);
    }

    public void Remove0() {
        if(list.size() > 0){
            list.remove(0);
        }
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
        if(!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
        img.setImageURI(imageUri);

        if (position != 0) {
            int sectionVisible = SideBarSortHelp.getPositionForSection(list,
                    customer.getSortLetters().charAt(0));
            if (sectionVisible == position) {
                holder.setViewVisible(R.id.tv_sort, View.VISIBLE);
            } else {
                holder.setViewVisible(R.id.tv_sort, View.GONE);
            }
        } else {
            holder.setViewVisible(R.id.tv_sort, View.VISIBLE);
        }


    }

    @Override
    public int getLayout(int position) {
        // TODO Auto-generated method stub
        return R.layout.school_contact_item;
    }

    private void SortCustomers(List<Customer> list2) {
        Collections.sort(list2, new PinyinComparator());

    }

}

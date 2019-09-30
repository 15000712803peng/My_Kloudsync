package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.AccountSettingContactBean;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.AccountSettingPinyinComparator;

import java.util.Collections;
import java.util.List;

/**
 * Created by wang on 2018/3/23.
 */

public class AccountSettingContactAdapter extends BaseAdapter {

    private Context context;
    private List<AccountSettingContactBean> serviceList;
    public ImageLoader imageLoader;
    private int isShow;

    boolean fromSearch;
    String keyword;

    public AccountSettingContactAdapter(Context context, List<AccountSettingContactBean> serviceList, boolean isPublic, int isShow) {
        Log.e("老余adapter", "laozhang"+serviceList.size());
        this.context = context;
        this.serviceList = serviceList;
        this.isShow = isShow;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    public OnModifyServiceListener onModifyServiceListener;


    public interface OnModifyServiceListener {
        void select(AccountSettingContactBean bean);
    }

    public void setOnModifyServiceListener(OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    //搜索
    public void updateListView2(List<AccountSettingContactBean> list2) {
        this.serviceList = list2;
        notifyDataSetChanged();
    }

    //首字母
    public void updateListView(List<AccountSettingContactBean> list2) {
         AddRobetinfo(list2);
         notifyDataSetChanged();

    }
    private void AddRobetinfo(List<AccountSettingContactBean> list2) {
        SortCustomers(list2);
        this.serviceList = list2;
    }


    public void SortCustomers(List<AccountSettingContactBean> list2) {
        Collections.sort(list2, new AccountSettingPinyinComparator());
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.account_setting_contact_item, null);
            holder.as_img_admin_user = convertView.findViewById(R.id.as_img_admin_user);
            holder.as_tv_admin_user_item = convertView.findViewById(R.id.as_tv_admin_user_item);
            holder.tv_sort = convertView.findViewById(R.id.tv_sort);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }





        final AccountSettingContactBean bean = serviceList.get(position);
        final ViewHolder finalHolder = holder;



        holder.as_tv_admin_user_item.setText(bean.getUserName());


        String imageurl = bean.getAvatarUrl();
        Uri imageUri = Uri.parse(imageurl);
        holder.as_img_admin_user.setImageURI(imageUri);


        holder.tv_sort.setText(bean.getSortLetters());
        int sectionVisible = getPositionForSection(serviceList,
                bean.getSortLetters().charAt(0));
        if (sectionVisible == position) {
            holder.tv_sort.setVisibility(View.VISIBLE);
        } else {
            holder.tv_sort.setVisibility(View.GONE);
        }



        Log.e("老余adapter", bean.getAvatarUrl() + "");
        return convertView;

    }

    class ViewHolder {

        TextView as_tv_admin_user_item,tv_sort;
        SimpleDraweeView as_img_admin_user;

    }
        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public static int getPositionForSection(List<AccountSettingContactBean> list,char section) {
            for (int i = 0; i < list.size(); i++) {
                String sortStr = list.get(i).getSortLetters();
                if(null == sortStr){
                    continue;
                }
                char firstChar = sortStr.charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }


    }


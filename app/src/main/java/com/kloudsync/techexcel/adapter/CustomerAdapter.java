package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.tool.PinyinComparator;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.TeamPropertyActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerAdapter extends CommonAdapter<Customer> {

    private List<Customer> list = null;
    private Context mContext;

    public CustomerAdapter(Context mContext, List<Customer> list2) {
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
        int viewType = getItemViewType(position);
        if (0 == viewType) {
            final Space space = customer.getSpace();
            ArrayList<Space> sl = customer.getSpaceList();
            holder.setText(R.id.tv_name, space.getName());
            holder.setViewVisible(R.id.tv_sort, 0 == position ? View.VISIBLE : View.GONE);

            switch (position % 5){
                case 0:
                    holder.setImageResource(R.id.img_head, R.drawable.avtar_1);
                    break;
                case 1:
                    holder.setImageResource(R.id.img_head, R.drawable.avtar_2);
                    break;
                case 2:
                    holder.setImageResource(R.id.img_head, R.drawable.avtar_3);
                    break;
                case 3:
                    holder.setImageResource(R.id.img_head, R.drawable.avtar_4);
                    break;
                case 4:
                    holder.setImageResource(R.id.img_head, R.drawable.avtar_5);
                    break;

            }

            LinearLayout lin_team = holder.getView(R.id.lin_team);
            lin_team.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, TeamPropertyActivity.class);
                    i.putExtra("ItemID", space.getItemID());
                    ((Activity)mContext).startActivity(i);
                }
            });
            ImageView img_arrow = holder.getView(R.id.img_arrow);
            img_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    space.setExpand(!space.isExpand());
                    updateAdapter(list);
                }
            });
            img_arrow.setImageDrawable(mContext.getDrawable(space.isExpand()? R.drawable.arrow_up: R.drawable.arrow_down));
            LinearLayout myLayout = holder.getView(R.id.lin_expand);
            myLayout.removeAllViews();
            if (space.isExpand()) {
                for (int i = 0; i < sl.size(); i++) {
                    final Space ssp = sl.get(i);
                    LinearLayout lin_son = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.team_son_item, null);
                    TextView tv_sort = (TextView) lin_son.findViewById(R.id.tv_sort);
                    TextView tv_sname = (TextView) lin_son.findViewById(R.id.tv_sname);

                    lin_son.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, SpaceDocumentsActivity.class);
                            intent.putExtra("ItemID", ssp.getItemID());
                            ((Activity)mContext).startActivity(intent);
                        }
                    });

                    switch (i % 3){
                        case 0:
                            tv_sort.setBackgroundResource(R.drawable.blue_circle);
                            break;
                        case 1:
                            tv_sort.setBackgroundResource(R.drawable.orange_cicle);
                            break;
                        case 2:
                            tv_sort.setBackgroundResource(R.drawable.circle_expand);
                            break;

                    }
                    tv_sname.setText(ssp.getName());
                    if(ssp.getName().length() > 0) {
                        tv_sort.setText(ssp.getName().substring(0, 1));
                    }else{
                        tv_sort.setText("");
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 10, 0, 10);

                    lin_son.setLayoutParams(lp);

                    myLayout.addView(lin_son);
                }
            }

        } else {

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
            holder.setViewVisible(R.id.img_chat,customer.isEnableChat()? View.VISIBLE:View.GONE);
        }


    }

    @Override
    public int getLayout(int position) {
        // TODO Auto-generated method stub

        if (list.get(position).isTeam()) {
            return R.layout.customer_item2;
        } else {
            return R.layout.customer_item;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isTeam()) {
            return 0;
        } else {
            return 1;
        }
    }

    public void SortCustomers(List<Customer> list2) {
        Collections.sort(list2, new PinyinComparator());
    }


}

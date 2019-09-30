package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.PinyinComparator;
import com.kloudsync.techexcel.tool.TextTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ContactHolder> {

    private List<Customer> customers = new ArrayList<>();
    private Context mContext;
    private String keyword;

    public interface OnItemClickListener {
        void onItemClick(int position, Customer customer);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public void setCustomers(List<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }

    public void SortCustomers(List<Customer> list2) {
        Collections.sort(list2, new PinyinComparator());
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_contact_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, final int position) {
        final Customer customer = customers.get(position);
        String url = customer.getUrl();
        Uri imageUri = null;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
        holder.avatarImage.setImageURI(imageUri);
        holder.chatImage.setVisibility(customer.isEnableChat() ? View.VISIBLE : View.GONE);
        holder.headerText.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, customer);
                }
            }
        });
        holder.nameText.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), customer.getName(), keyword));

    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nameText;
        ImageView chatImage;
        TextView headerText;

        public ContactHolder(View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.img_head);
            nameText = itemView.findViewById(R.id.tv_name);
            chatImage = itemView.findViewById(R.id.img_chat);
            headerText = itemView.findViewById(R.id.tv_sort);
        }
    }

}

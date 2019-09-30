package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Company;

import java.util.ArrayList;
import java.util.List;

public class InvitationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Company> companies = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, null));
    }

    public void setCompanies(List<Company> companies) {
        this.companies.clear();
        this.companies.addAll(companies);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final Company company = companies.get(position);
        if (position == 0) {
            myHolder.divider.setVisibility(View.GONE);
        } else {
            myHolder.divider.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(company.getCompanyName())) {
            myHolder.nameText.setText(company.getCompanyName());
        }
        myHolder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company.setSelected(!company.isSelected());
                myHolder.checkBox.setChecked(!myHolder.checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView icon;
        CheckBox checkBox;
        View divider;
        CardView cardItem;

        public Holder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.txt_company_name);
            divider = itemView.findViewById(R.id.item_divider);
            cardItem = itemView.findViewById(R.id.card_item);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public interface OnItemClickLisent {
        void onItemClick(Company company);
    }

    OnItemClickLisent onItemClickLisent;

    public OnItemClickLisent getOnItemClickLisent() {
        return onItemClickLisent;
    }

    public void setOnItemClickLisent(OnItemClickLisent onItemClickLisent) {
        this.onItemClickLisent = onItemClickLisent;
    }

    public String getSelectedCompanies() {
        String companiesStr = "";
        for (Company company : companies) {
            if (company.isSelected()) {
                companiesStr += company.getCompanyID() + ",";
            }
        }
        return companiesStr;
    }
}

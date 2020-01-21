package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.bean.EventCompanyClicked;
import com.kloudsync.techexcel.bean.SimpleCompanyData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class InvitationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<SimpleCompanyData> companies = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, null));
    }

    public void setCompanies(List<SimpleCompanyData> companies) {
        this.companies.clear();
        this.companies.addAll(companies);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final SimpleCompanyData company = companies.get(position);

        if (!TextUtils.isEmpty(company.getSchoolName())) {
            myHolder.nameText.setText(company.getSchoolName());
        }

        myHolder.checkBox.setChecked(company.isSelected());

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelected(company.getSchoolID());
                company.setSelected(!company.isSelected());
                notifyDataSetChanged();
                EventBus.getDefault().post(new EventCompanyClicked());
            }
        });
        myHolder.checkBox.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView icon;
        CheckBox checkBox;
        public Holder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.txt_company_name);
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

    public SimpleCompanyData getSelectedCompanies() {
        String companiesStr = "";
        for (SimpleCompanyData company : companies) {
            if (company.isSelected()) {
                return company;
            }
        }
        return null;

    }

    private void clearSelected(int companyId){
        for(SimpleCompanyData company : companies){
            if(company.getSchoolID() == companyId){
                continue;
            }
            company.setSelected(false);
        }
    }

}

package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.CompanySubsystem;
import com.kloudsync.techexcel.info.School;

import java.util.ArrayList;
import java.util.List;


public class OrganizationAdapterV2 extends BaseExpandableListAdapter implements View.OnClickListener {

    private List<School> companies = new ArrayList<>();
    private Context context;

    public OrganizationAdapterV2(Context context) {
        this.context = context;
    }

    public void setCompanies(List<School> companies) {
        this.companies.clear();
        this.companies.addAll(companies);
        notifyDataSetChanged();

    }


    public List<School> getCompanies() {
        return companies;
    }

    public void setSubsystems(List<CompanySubsystem> subsystems, int companyId) {
        for (School company : this.companies) {
            if (company.getSchoolID() == companyId) {
                company.setSubsystems(subsystems);
                break;
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public int getGroupCount() {
        return companies.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<CompanySubsystem> subsystems = companies.get(groupPosition).getSubsystems();
        if (subsystems != null) {
            return subsystems.size();
        }
        return 0;
    }

    @Override
    public School getGroup(int groupPosition) {
        return companies.get(groupPosition);
    }

    @Override
    public CompanySubsystem getChild(int groupPosition, int childPosition) {
        List<CompanySubsystem> subsystems = companies.get(groupPosition).getSubsystems();
        if (subsystems != null) {
            return subsystems.get(childPosition);
        }
        return null;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return companies.get(groupPosition).getSchoolID();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        List<CompanySubsystem> subsystems = companies.get(groupPosition).getSubsystems();
        if (subsystems != null) {
            return Long.parseLong(subsystems.get(childPosition).getSubSystemId());
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        School company = getGroup(groupPosition);
        CompanyHolder companyHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_company, null);
            companyHolder = new CompanyHolder();
            companyHolder.selectedImage = convertView.findViewById(R.id.imge_selected);
            companyHolder.companyAvatar = convertView.findViewById(R.id.image_company_avatar);
            companyHolder.companyNameText = convertView.findViewById(R.id.txt_company_name);
            companyHolder.divider = convertView.findViewById(R.id.group_divider);
            companyHolder.groupItem = convertView.findViewById(R.id.layout_group_item);
            convertView.setTag(companyHolder);
        } else {
            companyHolder = (CompanyHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            companyHolder.divider.setVisibility(View.GONE);
        } else {
            companyHolder.divider.setVisibility(View.VISIBLE);
        }
        companyHolder.companyNameText.setText(company.getSchoolName());
        if (company.isSelected()) {
            companyHolder.selectedImage.setImageResource(R.drawable.select_dy);
            companyHolder.groupItem.setBackgroundResource(R.drawable.corner_green_bg);
            companyHolder.companyNameText.setTextColor(context.getResources().getColor(R.color.pc_white));
        } else {
            companyHolder.selectedImage.setImageResource(R.drawable.unchecked2);
            companyHolder.groupItem.setBackgroundResource(R.drawable.rc_white_bg_shape);
            companyHolder.companyNameText.setTextColor(context.getResources().getColor(R.color.pi_phone_text));
        }
        return convertView;
    }

    public String getSelectCompanyId() {
        for (School company : this.companies) {
            if (company.isSelected()) {
                return company.getSchoolID() + "";
            }
        }
        return "";
    }

    public School getSelectCompany() {
        for (School company : this.companies) {
            if (company.isSelected()) {
                return company;
            }
        }
        return null;
    }


    public void setSelectCompany(int companyId) {
        for (School company : this.companies) {
            if (company.getSchoolID() == companyId) {
                company.setSelected(true);
            } else {
                company.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public CompanySubsystem getSelectSubsystem() {
        School selectedCompany = getSelectCompany();
        List<CompanySubsystem> subsystems = null;

        if (selectedCompany != null) {
            subsystems = selectedCompany.getSubsystems();
        }
        if (subsystems != null && subsystems.size() > 0) {
            for (CompanySubsystem subsystem : subsystems) {
                if (subsystem.isSelected()) {
                    return subsystem;
                }
            }
        }

        return null;

    }

    public void clearSelectedSubsystem(int companyId) {
        School selectedCompany = null;
        List<CompanySubsystem> subsystems = null;
        for (School company : this.companies) {
            if (company.getSchoolID() == companyId) {
                selectedCompany = company;
                break;
            }
        }
        if (selectedCompany != null) {
            subsystems = selectedCompany.getSubsystems();
        }
        if (subsystems != null && subsystems.size() > 0) {
            for (CompanySubsystem subsystem : subsystems) {
                subsystem.setSelected(false);

            }
        }
        notifyDataSetChanged();
    }

    public void setUnselectSubsystem(int companyId, String selectedSubsystemId) {
        School selectedCompany = null;
        List<CompanySubsystem> subsystems = null;
        for (School company : this.companies) {
            if (company.getSchoolID() == companyId) {
                selectedCompany = company;
                break;
            }
        }
        if (selectedCompany != null) {
            subsystems = selectedCompany.getSubsystems();
        }
        if (subsystems != null && subsystems.size() > 0) {
            for (CompanySubsystem subsystem : subsystems) {
                if (subsystem.getSubSystemId().equals(selectedSubsystemId)) {
                    subsystem.setSelected(false);
                }
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CompanySubsystem subsystem = getChild(groupPosition, childPosition);
        SubsystemHolder subsystemHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_subsystem, null);
            subsystemHolder = new SubsystemHolder();
            subsystemHolder.selectedImage = convertView.findViewById(R.id.imge_selected);
            subsystemHolder.systemAvatar = convertView.findViewById(R.id.image_company_avatar);
            subsystemHolder.systemNameText = convertView.findViewById(R.id.txt_company_name);
            convertView.setTag(subsystemHolder);
        } else {
            subsystemHolder = (SubsystemHolder) convertView.getTag();
        }
        subsystemHolder.systemNameText.setText(subsystem.getSubSystemName());
        if (subsystem.isSelected()) {
            subsystemHolder.selectedImage.setImageResource(R.drawable.select_dy);
        } else {
            subsystemHolder.selectedImage.setImageResource(R.drawable.unchecked2);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class CompanyHolder {
        TextView companyNameText;
        ImageView selectedImage;
        ImageView companyAvatar;
        View divider;
        RelativeLayout groupItem;
    }

    class SubsystemHolder {
        TextView systemNameText;
        ImageView selectedImage;
        ImageView systemAvatar;
    }
}

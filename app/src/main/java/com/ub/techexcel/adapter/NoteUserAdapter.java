package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Customer;

import java.util.List;

public class NoteUserAdapter extends BaseAdapter {

    private Context context;
    private List<Customer> customerList;

    public NoteUserAdapter(Context context, List<Customer> serviceList) {
        this.context = context;
        this.customerList = serviceList;
    }

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if (convertView == null) {
            holder = new MyViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.noteuseritem, null);
            holder.name = convertView.findViewById(R.id.tv_name);
            holder.imageView = convertView.findViewById(R.id.img_selected);
            holder.ll = convertView.findViewById(R.id.ll);
            holder.number = convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        final Customer cu = customerList.get(position);
        if (cu.isSelected()) {
            holder.imageView.setImageResource(R.drawable.check2);
        } else {
            holder.imageView.setImageResource(R.drawable.unchecked2);
        }
        holder.name.setText(cu.getName());
        holder.number.setText(cu.getNoteCount() + "");
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModifyServiceListener.select(position);
            }
        });
        return convertView;

    }


    class MyViewHolder extends ViewHolder {
        TextView name;
        ImageView imageView;
        RelativeLayout ll;
        TextView number;
    }


    private OnModifyCourseListener onModifyServiceListener;

    public void setOnModifyCourseListener(
            OnModifyCourseListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyCourseListener {

        void select(int position);

    }


}

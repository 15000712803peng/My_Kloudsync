package com.kloudsync.techexcel.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.Jianbuderen;

import java.util.ArrayList;
import java.util.List;


public class InviteNew2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Customer> mlist = new ArrayList<>();

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public InviteNew2Adapter(List<Customer> mlist) {
        this.mlist = mlist;
        Jianbuderen.SortCustomers(mlist);
    }

    public void UpdateRV(List<Customer> mlist) {
        this.mlist = mlist;
        Jianbuderen.SortCustomers(mlist);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitenew, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Customer cus = mlist.get(position);
        holder.tv_name.setText(cus.getName());
        holder.img_selected.setImageDrawable(cus.isSelected() ? holder.itemView.getContext().getResources().getDrawable(
               R.drawable.check)
               : null);
        String url = cus.getUrl();
        Uri imageUri = Uri.parse(url);
        holder.img.setImageURI(imageUri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        holder.tv_sort.setText(cus.getSortLetters());
        int sectionVisible = SideBarSortHelp.getPositionForSection(mlist,
                cus.getSortLetters().charAt(0));
        Log.e("biang", position + ":" + cus.getSortLetters().charAt(0) + ":" + sectionVisible);
        holder.tv_sort.setVisibility((sectionVisible != position) ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_sort;
        SimpleDraweeView img;
        ImageView img_selected;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_sort = (TextView) view.findViewById(R.id.tv_sort);
            img = (SimpleDraweeView) view.findViewById(R.id.img_head);
            img_selected = (ImageView) view.findViewById(R.id.img_selected);
        }
    }


}

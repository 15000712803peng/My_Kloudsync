package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;

import java.util.List;

public class AddAuditorAdapter extends BaseAdapter {

    private List<Customer> list = null;
    private Context mContext;
    private ImageLoader imageLoader;

    public AddAuditorAdapter(Context mContext, List<Customer> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = new ImageLoader(mContext.getApplicationContext());
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<Customer> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Customer> getlist() {
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.friends_item,
                    null);
            viewHolder.name = (TextView) view.findViewById(R.id.name); // 名字
            viewHolder.tvLetter = (TextView) view
                    .findViewById(R.id.item1_catalog);// 字母
            viewHolder.imageView = (SimpleDraweeView) view
                    .findViewById(R.id.image);
            viewHolder.img_selected = (ImageView) view.findViewById(R.id.img_selected);
            viewHolder.img_selected.setVisibility(View.VISIBLE);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int position1 = position;
        final Customer entity = list.get(position1);
        // 根据ListView的当前位置获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position1);
        if (position1 == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(entity.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        if (entity.isSelected()) {
            viewHolder.img_selected.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.select_b));
        } else {
            viewHolder.img_selected.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.unselect));
        }
        viewHolder.name.setText(entity.getName());
        String url = entity.getUrl();

        if(!TextUtils.isEmpty(url)){
            Uri imageUri = Uri.parse(url);
            viewHolder.imageView.setImageURI(imageUri);
        }


        return view;
    }

    final static class ViewHolder {
        TextView tvLetter; // 首字母
        TextView name;
        SimpleDraweeView imageView;
        ImageView img_selected;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值 == 与此时 当前位置 的字母的值相等
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    public Object[] getSections() {
        return null;
    }


}

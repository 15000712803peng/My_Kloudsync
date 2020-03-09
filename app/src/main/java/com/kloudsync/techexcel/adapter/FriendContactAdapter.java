package com.kloudsync.techexcel.adapter;

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
import com.kloudsync.techexcel.bean.FriendContact;
import com.kloudsync.techexcel.bean.SameLetterFriends;
import com.kloudsync.techexcel.help.SideBarSortHelp;

import java.util.List;

public class FriendContactAdapter extends BaseAdapter {

    private static final int TYPE_CATEGORY_ITEM = 0;
    private static final int TYPE_ITEM = 1;

    private List<SameLetterFriends> letterFriendsList;
    private LayoutInflater mInflater;

    public FriendContactAdapter(Context context, List<SameLetterFriends> letterFriendsList) {
        this.letterFriendsList = letterFriendsList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != letterFriendsList) {
            //  所有分类中item的总和是ListVIew  Item的总个数  
            for (SameLetterFriends letterFriends : letterFriendsList) {
                count += letterFriends.getItemCount();
            }
        }

        return count;
    }

    @Override
    public Object getItem(int position) {

        // 异常情况处理  
        if (null == letterFriendsList || position < 0 || position > getCount()) {
            return null;
        }

        // 同一分类内，第一个元素的索引值  
        int categroyFirstIndex = 0;

        for (SameLetterFriends letterFriends : letterFriendsList) {
            int size = letterFriends.getItemCount();
            // 在当前分类中的索引值  
            int categoryIndex = position - categroyFirstIndex;
            // item在当前分类内  
            if (categoryIndex < size) {
                return letterFriends.getItem(categoryIndex);
            }
            // 索引移动到当前分类结尾，即下一个分类第一个元素索引  
            categroyFirstIndex += size;
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // 异常情况处理  
        if (null == letterFriendsList || position < 0 || position > getCount()) {
            return TYPE_ITEM;
        }

        int firstIndex = 0;
        for (SameLetterFriends letterFriends : letterFriendsList) {
            int size = letterFriends.getItemCount();
            // 在当前分类中的索引值  
            int categoryIndex = position - firstIndex;
            if (categoryIndex == 0) {
                return TYPE_CATEGORY_ITEM;
            }

            firstIndex += size;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_CATEGORY_ITEM:
                TitleHolder titleHolder = null;
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.letter_item, null);
                    titleHolder = new TitleHolder();
                    titleHolder.titleText = convertView.findViewById(R.id.tv_sort);
                    convertView.setTag(titleHolder);
                } else {
                    titleHolder = (TitleHolder) convertView.getTag();
                }
                if (getItem(position) instanceof String) {
                    String title = (String) getItem(position);
                    titleHolder.titleText.setText(title);
                }
                break;

            case TYPE_ITEM:
                ContactHolder contactHolder = null;
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.friend_contact_item, null);
                    contactHolder = new ContactHolder();
                    contactHolder.img = convertView.findViewById(R.id.img_head);
                    contactHolder.name = convertView.findViewById(R.id.name);
                    contactHolder.chatImage = convertView.findViewById(R.id.img_chat);
                    convertView.setTag(contactHolder);
                } else {
                    contactHolder = (ContactHolder) convertView.getTag();
                }

                if (getItem(position) instanceof FriendContact) {
                    FriendContact friendContact = (FriendContact) getItem(position);
                    String url = friendContact.getAvatarUrl();
                    Uri imageUri = null;
                    if (!TextUtils.isEmpty(url)) {
                        imageUri = Uri.parse(url);
                    }
                    contactHolder.img.setImageURI(imageUri);
                    contactHolder.name.setText(friendContact.getUserName());

                    if(friendContact.getStatus() == 1){
                        contactHolder.chatImage.setVisibility(View.VISIBLE);
                    }else {
                        contactHolder.chatImage.setVisibility(View.GONE);
                    }

//                    if(friendContact.is)

                }


//            int sectionVisible = SideBarSortHelp.getPositionForSection(list,
//                    customer.getSortLetters().charAt(0));
//            if (sectionVisible == position) {
//                holder.setViewVisible(R.id.tv_sort, View.VISIBLE);
//            } else {
//                holder.setViewVisible(R.id.tv_sort, View.GONE);
//            }
//
                // 绑定数据
                break;
        }

        return convertView;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_CATEGORY_ITEM;
    }

    public void refresh(List<SameLetterFriends> letterFriendsList) {
        this.letterFriendsList.clear();
        this.letterFriendsList.addAll(letterFriendsList);
        notifyDataSetChanged();
    }

    class TitleHolder {
        public TextView titleText;
    }

    class ContactHolder {
        public SimpleDraweeView img;
        public TextView name;
        public ImageView chatImage;
    }

}


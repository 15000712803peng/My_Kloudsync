package com.kloudsync.techexcel.adapter;

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
import com.kloudsync.techexcel.tool.TextTool;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

public class SimpleChatAdapter extends RecyclerView.Adapter<SimpleChatAdapter.ChatHolder> {

    List<Conversation> conversations = new ArrayList<>();
    String keyword;

    public interface OnItemClickListener {
        void onItemClick(int position, Conversation conversation);
    }

    private OnItemClickListener itemClickListener;


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_chat_item, parent, false);
        return new ChatHolder(view);
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations.clear();
        this.conversations.addAll(conversations);
//        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, final int position) {
        final Conversation conversation = conversations.get(position);
        if (!TextUtils.isEmpty(conversation.getPortraitUrl())) {
            holder.avatarImage.setImageURI(Uri.parse(conversation.getPortraitUrl()));
        } else {
            holder.avatarImage.setImageResource(R.drawable.rc_default_portrait);
        }
        holder.nameText.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), conversation.getSenderUserName(), keyword));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position, conversation);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nameText;

        public ChatHolder(View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.image_avatar);
            nameText = itemView.findViewById(R.id.txt_name);
        }
    }
}

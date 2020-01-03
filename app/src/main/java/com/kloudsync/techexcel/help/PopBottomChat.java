package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.ChatMessage;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class PopBottomChat implements PopupWindow.OnDismissListener, OnClickListener,IRongCallback.ISendMessageCallback {

    private PopupWindow bottomChatWindow;
    int width;
    private Context mContext;
    //--
    private RecyclerView chatList;
    private ChatAdapter adapter;
    private EditText editText;
    private ImageView sendImage;
    private String roomId;



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                String content = editText.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    Tools.sendMessageInRoom(content,roomId,this);
                }
                break;
        }
    }

    public PopBottomChat(Context context) {
        this.mContext = context;
        getPopupWindow();
        bottomChatWindow.setAnimationStyle(R.style.PopupAnimation5);
    }


    public void getPopupWindow() {
        if (null != bottomChatWindow) {
            bottomChatWindow.dismiss();
            return;
        } else {
            init();
        }
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.chat_pop2, null);
        chatList = (RecyclerView) view.findViewById(R.id.chat_list);
        editText = view.findViewById(R.id.edit);
        sendImage = view.findViewById(R.id.send);
        sendImage.setOnClickListener(this);
        chatList.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        bottomChatWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.chat_pop_height), false);
        bottomChatWindow.setOnDismissListener(this);
        bottomChatWindow.setBackgroundDrawable(new BitmapDrawable());
        bottomChatWindow.setOutsideTouchable(true);
        bottomChatWindow.setAnimationStyle(R.style.anination2);
        bottomChatWindow.setFocusable(true);
        adapter = new ChatAdapter();
        chatList.setAdapter(adapter);


    }


    public void show(View view,String roomId) {
        this.roomId = roomId;
        if (bottomChatWindow == null) {
            init();
        }

        bottomChatWindow.setOnDismissListener(this);
        if (!bottomChatWindow.isShowing()) {
            bottomChatWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }


    public boolean isShowing() {
        if (bottomChatWindow != null) {
            return bottomChatWindow.isShowing();
        }
        return false;
    }

    public void hide() {
        if (bottomChatWindow != null) {
            bottomChatWindow.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        EventBus.getDefault().post(new EventShowMenuIcon());
    }

    @Override
    public void onAttached(Message message) {

    }

    @Override
    public void onSuccess(Message message) {
        Log.e("send_chat_message","on_success");
    }

    @Override
    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

    }

    class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

        List<ChatMessage> messages = new ArrayList<>();

        public ChatAdapter(List<ChatMessage> messages){
            this.messages.clear();
            this.messages.addAll(messages);
        }

        public ChatAdapter(){

        }

        public void setMessages(List<ChatMessage> messages){
            this.messages.clear();
            this.messages.addAll(messages);
            notifyDataSetChanged();
        }

        public void addMessage(ChatMessage message){
            this.messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }

        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_v2, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
            ChatMessage message = messages.get(position);
            if(TextUtils.isEmpty(message.getUserName())){
                holder.name.setVisibility(View.INVISIBLE);
            }else {
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(message.getUserName());
            }

            TextMessage textMessage = (TextMessage) message.getMessage().getContent();
            if(!TextUtils.isEmpty(textMessage.getContent())){
                holder.content.setText(textMessage.getContent());
            }else {
                holder.content.setText("");
            }

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }


    class ChatHolder extends RecyclerView.ViewHolder{

        public TextView time;
        public TextView name;
        public TextView content;

        public ChatHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
        }
    }

    public void initMessages(List<ChatMessage> messages){
        if(isShowing()){
            if(adapter != null){
                adapter.setMessages(messages);
                chatList.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    public void addMessage(ChatMessage message){
        if(isShowing()){
            if(adapter != null){
                adapter.addMessage(message);
                chatList.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

}

package com.kloudsync.techexcel.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class ChatTextMsgView extends BaseMsgView {

    private TextView username;
    private TextView msgText;

    public ChatTextMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.chat_msg_text_view, this);
        username = (TextView) view.findViewById(R.id.username);
        msgText = (TextView) view.findViewById(R.id.msg_text);
    }

    @Override
    public void setContent(MessageContent msgContent, String senderUserId) {
        TextMessage msg = (TextMessage) msgContent;
        String name = senderUserId;
        username.setText(name + ": ");
        msgText.setText(msg.getContent());
    }
}

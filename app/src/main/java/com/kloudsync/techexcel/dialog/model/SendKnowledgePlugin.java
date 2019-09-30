package com.kloudsync.techexcel.dialog.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.dialog.SendKnowledge;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by pingfan on 2017/6/14.
 */

public class SendKnowledgePlugin implements IPluginModule {
    Conversation.ConversationType conversationType;
    String targetId;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable obtainDrawable(Context context) {
        //设置插件 Plugin 图标
        return context.getResources().getDrawable(R.drawable.commonphrase,null);
    }

    @Override
    public String obtainTitle(Context context) {
        // R.string.add_contacts 通讯录
        return context.getString(R.string.Send_knowledge);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        //示例获取 会话类型、targetId、Context,此处可根据产品需求自定义逻辑，如:开启新的 Activity 等。
        conversationType = rongExtension.getConversationType();
        targetId = rongExtension.getTargetId();
        Intent intent = new Intent(fragment.getActivity(), SendKnowledge.class);
        fragment.getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}

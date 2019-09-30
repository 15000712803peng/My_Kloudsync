package com.kloudsync.techexcel.dialog.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.ub.service.activity.SelectCourseActivity;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;


/**
 * Created by pingfan on 2017/6/14.
 */

public class NewServicePlugin implements IPluginModule {
    Conversation.ConversationType conversationType;
    String targetId;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable obtainDrawable(Context context) {
        //设置插件 Plugin 图标
        return context.getResources().getDrawable(R.drawable.service,null);
    }

    @Override
    public String obtainTitle(Context context) {
        // R.string.add_contacts 通讯录
        return context.getString(R.string.New_Service);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        //示例获取 会话类型、targetId、Context,此处可根据产品需求自定义逻辑，如:开启新的 Activity 等。
        conversationType = rongExtension.getConversationType();
        targetId = rongExtension.getTargetId();
        /*if (1 == AppConfig.UserType) {
            boolean ismember = false;
            for (int i = 0; i < memlist.size(); i++) {
                Customer cus = memlist.get(i);
                if (cus.getUBAOUserID().equals(targetId)) {
                    ismember = true;
                    break;
                }
            }
            Intent intent = new Intent(fragment.getActivity(),
                    SelectCourseActivity.class);
            intent.putExtra("mTargetId", targetId);
            int ctype = Conversation.ConversationType.GROUP == conversationType? 2 : 1;
            intent.putExtra("conversationtype", ctype);
            // intent.putExtra("isDialogue", true);
            AppConfig.isNewService = true;
            if (ismember) {
                Toast.makeText(fragment.getActivity(), "会员不可新建服务",
                        Toast.LENGTH_LONG).show();
            } else {
                fragment.getActivity().startActivity(intent);
            }
        }else {
            PopUbaoMan pum = new PopUbaoMan();
            int width = fragment.getActivity().getResources().getDisplayMetrics().widthPixels;
            int height = fragment.getActivity().getResources().getDisplayMetrics().heightPixels;
            pum.getPopwindow(fragment.getActivity(), width, height, fragment.getActivity().getString(R.string.Add_Service_Pop));
            pum.StartPop(fragment.getActivity().findViewById(R.id.tv_title));
            pum.setPoPDismissListener(new PopUbaoMan.PoPDismissListener() {

                @Override
                public void PopDismiss() {
                    // TODO Auto-generated method stub

                }
            });


        }*/
        /*boolean ismember = false;
        for (int i = 0; i < ulist.size(); i++) {
            Customer cus = ulist.get(i);
            if (cus.getUBAOUserID().equals(targetId)) {
                ismember = true;
                break;
            }
        }*/
        Intent intent = new Intent(fragment.getActivity(),
                SelectCourseActivity.class);
        intent.putExtra("mTargetId", targetId);
        int ctype = Conversation.ConversationType.GROUP == conversationType? 2 : 1;
        intent.putExtra("conversationtype", ctype);
        // intent.putExtra("isDialogue", true);
        AppConfig.isNewService = true;
        /*if (ismember) {
            Toast.makeText(fragment.getActivity(), "会员不可新建服务",
                    Toast.LENGTH_LONG).show();
        } else {
            fragment.getActivity().startActivity(intent);
        }*/
        fragment.getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}

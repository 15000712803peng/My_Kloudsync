package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.RoleAdapter;

import java.util.ArrayList;
import java.util.List;

public class PopPerfactRole {

    public Context mContext;

    private static PopUpdateOutDismissListener popUpdateOutDismissListener;

    private RecyclerView rv_role;

    private RoleAdapter Radapter;

    private int role;

    private String job;

    private List<String> Roles;

    public interface PopUpdateOutDismissListener {
        void PopDismiss(int role, String job);
    }

    public void setPoPDismissListener(PopUpdateOutDismissListener popUpdateOutDismissListener) {
        PopPerfactRole.popUpdateOutDismissListener = popUpdateOutDismissListener;
    }

    public void getPopwindow(Context context, int role) {
        this.mContext = context;
        this.role = role;
        String[] tab = context.getResources().getStringArray(R.array.Role);
        Roles = new ArrayList<>();
        for (int i = 0; i < tab.length; i++) {
            Roles.add(tab[i]);
            if (i == role) {
                job = tab[i];
            }
        }

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation3);
    }


    public PopupWindow mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.poprole, null);
        rv_role = (RecyclerView) popupWindow.findViewById(R.id.rv_role);

        ShowRoles();

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popUpdateOutDismissListener != null) {
                    popUpdateOutDismissListener.PopDismiss(role, job);
                }
            }
        });

        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void ShowRoles() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_role.setLayoutManager(manager);
        Radapter = new RoleAdapter(Roles, role);
        Radapter.setOnItemClickListener(new RoleAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String name, int position) {
                role = position;
                job = name;
                mPopupWindow.dismiss();
            }
        });
        rv_role.setAdapter(Radapter);
    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


}

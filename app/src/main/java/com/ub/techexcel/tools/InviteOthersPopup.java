package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class InviteOthersPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView cancel;
    private TextView ok;
    private ListView listView;
    private ArrayList<Customer> mlist = new ArrayList<Customer>();
    private GroupAdapter gadapter;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void select(List<Customer> list);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.inviteotherspopup, null);
        cancel = (ImageView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.listview);

        GetJiuCai();
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void GetJiuCai() {
        LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                mlist = new ArrayList<Customer>();
                mlist.addAll(list);
                gadapter = new GroupAdapter(mContext, mlist);
                listView.setAdapter(gadapter);
                listView.setOnItemClickListener(new InviteOthersPopup.myOnItem());

            }
        });
        loginget.CustomerRequest(mContext);
    }

    private class myOnItem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            gadapter.SetSelected(true);
            Customer cus;
            cus = mlist.get(position);
            if (!cus.isHasSelected()) {
                if (cus.isSelected()) {
                    mlist.get(position).setSelected(false);
                } else {
                    mlist.get(position).setSelected(true);
                }
            }
            gadapter.updateListView(mlist);
        }

    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                List<Customer> ll = new ArrayList<>();
                for (int i = 0; i < mlist.size(); i++) {
                    if (mlist.get(i).isSelected()) {
                        ll.add(mlist.get(i));
                    }
                }
                mFavoritePoPListener.select(ll);
                break;
            default:
                break;
        }
    }

}

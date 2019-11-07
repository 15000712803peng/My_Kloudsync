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
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.adapter.NoteUserAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class NoteOthersPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView cancel;
    private TextView ok;
    private ListView listView;
    private ArrayList<Customer> mlist = new ArrayList<Customer>();
    private NoteUserAdapter noteUserAdapter;


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

    private void getNoteList(String syncroomid) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/SyncRoomUserList?syncRoomID=" + syncroomid;
        ServiceInterfaceTools.getinstance().getSyncRoomUserList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                mlist.clear();
                mlist.addAll((List<Customer>) object);

                Collections.sort(mlist, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer s1, Customer s2) {
                        if (s1.getNoteCount() - (s2.getNoteCount()) != 0) {
                            return s1.getNoteCount() - (s2.getNoteCount());
                        } else {
                            return s1.getName().compareTo(s2.getName());
                        }
                    }
                });


                Collections.reverse(mlist); //反转

                for (int i = 0; i < mlist.size(); i++) {
                    mlist.get(i).setSelected(false);
                }

                noteUserAdapter = new NoteUserAdapter(mContext, mlist);
                noteUserAdapter.setOnModifyCourseListener(new NoteUserAdapter.OnModifyCourseListener() {
                    @Override
                    public void select(int pp) {
                        for (int i = 0; i < mlist.size(); i++) {
                            if (pp == i) {
                                mlist.get(pp).setSelected(true);
                            } else {
                                mlist.get(i).setSelected(false);
                            }
                        }
                        noteUserAdapter.notifyDataSetChanged();
                    }

                });
                listView.setAdapter(noteUserAdapter);
            }
        });
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, String syncroomId) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            getNoteList(syncroomId);
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

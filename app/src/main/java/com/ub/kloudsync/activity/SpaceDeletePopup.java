package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ShowSpaceAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogTSDelete;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class SpaceDeletePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private TextView ok, cancel;
    private TextView tv_team;
    private TextView tv_note, tv_title;
    private RecyclerView rv_space;
    private RelativeLayout rl_team;

    private ShowSpaceAdapter madapter;

    private static FavoritePoPListener mFavoritePoPListener;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private List<Space> mlist = new ArrayList<>();

    private int tid = -1;
    private int sid = -1;

    private boolean flagM;
    private Document document;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(mContext,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.SwitchSpace:
                    mFavoritePoPListener.refresh();
                    EventBus.getDefault().post(new TeamSpaceBean());
                    dismiss();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void delete(int spaceid);

        void refresh();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void setSP(ArrayList<Customer> cuslist) {
        this.cuslist = cuslist;
    }

    public void ChangeMove(Document document) {
        flagM = true;
        this.document = document;
        if (tv_note != null) {
            tv_note.setVisibility(View.GONE);
            ok.setVisibility(View.VISIBLE);
            tv_title.setText("Move to");
        }

    }

    public void SendTeam(int tid, String name) {
        this.tid = tid;
        if (tv_team != null) {
            tv_team.setText(name);
            GetSplist(tid);
        }

    }

    private void GetSplist(int tid) {
        for (int i = 0; i < cuslist.size(); i++) {
            Customer cus = cuslist.get(i);
            if (tid == cus.getSpace().getItemID()) {
                mlist = cus.getSpaceList();
                madapter.UpdateRV(mlist, sid);
                break;
            }
        }
    }

    public void ChangeMove2() {
        flagM = false;
        ok.setVisibility(View.GONE);
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
        view = layoutInflater.inflate(R.layout.space_delete_popup, null);

        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        tv_team = (TextView) view.findViewById(R.id.tv_team);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_note = (TextView) view.findViewById(R.id.tv_note);
        rl_team = (RelativeLayout) view.findViewById(R.id.rl_team);
        rv_space = (RecyclerView) view.findViewById(R.id.rv_space);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        rl_team.setOnClickListener(this);
        rv_space.setOnClickListener(this);

        ShowSpace();

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void ShowSpace() {
        madapter = new ShowSpaceAdapter(mlist);
        madapter.setOnItemClickListener(new ShowSpaceAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Space sp = mlist.get(position);
                sid = sp.getItemID();
                GetSplist(tid);
                if (!flagM) {
                    mFavoritePoPListener.delete(sid);
                    dismiss();
                }
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_space.setLayoutManager(manager);
        rv_space.setAdapter(madapter);
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
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
                BoyNextDoor();
                break;
            case R.id.rl_team:
                SelectID();
                break;

            default:
                break;
        }
    }

    private void BoyNextDoor() {
        if (sid < 0) {
            Toast.makeText(mContext, "Please select space first", Toast.LENGTH_LONG).show();
            return;
        }
        mFavoritePoPListener.delete(sid);
        if (flagM) {
            SwitchSpace();
        } else {
            dismiss();
        }

    }

    private void SwitchSpace() {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/SwitchSpace?itemIDs=" + document.getItemID()
                                    + "&spaceID=" + sid, jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SwitchSpace;
//                        JSONObject RetData = responsedata.getJSONObject("RetData");
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }

                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void SelectID() {
        DialogTSDelete dts = new DialogTSDelete();
        dts.setPoPDismissListener(new DialogTSDelete.DialogDismissListener() {
            @Override
            public void PopSelect(Customer cus) {
                tv_team.setText(cus.getSpace().getName());
                if (tid != cus.getSpace().getItemID()) {
                    sid = -1;
                }
                tid = cus.getSpace().getItemID();
                GetSplist(tid);
            }

           /* @Override
            public void PopSelect(Space sp, int type) {
                if(0 == type){
                    tv_team.setText(sp.getName());
                    if(tid != sp.getItemID()){
                        sid = -1;
                    }
                    tid = sp.getItemID();
                } else if(1 == type){
                    tv_space.setText(sp.getName());
                    sid = sp.getItemID();
                }
            }*/
        });
        /*if(0 == type) {
//            dts.SetType(type, spaceid);
        }else if(1 == type) {
            if(tid < 0){
                Toast.makeText(mActivity,"Please select team first",Toast.LENGTH_LONG).show();
                return;
            }
//            dts.SetType(type, tid);
        }*/
        dts.EditCancel(mContext, cuslist);
    }

}

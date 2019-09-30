package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.NetWorkHelp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by wang on 2017/9/18.
 */

public class TeamMorePopup implements View.OnClickListener {
    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private LinearLayout lin_delete;
    private LinearLayout lin_rename;
    private LinearLayout lin_quit;
    private LinearLayout lin_edit;
    private TextView tv_name;
    private TextView rename;
    private ImageView img_close;
    private boolean isTeam;
    private String name;
    private int itemID;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.RemoveMember:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    EventBus.getDefault().post(new Customer());
                    dismiss();
                    mFavoritePoPListener.quit();
                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(mContext,
                            result,
                            Toast.LENGTH_LONG).show();
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

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void delete();

        void rename();

        void quit();

        void edit();


    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void setIsTeam(boolean isTeam) {
        this.isTeam = isTeam;
    }

    public void setTSid(int itemID) {
        this.itemID = itemID;
    }

    public void setTName(String name) {
        this.name = name;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
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
        view = layoutInflater.inflate(R.layout.team_more_popup, null);
        lin_edit = (LinearLayout) view.findViewById(R.id.lin_edit);
        lin_rename = (LinearLayout) view.findViewById(R.id.lin_rename);
        lin_quit = (LinearLayout) view.findViewById(R.id.lin_quit);
        lin_delete = (LinearLayout) view.findViewById(R.id.lin_delete);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        rename = (TextView) view.findViewById(R.id.rename);
        img_close = (ImageView) view.findViewById(R.id.img_close);
        lin_delete.setOnClickListener(this);
        lin_quit.setOnClickListener(this);
        lin_rename.setOnClickListener(this);
        lin_edit.setOnClickListener(this);
        img_close.setOnClickListener(this);
        tv_name.setText(name);
        rename.setText(isTeam ? "Team members" : "Space members");
//        deleteTeam.setText(isTeam ? "Delete team" : "Delete space");
//        quitteam.setText(isTeam ? "Quit team" : "Quit space");

        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mFavoritePoPListener != null) {
                    mFavoritePoPListener.dismiss();
                }
            }
        });


    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
//            mPopupWindow.showAsDropDown(v);
            mPopupWindow.show();
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
            case R.id.lin_delete:
                dismiss();
                mFavoritePoPListener.delete();
                break;
            case R.id.lin_rename:
                dismiss();
                mFavoritePoPListener.rename();
                break;
            case R.id.lin_quit:
                QuitTeamSpace();
                break;
            case R.id.lin_edit:
                dismiss();
                mFavoritePoPListener.edit();
                break;
            case R.id.img_close:
                dismiss();
                break;

            default:
                break;
        }
    }

    private void QuitTeamSpace() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +"TeamSpace/RemoveMember?ItemID=" + itemID
                                    + "&MemberID=" + AppConfig.UserID
                    );
                    Log.e("RemoveMember", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.RemoveMember;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }

}

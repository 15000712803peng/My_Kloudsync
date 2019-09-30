package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.Favourite2Adapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

public class DialogSendFile {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_pc;
    public Context mContext;
    Conversation.ConversationType conversationType;
    String targetId;
    private ArrayList<Document> mlist = new ArrayList<Document>();
    private Favourite2Adapter fAdapter;

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
                case AppConfig.ShareDocument:
                    String url = AppConfig.Sharelive + (String) msg.obj;
                    TextMessage myTextMessage = TextMessage.obtain(url);
                    io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(targetId, Conversation.ConversationType.PRIVATE, myTextMessage);
                    RongIM.getInstance()
                            .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                                @Override
                                public void onAttached(io.rong.imlib.model.Message message) {

                                }

                                @Override
                                public void onSuccess(io.rong.imlib.model.Message message) {
                                    Log.e("lalala", "sendMessage onError");

                                }

                                @Override
                                public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                                    Log.e("lalala", "sendMessage onError");

                                }
                            });
                    dlgGetWindow.dismiss();
                    break;

                default:
                    break;
            }
        }
    };


    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void DialogDismiss();
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogSendFile.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context, Conversation.ConversationType conversationType,
                           String targetId) {
        this.mContext = context;
        this.conversationType = conversationType;
        this.targetId = targetId;

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setContentView(R.layout.sendfile);

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = width * 5 / 7;
        layoutParams.height = height * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        rv_pc = (RecyclerView) window.findViewById(R.id.rv_pc);

        GetData();

    }

    private void GetData() {

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
        fAdapter = new Favourite2Adapter(mlist);
        fAdapter.setOnItemClickListener(new Favourite2Adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Document fav = mlist.get(position);
                GetUserInfo(fav);
            }
        });
        rv_pc.setAdapter(fAdapter);

        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {
                mlist = new ArrayList<Document>();
                mlist.addAll(list);
                fAdapter.UpdateRV(mlist);
            }
        });
        lg.MyFavoriteRequest(mContext,0);
    }

    private void GetUserInfo(final Document fav) {
        final LoginGet loginget = new LoginGet();
        loginget.setFriendGetListener(new LoginGet.FriendGetListener() {

            @Override
            public void getFriends(ArrayList<Customer> cus_list) {
                // TODO Auto-generated method stub
                Customer cus = cus_list.get(0);
                ShareDocument(fav, cus.getUserID());

            }
        });
        loginget.FriendsRequest(mContext, targetId);
    }

    private void ShareDocument(final Document fa, final String useid) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "ShareDocument/Share?attachmentID=" + fa.getAttachmentID()
                                    + "&toShareUserID=" + useid
                                    + "&title=" + LoginGet.getBase64Password(fa.getTitle()), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.ShareDocument;
                        msg.obj = responsedata.getString("RetData");
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


}

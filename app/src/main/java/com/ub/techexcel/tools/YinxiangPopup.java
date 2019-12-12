package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.ub.techexcel.adapter.YinXiangAdapter;
import com.ub.techexcel.adapter.YinXiangAdapter2;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SoundtrackBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class YinxiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView close;
    private RelativeLayout backimg;
    private RecyclerView recycleview;
    private RecyclerView allrecycleview;
    private RelativeLayout ll1;
    private RelativeLayout ll2;
    private TextView selectmore;
    private TextView ok;
    private LinearLayout createyinxiang;
    private YinXiangAdapter yinXiangAdapter;
    private YinXiangAdapter2 yinXiangAdapter2;
    private List<SoundtrackBean> mlist = new ArrayList<>();
    private List<SoundtrackBean> allList = new ArrayList<>();
    private static FavoritePoPListener mFavoritePoPListener;
    private String attachmentid;
    private String lessonid;
    private ClipboardManager myClipboard;
    private LineItem currentDocument = new LineItem();

    public void setCurrentDocument(LineItem currentDocument) {
        if (currentDocument != null) {
            this.currentDocument = currentDocument;
        }
    }

    public interface ShareDocumentToFriendListener {
        void shareDocumentToFriend(SoundtrackBean soundtrackBean);
    }

    private ShareDocumentToFriendListener shareDocumentToFriendListener;


    public void setShareDocumentToFriendListener(ShareDocumentToFriendListener shareDocumentToFriendListener) {
        this.shareDocumentToFriendListener = shareDocumentToFriendListener;
    }


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void createYinxiang();

        void editYinxiang(SoundtrackBean soundtrackBean);

        void playYinxiang(SoundtrackBean soundtrackBean);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        myClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
        view = layoutInflater.inflate(R.layout.yinxiang_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        backimg = (RelativeLayout) view.findViewById(R.id.layout_back);

        ll1 = (RelativeLayout) view.findViewById(R.id.ll1);
        ll2 = (RelativeLayout) view.findViewById(R.id.ll2);
        selectmore = (TextView) view.findViewById(R.id.selectmore);
        selectmore.setOnClickListener(this);
        ok = (TextView) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        backimg.setOnClickListener(this);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        allrecycleview = (RecyclerView) view.findViewById(R.id.allrecycleview);
        createyinxiang = (LinearLayout) view.findViewById(R.id.createyinxiang);
        createyinxiang.setOnClickListener(this);

        recycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        allrecycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        yinXiangAdapter = new YinXiangAdapter(mContext, mlist);
        yinXiangAdapter.setFavoritePoPListener(new YinXiangAdapter.FavoritePoPListener() {
            @Override
            public void editYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                getgetSoundtrackItem(soundtrackBean, 1);
            }

            @Override
            public void deleteYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                deleteYinxiang2(soundtrackBean);
            }

            @Override
            public void playYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                getgetSoundtrackItem(soundtrackBean, 0);
            }

            @Override
            public void shareYinxiang(SoundtrackBean soundtrackBean) {

            }

            @Override
            public void copyUrl(SoundtrackBean soundtrackBean) {
                CopyLink(soundtrackBean);
            }

            @Override
            public void shareInApp(SoundtrackBean soundtrackBean) {
                if (shareDocumentToFriendListener != null) {
                    shareDocumentToFriendListener.shareDocumentToFriend(soundtrackBean);
                }
                dismiss();

//                InviteOthersPopup inviteOthersPopup = new InviteOthersPopup();
//                inviteOthersPopup.getPopwindow(mContext);
//                inviteOthersPopup.setFavoritePoPListener(new InviteOthersPopup.FavoritePoPListener() {
//                    @Override
//                    public void select(List<Customer> list) {
//                        SharedInAppPopup sharedInAppPopup = new SharedInAppPopup();
//                        sharedInAppPopup.getPopwindow(mContext);
//                        sharedInAppPopup.setFavoritePoPListener(new SharedInAppPopup.FavoritePoPListener() {
//                            @Override
//                            public void select(List<Customer> list) {
//
//                            }
//                        });
//                        sharedInAppPopup.StartPop(outView);
//                    }
//                });
//                inviteOthersPopup.StartPop(outView);
            }

            @Override
            public void sharePopup(final SoundtrackBean soundtrackBean) {
                MeetingYinxiangSharePopup joinMeetingPopup = new MeetingYinxiangSharePopup();
                joinMeetingPopup.getPopwindow(mContext);
                joinMeetingPopup.setFavoritePoPListener(new MeetingYinxiangSharePopup.FavoritePoPListener() {

                    @Override
                    public void weChat() {
                        weixingetUrl(currentDocument, soundtrackBean.getSoundtrackID(), true);
                    }

                    @Override
                    public void QQ() {
                        Toast.makeText(mContext, "待完善", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void peertime() {
                        if (shareDocumentToFriendListener != null) {
                            shareDocumentToFriendListener.shareDocumentToFriend(soundtrackBean);
                        }
                    }
                });
                joinMeetingPopup.StartPop(outView);
            }
        });
        recycleview.setAdapter(yinXiangAdapter);
        close.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
//        DisplayMetrics dm = new DisplayMetrics();
//        (((Activity)mContext).getWindowManager()).getDefaultDisplay().getRealMetrics(dm);
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);

    }

    private ClipboardManager mClipboard = null;

    private void CopyLink(SoundtrackBean soundtrackBean) {
        String url = AppConfig.SHARE_DOCUMENT + soundtrackBean.getSoundtrackID();
        if (soundtrackBean.getSoundtrackID() > 0) {
            url = AppConfig.SHARE_SYNC + soundtrackBean.getSoundtrackID();
        }
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        mClipboard.setPrimaryClip(ClipData.newPlainText(null, url));
        Toast.makeText(mContext, "Copy link success!", Toast.LENGTH_LONG).show();
    }


    public void getSoundtrack(final String attachmentid) {
        this.attachmentid = attachmentid;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("getSoundList", "getSoundListgetSoundList");
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        mlist.clear();
                        mlist.addAll(oo);
                        yinXiangAdapter.notifyDataSetChanged();
                    }
                }, isHidden, ishavepresenter);
    }


    public void getSoundtrack(final String attachmentid, final String lessonId) {
        this.attachmentid = attachmentid;
        this.lessonid = lessonId;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "LessonSoundtrack/List?lessonID=" + lessonId + "&attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        mlist.clear();
                        mlist.addAll(oo);
                        yinXiangAdapter.notifyDataSetChanged();
                    }
                }, isHidden, ishavepresenter);
    }


    public void getSoundtrack2(final String attachmentid) {
        this.attachmentid = attachmentid;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        allList.clear();
                        allList.addAll(oo);
                        yinXiangAdapter2 = new YinXiangAdapter2(mContext, mlist, allList);
                        allrecycleview.setAdapter(yinXiangAdapter2);
                    }
                }, isHidden, ishavepresenter);

    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (TextUtils.isEmpty(lessonid)) {
                            getSoundtrack(attachmentid);
                        } else {
                            getSoundtrack(attachmentid, lessonid);
                        }
                    }
                });
    }


    private boolean isHidden;
    private boolean ishavepresenter = true;
    private View outView;

    @SuppressLint("NewApi")
    public void StartPop(View v, String attachmentid, String lessonid, boolean isHidden, boolean ishavepresenter) {
        this.outView = v;
        yinXiangAdapter.setView(outView);
        if (mPopupWindow != null) {
            this.isHidden = isHidden;
            this.ishavepresenter = ishavepresenter;
            Log.e("eeeee", attachmentid + "   " + lessonid + "  " + isHidden + "  " + ishavepresenter);
            if (TextUtils.isEmpty(attachmentid)) {
                return;
            }

            mFavoritePoPListener.open();
            mPopupWindow.show();
            if (TextUtils.isEmpty(lessonid)) {
                getSoundtrack(attachmentid);
                selectmore.setVisibility(View.INVISIBLE);
            } else {
                getSoundtrack(attachmentid, lessonid);
                selectmore.setVisibility(View.VISIBLE);
            }
            if (isHidden) {
                createyinxiang.setVisibility(View.GONE);
            } else {
                createyinxiang.setVisibility(View.VISIBLE);
            }
            if (!ishavepresenter) {
                createyinxiang.setVisibility(View.GONE);
            }
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
            case R.id.close:
                dismiss();
                break;
            case R.id.createyinxiang:
                dismiss();
                mPopupWindow = null;
                mFavoritePoPListener.createYinxiang();
                break;
            case R.id.selectmore:
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                getSoundtrack2(attachmentid);
                break;
            case R.id.ok:
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
                String soundids = "";
                for (int i = 0; i < allList.size(); i++) {
                    SoundtrackBean soundtrackBean = allList.get(i);
                    if (soundtrackBean.isCheck()) {
                        soundids = soundids + soundtrackBean.getSoundtrackID() + ",";
                    }
                }
                if (!TextUtils.isEmpty(soundids)) {
                    addsoundtolesson(soundids.substring(0, soundids.length() - 1));
                }
                break;
            case R.id.layout_back:
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private void addsoundtolesson(final String soundtrackIDs) {
        String url = AppConfig.URL_PUBLIC + "LessonSoundtrack?lessonID=" + lessonid + "&soundtrackIDs=" + soundtrackIDs;
        ServiceInterfaceTools.getinstance().addSoundToLesson(url, ServiceInterfaceTools.ADDSOUNDTOLESSON,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (TextUtils.isEmpty(lessonid)) {
                            getSoundtrack(attachmentid);
                        } else {
                            getSoundtrack(attachmentid, lessonid);
                        }
                    }
                });
    }

    private void weixingetUrl(final LineItem document, final int id, final boolean st) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
                20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
//            String url = document.getSourceFileUrl();
        String url = document.getUrl();
        Log.e("url", url + "      " + document.getUrl());
        if (url.contains("<") && url.contains(">")) {
//                url = url.substring(0, url.lastIndexOf("<")) + "1_thumbnail" + url.substring(url.lastIndexOf("."), url.length());
            url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
        }
        Log.e("url", url + "      ");
//        final long s1 = System.currentTimeMillis();
        imageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error", error + "");
                weiXinShare(document, null, id, st);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                // TODO Auto-generated method stub
                if (response.getBitmap() != null) {
//                    long s2 = System.currentTimeMillis();
//                    Log.e("duang", response.getBitmap().getByteCount() + " : " + s1 + " : " + s2 + "  :   " + (s2 - s1));
                    weiXinShare(document, response.getBitmap(), id, st);
                }
            }
        });

    }


    private void weiXinShare(LineItem document, Bitmap b, int id, final boolean st) {
        String url = AppConfig.SHARE_DOCUMENT + document.getItemId();
        if (id > 0) {
            url = AppConfig.SHARE_SYNC + id;
        }
        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            final WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = document.getFileName();
            msg.description = "请点击此框跳转至" + url;
            Bitmap thumb = b;
            if (null == thumb) {
                thumb = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.logo);
            }
            final Bitmap finalThumb = thumb;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    msg.thumbData = bmpToByteArray(finalThumb, true);
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;
                    req.scene = st ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

                    WeiXinApi.getInstance().GetApi().sendReq(req);
                }
            }).start();
        } else {
            Toast.makeText(mContext, "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }


    public static byte[] bmpToByteArray(Bitmap bmp, final boolean needRecycle) {

        Log.e("hahahaTT", "  " + (null == bmp));

        bmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                Log.e("hahahaTT", "  " + arrayOfByte.length);
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }


    private void getgetSoundtrackItem(final SoundtrackBean soundtrackBean, final int type) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + soundtrackID,
                ServiceInterfaceTools.GETSOUNDITEM,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (type == 0) {
                            SoundtrackBean sou = (SoundtrackBean) object;
                            mFavoritePoPListener.playYinxiang(sou);
                        } else if (type == 1) {
                            SoundtrackBean sou2 = (SoundtrackBean) object;
                            mFavoritePoPListener.editYinxiang(sou2);
                        }
                    }
                });
    }

    public SoundtrackBean getNextSoundtrack(int soundtrackId) {
        if (mlist.size() < 0) {
            return null;
        }
        int currentIndex = -1;
        for (int i = 0; i < mlist.size(); ++i) {
            if (soundtrackId == mlist.get(i).getSoundtrackID()) {
                currentIndex = i;
                break;
            }
        }

        Log.e("current_index", "current_index:" + currentIndex);
        if (currentIndex != -1 && currentIndex < mlist.size() - 1) {
            return mlist.get(currentIndex + 1);
        }
        return null;
    }


}

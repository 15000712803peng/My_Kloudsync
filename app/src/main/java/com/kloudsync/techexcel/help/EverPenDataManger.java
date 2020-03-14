package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SharedPreferencesUtils;
import com.kloudsync.techexcel.tool.SyncWebNoteActionsCache;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.tqltech.tqlpencomm.Dot;
import com.ub.techexcel.bean.NewBookPagesBean;
import com.ub.techexcel.bean.NoteDotBean;
import com.ub.techexcel.bean.NoteInfoBean;
import com.ub.techexcel.bean.SyncNoteBean;
import com.ub.techexcel.bean.UploadNoteBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class EverPenDataManger {
    private static EverPenDataManger mManger;
    private EverPenManger mEverPenManger;
    private Activity mActivity;
    private ServiceInterfaceTools mRequsetTools;
    private final double B5_WIDTH = 119.44;
    private final double B5_HEIGHT = 168;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.UPLOADPENDATA:
                    List<NoteInfoBean.DataBean> noteInfoList = SharedPreferencesUtils.getList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, new TypeToken<List<NoteInfoBean.DataBean>>() {
                    });
                    Map<String, NoteDotBean> partWebActions = SyncWebNoteActionsCache.getInstance(App.getAppContext()).getPartWebActions();
                    if (partWebActions != null && partWebActions.size() != 0) {
                        //上传笔数据请求参数实体类
                        SyncNoteBean syncNoteBean = new SyncNoteBean();
                        syncNoteBean.setPeertimeToken(AppConfig.UserToken);
                        List<SyncNoteBean.BookPagesBean> bookPagesList = new ArrayList<>();
                        List<SyncNoteBean.DrawingDataBean> drawingDataList = new ArrayList<>();
                        //请求noteId相关信息请求参数实体类
                        NewBookPagesBean newBookPagesBean = new NewBookPagesBean();
                        newBookPagesBean.setPeertimeToken(AppConfig.UserToken);
                        List<NewBookPagesBean.BookPagesBean> bookPagesBeans = new ArrayList<>();

                        Date date = null;
                        try {
                            date = mSimpleDateFormat.parse(" 2010-01-01 00:00:00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Iterator<String> iterator = partWebActions.keySet().iterator();
                        while (iterator.hasNext()) {
                            String dotId = iterator.next();
                            NoteDotBean noteDotBean = partWebActions.get(dotId);
                            String uuid = noteDotBean.getDotId();
                            Dot dot = noteDotBean.getDot();
                            String address = dot.OwnerID + "." + dot.SectionID + "." + dot.BookID + "." + dot.PageID;
                            int eventType = 0;
                            switch (dot.type) {
                                case PEN_DOWN:
                                    eventType = 2;
                                    break;
                                case PEN_MOVE:
                                    eventType = 1;
                                    break;
                                case PEN_UP:
                                    //发送数据给web端
                                    break;
                            }
                            int force = dot.force * 20;
                            if (force == 0) {
                                //不处理
                            } else if (force < 500) {
                                force = 500;
                            } else if (force > 1200) {
                                force = 1200;
                            }
                            double dotX = Double.valueOf(dot.x + String.valueOf(dot.fx));
                            double dotY = Double.valueOf(dot.y + String.valueOf(dot.fy));
                            double x = dotX / B5_WIDTH * 5600;
                            double y = dotY / B5_HEIGHT * 7920;

                            if (noteInfoList.size() == 0) {
                                NewBookPagesBean.BookPagesBean pagesBean = new NewBookPagesBean.BookPagesBean();
                                pagesBean.setPageAddress(address);
                                pagesBean.setPenId(uuid);
                                if (!bookPagesBeans.contains(pagesBean)) {
                                    bookPagesBeans.add(pagesBean);
                                }
                            } else {
                                for (NoteInfoBean.DataBean dataBean : noteInfoList) {
                                    if (!dataBean.getAddress().equals(address)) {
                                        NewBookPagesBean.BookPagesBean pagesBean = new NewBookPagesBean.BookPagesBean();
                                        pagesBean.setPageAddress(address);
                                        pagesBean.setPenId(uuid);
                                        bookPagesBeans.add(pagesBean);
                                    }
                                }
                            }

//				            if (bookPagesList.size() == 0) {
                            for (NoteInfoBean.DataBean dataBean : noteInfoList) {
                                if (dataBean.getAddress().equals(address)) {
                                    SyncNoteBean.BookPagesBean bookPagesBean = new SyncNoteBean.BookPagesBean();
                                    bookPagesBean.setNoteId(dataBean.getNoteId());
                                    bookPagesBean.setFileId(dataBean.getFileId());
                                    bookPagesBean.setTargetFolderKey(dataBean.getTargetFolder());
                                    bookPagesBean.setPageAddress(address);
                                    if (!bookPagesList.contains(bookPagesBean)) {
                                        bookPagesList.add(bookPagesBean);
                                    }
                                }
                            }
                                            /*} else {
					            for (NoteInfoBean.DataBean dataBean : noteInfoList) {
						            if (!dataBean.getAddress().equals(address)) {
							            SyncNoteBean.BookPagesBean bookPagesBean = new SyncNoteBean.BookPagesBean();
							            bookPagesBean.setNoteId(dataBean.getNoteId());
							            bookPagesBean.setNoteId(dataBean.getNoteId());
							            bookPagesBean.setFileId(dataBean.getFileId());
							            bookPagesBean.setPageAddress(address);
							            bookPagesList.add(bookPagesBean);
						            }
					            }
				            }*/

                            SyncNoteBean.DrawingDataBean drawingDataBean = new SyncNoteBean.DrawingDataBean();
                            drawingDataBean.setAddress(address);
                            drawingDataBean.setUserID(AppConfig.UserID);
                            drawingDataBean.setEvent_type(eventType);
                            drawingDataBean.setForce(force);
                            drawingDataBean.setPoint_x(String.valueOf(x));
                            drawingDataBean.setPoint_y(String.valueOf(y));
                            drawingDataBean.setTimestamp(String.valueOf(date.getTime() + dot.timelong));
                            drawingDataBean.setPenID(mEverPenManger.getCurrentPen().getMacAddress());
                            if (eventType == 2) {
                                drawingDataBean.setStrokeID(uuid);
                            }
                            drawingDataList.add(drawingDataBean);

                        }
                        syncNoteBean.setBookPages(bookPagesList);
                        syncNoteBean.setDrawingData(drawingDataList);
                        newBookPagesBean.setBookPages(bookPagesBeans);

                        if (newBookPagesBean.getBookPages().size() > 0) {
                            requestNewBookPages(newBookPagesBean);
                        } else {
                            uploadDrawing(syncNoteBean);
                        }
                    }
                    sendEmptyMessageDelayed(AppConfig.UPLOADPENDATA, 5000);
                    break;
            }
        }
    };

    public EverPenDataManger(EverPenManger everPenManger, Activity activity) {
        mActivity = activity;
        mEverPenManger = everPenManger;
        mRequsetTools = ServiceInterfaceTools.getinstance();
    }

    public static EverPenDataManger getInstace(EverPenManger everPenManger, Activity activity) {
        if (mManger == null) {
            synchronized (EverPenDataManger.class) {
                if (mManger == null) {
                    mManger = new EverPenDataManger(everPenManger, activity);
                }
            }
        }
        return mManger;
    }

    public void sendHandlerMessage() {
        mHandler.sendEmptyMessage(AppConfig.UPLOADPENDATA);
    }

    public void removeHandlerMessage() {
        mHandler.removeMessages(AppConfig.UPLOADPENDATA);
    }

    public void cacheDotData(Dot dot) {
        String uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
        NoteDotBean noteDotBean = new NoteDotBean();
        noteDotBean.setDotId(uuid);
        noteDotBean.setDot(dot);
        SyncWebNoteActionsCache.getInstance(App.getAppContext()).cacheActions(noteDotBean);
    }

    public void requestNewBookPages(final NewBookPagesBean newBookPagesBean) {
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, NoteInfoBean>() {
            @Override
            public NoteInfoBean apply(String s) throws Exception {
                String path = AppConfig.URL_LIVEDOC + "newBookPages";
                return mRequsetTools.requestNewBookPages(path, newBookPagesBean.getPeertimeToken(), newBookPagesBean.getBookPages());
            }
        }).doOnNext(new Consumer<NoteInfoBean>() {
            @Override
            public void accept(final NoteInfoBean noteinfobean) throws Exception {
                if (noteinfobean != null) {
                    if (noteinfobean.isSuccess()) {
                        List<NoteInfoBean.DataBean> dataBeanList = noteinfobean.getData();
                        List<NoteInfoBean.DataBean> list = SharedPreferencesUtils.getList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, new TypeToken<List<NoteInfoBean.DataBean>>() {
                        });
                        for (NoteInfoBean.DataBean bean : dataBeanList) {
                            list.add(bean);
                        }
                        SharedPreferencesUtils.putList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, list);
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NoteInfoBean.ErrorBean error = noteinfobean.getError();
                                String errorMessage = error.getErrorMessage();
                                ToastUtils.show(mActivity, errorMessage);
                            }
                        });
                    }
                }

            }
        }).subscribe();
    }

    public void uploadDrawing(final SyncNoteBean syncNoteBean) {
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, UploadNoteBean>() {
            @Override
            public UploadNoteBean apply(String s) throws Exception {
                String path = AppConfig.URL_LIVEDOC + "uploadDrawing";
                return mRequsetTools.uploadDrawing(path, syncNoteBean.getPeertimeToken(), syncNoteBean.getBookPages(), syncNoteBean.getDrawingData());
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UploadNoteBean>() {
            @Override
            public void accept(final UploadNoteBean bean) throws Exception {
                if (bean != null) {
                    if (bean.isSuccess()) {

                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UploadNoteBean.ErrorBean error = bean.getError();
                                String errorMessage = error.getErrorMessage();
                                ToastUtils.show(mActivity, errorMessage);
                            }
                        });
                    }
                }
            }
        }).subscribe();
    }
}

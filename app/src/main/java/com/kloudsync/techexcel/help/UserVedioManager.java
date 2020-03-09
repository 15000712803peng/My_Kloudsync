package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.MeetingUserVedioCache;
import com.ub.techexcel.bean.PartWebActions;
import com.ub.techexcel.bean.SectionVO;
import com.ub.techexcel.bean.WebAction;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/21.
 */

public class UserVedioManager {

    private static boolean needRefresh = true;
    private static UserVedioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private UserVedioAdapter adapter;
    private ImageLoader imageLoader;
    private MeetingUserVedioCache userVedioCache;
    //
    private List<UserVedioData> userVedioDatas = new ArrayList<>();

    private UserVedioManager(Context context) {
        this.context = context;
        audioPlayer = new MediaPlayer();
        imageLoader = new ImageLoader(context);

    }

    public void setAdapter(RecyclerView userList) {
        if (adapter == null) {
            adapter = new UserVedioAdapter();
        }
        userList.setAdapter(adapter);
    }

    public static UserVedioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserVedioManager.class) {
                if (instance == null) {
                    instance = new UserVedioManager(context);
                }
            }
        }
        return instance;
    }

    public void saveUserVedios(final String userId, final List<SectionVO> vediosData) {
        UserVedioData _user = new UserVedioData(userId);
        int index = userVedioDatas.indexOf(_user);
        if (index >= 0) {
            Log.e("add_user_vedio", "set_user_vedio:" + userId);
            UserVedioData userVedioData = userVedioDatas.get(index);
            userVedioData.setVedios(vediosData);
//            adapter.notifyItemChanged(index);
        } else {
            _user.setVedios(vediosData);
            userVedioDatas.add(_user);
        }
        Log.e("userVedioDatas", userVedioDatas + "");

    }

    public void refreshUserInfo(final String userId, final String userName, final String avatarUrl) {
        Observable.just("on_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (userVedioDatas == null) {
                    userVedioDatas = new ArrayList<>();
                }
                UserVedioData _user = new UserVedioData(userId);
                _user.setAvatarUrl(avatarUrl);
                _user.setUserName(userName);
                int checkIndex = userVedioDatas.indexOf(_user);
                Log.e("refreshUserInfo", "check_index,index:" + checkIndex);
                if (checkIndex >= 0) {
                    Log.e("refreshUserInfo", "check_index_setCurrentVedio,index:" + checkIndex);
                    _user.setVedios(userVedioDatas.get(checkIndex).getVedios());
                }

                if (adapter != null) {
                    adapter.userEnter(_user);
//                    int index = adapter.getUserVedioDatas().indexOf(_user);
//                    if (index >= 0) {
//                        UserVedioData userVedioData = adapter.getUserVedioDatas().get(index);
//                        userVedioData.setAvatarUrl(avatarUrl);
//                        userVedioData.setUserName(userName);
//                        adapter.notifyItemChanged(index);
//                    } else {
//                        UserVedioData vedioData = new UserVedioData(userId);
//                        vedioData.setAvatarUrl(avatarUrl);
//                        vedioData.setUserName(userName);
//                        adapter.getUserVedioDatas().add(vedioData);
//                        adapter.notifyItemInserted(userVedioDatas.indexOf(vedioData));
//                    }
                }
            }
        });

    }

    SectionVO checkData;

    public void refreshUserInfo(final String userId, final String userName, final String avatarUrl, final int status) {
        Observable.just("on_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (userVedioDatas == null) {
                    userVedioDatas = new ArrayList<>();
                }
                UserVedioData _user = new UserVedioData(userId);
                _user.setAvatarUrl(avatarUrl);
                _user.setUserName(userName);
                int checkIndex = userVedioDatas.indexOf(_user);
                Log.e("refreshUserInfo", "check_index,index:" + checkIndex);
                if (checkIndex >= 0) {
                    Log.e("refreshUserInfo", "check_index_setCurrentVedio,index:" + checkIndex);
                    _user.setVedios(userVedioDatas.get(checkIndex).getVedios());
                }

                if (adapter != null) {
                    if (status == 1) {
                        adapter.userEnter(_user);
                    } else if (status == 0) {
                        adapter.userOut(_user);
                    }

//                    int index = adapter.getUserVedioDatas().indexOf(_user);
//                    if (index >= 0) {
//                        UserVedioData userVedioData = adapter.getUserVedioDatas().get(index);
//                        userVedioData.setAvatarUrl(avatarUrl);
//                        userVedioData.setUserName(userName);
//                        adapter.notifyItemChanged(index);
//                    } else {
//                        UserVedioData vedioData = new UserVedioData(userId);
//                        vedioData.setAvatarUrl(avatarUrl);
//                        vedioData.setUserName(userName);
//                        adapter.getUserVedioDatas().add(vedioData);
//                        adapter.notifyItemInserted(userVedioDatas.indexOf(vedioData));
//                    }
                }
            }
        });

    }


    public void setPlayTime(long playTime) {

        this.playTime = playTime;

        Log.e("check_play", "step_one");

        notifyShowVedioIfTimeComing();

//        SectionVO data = getNearestVedioData(playTime);
//
//        if (data == null) {
//            return;
//        }
//        Log.e("check_play", "step_two");
//
//        if (playTime < data.getStartTime() || playTime > data.getEndTime()) {
//            return;
//        }
//
//        Log.e("check_play", "step_three");
//
//        if (checkData != null && !checkData.equals(data) || checkData == null) {
//            needRefresh = true;
//        }
//
//        Log.e("check_play", "step_four:" + needRefresh);
//
//        if (needRefresh) {
//            if (adapter != null) {
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("UserVedioManager", "adapter_refresh");
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//            }
//            needRefresh = false;
//            checkData = data;
//        }


    }

    private void notifyShowVedioIfTimeComing() {

        if (adapter == null) {
            return;
        }
        for (final UserVedioData data : adapter.getUserVedioDatas()) {
            Log.e("notifyShowVedioIfTimeComing", "vedios:" + data.getVedios());
            if (data.getVedios() == null) {
                continue;
            }

            if (data.getVedios().size() > 0) {

                for (int i = 0; i < data.getVedios().size(); ++i) {
                    //4591,37302
                    final SectionVO sectionVO = data.getVedios().get(i);

                    long interval = playTime - sectionVO.getStartTime();
                    Log.e("notifyShowVedioIfTimeComing", "interval:" + interval);
                    if (interval > 0) {
//                        index = i;
                        if (playTime >= sectionVO.getStartTime() && playTime <= sectionVO.getEndTime()) {
                            if (sectionVO.isPlaying() || sectionVO.isPreparing() || data.showCameraVedio) {
                                continue;
                            }
                            data.setShowCameraVedio(true);
                            Log.e("check_user_camera", "notify_play");
                            Observable.just("load_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    if (adapter != null) {
                                        data.setCurrentVedio(sectionVO);
                                        adapter.notifyItemChanged(adapter.getUserVedioDatas().indexOf(data));
                                    }

                                }
                            });

                        } else {

                            if (data.isShowCameraVedio()) {
                                data.setShowCameraVedio(false);
                                Observable.just("load_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        if (adapter != null) {
                                            adapter.notifyItemChanged(userVedioDatas.indexOf(data));
                                        }

                                    }
                                });
                            }
                        }
                    }

                }
            }

        }

    }


    public void release() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }
        if (userVedioDatas != null) {
            userVedioDatas.clear();
            userVedioDatas = null;
        }

        adapter = null;
        checkData = null;
        instance = null;
    }

    public class UserVedioData {
        private int type;
        private String userId;
        private String userName;
        private String avatarUrl;
        private List<SectionVO> vedios;
        private boolean isPlaying;
        private boolean isPreparing;
        private boolean isPrepared;
        private MediaPlayer mediaPlayer;
        private boolean showCameraVedio;
        private SectionVO currentVedio;

        public SectionVO getCurrentVedio() {
            return currentVedio;
        }

        public void setCurrentVedio(SectionVO currentVedio) {
            this.currentVedio = currentVedio;
        }

        public boolean isShowCameraVedio() {
            return showCameraVedio;
        }

        public void setShowCameraVedio(boolean showCameraVedio) {
            this.showCameraVedio = showCameraVedio;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        public boolean isPrepared() {
            return isPrepared;
        }

        public void setPrepared(boolean prepared) {
            isPrepared = prepared;
        }

        public boolean isPreparing() {
            return isPreparing;
        }

        public void setPreparing(boolean preparing) {
            isPreparing = preparing;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public UserVedioData(String userId) {
            this.userId = userId;
        }

        public UserVedioData() {

        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public List<SectionVO> getVedios() {
            return vedios;
        }

        public void setVedios(List<SectionVO> vedios) {
            this.vedios = vedios;
        }

        @Override
        public String toString() {
            return "UserVedioData{" +
                    "userId='" + userId + '\'' +
                    ", vedios=" + vedios +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserVedioData that = (UserVedioData) o;

            return userId != null ? userId.equals(that.userId) : that.userId == null;
        }

        @Override
        public int hashCode() {
            return userId != null ? userId.hashCode() : 0;
        }
    }

    class UserVedioAdapter extends RecyclerView.Adapter<VedioHolder> {

        CopyOnWriteArrayList<UserVedioData> _userVedioDatas = new CopyOnWriteArrayList();

        public List<UserVedioData> getUserVedioDatas() {
            return _userVedioDatas;
        }

        public void userEnter(UserVedioData userVedioData) {
            if (_userVedioDatas.contains(userVedioData)) {
                return;
            }
            _userVedioDatas.add(userVedioData);
            Log.e("UserVedioAdapter", "userEnter:" + userVedioData);
            notifyItemInserted(_userVedioDatas.size());
        }

        public void userOut(UserVedioData userVedioData) {
            if (_userVedioDatas.contains(userVedioData)) {
                return;
            }
            int position = _userVedioDatas.indexOf(userVedioData);
            _userVedioDatas.remove(userVedioData);
            notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public VedioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vedio, parent, false);
            return new VedioHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VedioHolder holder, int position) {
            final UserVedioData vedioData = _userVedioDatas.get(position);
            holder.nameText.setText(vedioData.getUserName());
            if (TextUtils.isEmpty(vedioData.getAvatarUrl())) {
                holder.iconImage.setImageResource(R.drawable.hello);
            } else {
                imageLoader.DisplayImage(vedioData.getAvatarUrl(), holder.iconImage);
            }

            Log.e("onBindViewHolder", "vedio_data:" + vedioData);
            if (vedioData.getVedios() != null) {
                if (vedioData.isShowCameraVedio()) {
                    if (vedioData.getCurrentVedio() != null) {
                        Log.e("check_user_camera", "vedio_data_play");
                        holder.nameText.setVisibility(View.INVISIBLE);
                        holder.iconImage.setVisibility(View.INVISIBLE);
                        toPlay(vedioData, vedioData.getCurrentVedio(), holder.surface);
                    }

                } else {
                    holder.nameText.setVisibility(View.VISIBLE);
                    holder.iconImage.setVisibility(View.VISIBLE);
                    holder.surface.setBackgroundColor(Color.parseColor("#000000"));

                }
            } else {
                holder.nameText.setVisibility(View.VISIBLE);
                holder.iconImage.setVisibility(View.VISIBLE);
                holder.surface.setBackgroundColor(Color.parseColor("#000000"));
            }
        }

        @Override
        public int getItemCount() {
            return _userVedioDatas.size();
        }

        public void loadUserCameraVedio() {

        }
    }


    class VedioHolder extends RecyclerView.ViewHolder {
        SurfaceView surface;
        TextView nameText;
        ImageView iconImage;

        public VedioHolder(View itemView) {
            super(itemView);
            surface = itemView.findViewById(R.id.user_surface);
            nameText = itemView.findViewById(R.id.txt_name);
            iconImage = itemView.findViewById(R.id.member_icon);
        }
    }


    private SectionVO getNearestVedioData(long playTime, List<SectionVO> vedioDatas) {
        if (vedioDatas.size() > 0) {
            int index = 0;
            for (int i = 0; i < vedioDatas.size(); ++i) {
                //4591,37302
                long interval = vedioDatas.get(i).getStartTime() - playTime;
                if (interval > 0) {
                    index = i;
                    break;
                }

            }
            return vedioDatas.get(index);

        }
        return null;
    }

    private SectionVO getNearestVedioData(long playTime) {

        if (userVedioDatas.size() <= 0) {
            return null;
        }

        for (UserVedioData data : userVedioDatas) {
            int index = 0;
            if (data.getVedios() == null) {
                return null;
            }
            if (data.getVedios().size() > 0) {
                for (int i = 0; i < data.getVedios().size(); ++i) {
                    //4591,37302
                    long interval = data.getVedios().get(i).getStartTime() - playTime;
                    if (interval > 0) {
                        index = i;
                        break;
                    }

                }
            }
            return data.getVedios().get(index);
        }

        return null;
    }


    private void toPlay(final UserVedioData userVedioData, final SectionVO data, final SurfaceView surfaceView) {

        if (data == null) {
            return;
        }

        if (data.isPreparing() || userVedioData.isPreparing()) {
            return;
        }

        Log.e("toPlay", "vedio_data:" + data);

        Observable.just("play").observeOn(Schedulers.io()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                final MediaPlayer vedioPlayer = new MediaPlayer();
                try {
                    try {
                        data.setPreparing(true);
                        userVedioData.setPreparing(true);
                        data.setPrepared(true);
                        vedioPlayer.reset();
                        initSurface(surfaceView, vedioPlayer);
                        Uri uri = null;
                        if(userVedioCache.containFile(data.getFileUrl())){
                            File file = new File(userVedioCache.getVedioPath(data.getFileUrl()));
                            if(file.exists()){
                                Log.e("toPlay","to_play:" + file.getAbsolutePath());
                                vedioPlayer.setDataSource(file.getAbsolutePath());
                            }else {
                                vedioPlayer.setDataSource(context, Uri.parse(data.getFileUrl()));
                            }
                        }else {
                            vedioPlayer.setDataSource(context, Uri.parse(data.getFileUrl()));
                        }

                        vedioPlayer.prepare();
                        vedioPlayer.start();
                        Log.e("check_user_vedio","start");
                        vedioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Observable.just("play_complete").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        if(adapter != null){
                                            Log.e("check_user_vedio","onCompletion");
                                            userVedioData.setCurrentVedio(null);
                                            adapter.notifyItemChanged(adapter.getUserVedioDatas().indexOf(userVedioData));
                                        }
                                    }
                                });
                            }
                        });
                        data.setPreparing(false);
                        userVedioData.setPreparing(false);
                    } catch (IllegalStateException e) {

                    }

//                    vedioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            vedioPlayer.start();
//
//                            userVedioData.setPreparing(false);
//                            data.setPrepared(false);
//                            data.setPlaying(true);
//
//                        }
//                    });


                    vedioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            return false;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    userVedioData.setPreparing(false);
                }

            }
        });
    }


    public void initSurface(SurfaceView surfaceView, MediaPlayer mediaPlayer) {
        //给surfaceHolder设置一个callback
        surfaceView.setVisibility(View.VISIBLE);
        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);
        surfaceView.getHolder().addCallback(new SurfaceCallBack(mediaPlayer));
        surfaceView.setBackgroundColor(Color.parseColor("#00000000"));

    }

    private class SurfaceCallBack implements SurfaceHolder.Callback {

        MediaPlayer mediaPlayer;

        public SurfaceCallBack(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //调用MediaPlayer.setDisplay(holder)设置surfaceHolder，surfaceHolder可以通过surfaceview的getHolder()方法获得

            Log.e("WebVedioManager", "surfaceCreated");
            if (mediaPlayer != null) {
                mediaPlayer.setDisplay(holder);
            }
        }

        /**
         * 当SurfaceHolder的尺寸发生变化的时候被回调
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            release();
        }
    }

    public void predownLoadUserVedio(Context context) {
        userVedioCache = MeetingUserVedioCache.getInstance(context);
        FileUtils.createUserVediosFilesDir(context);
        List<String> urls = new ArrayList<>();
        if (userVedioDatas != null && userVedioDatas.size() > 0) {
            for (UserVedioData userVedioData : userVedioDatas) {
                if (userVedioData.getVedios() != null && userVedioData.getVedios().size() > 0) {
                    for (SectionVO sectionVO : userVedioData.getVedios()) {
                        if (!TextUtils.isEmpty(sectionVO.getFileUrl())) {
                            urls.add(sectionVO.getFileUrl());
                        }
                    }
                }
            }
        }

        if (urls.size() > 0) {
            Observable.fromArray(urls.toArray()).observeOn(Schedulers.io()).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object _url) throws Exception {
                    if (_url instanceof String) {
                        String url = (String) _url;
                        if (!TextUtils.isEmpty(url)) {
                            if (userVedioCache.containFile(url)) {
                                String path = userVedioCache.getVedioPath(url);
                                File localFile = new File(path);
                                if (localFile.exists()) {
                                    return;
                                } else {
                                    userVedioCache.removeFile(url);
                                }

                            }

                            String _path = FileUtils.getBaseUserVediosDir();
                            File dir = new File(_path);
                            String name = url.substring(url.lastIndexOf("/"),url.length());
                            File vedioFile = new File(dir,name);
                            Log.e("predownLoadUserVedio","vedioFile:" + vedioFile.getAbsolutePath());
                            safeDownloadFile(url,vedioFile.getAbsolutePath(),true);

                        }
                    }
                }
            }).subscribe();


        }
    }

    private synchronized void safeDownloadFile(final String url,final String savePath,final boolean needRedownload) {

        final ThreadLocal<String> localPage = new ThreadLocal<>();
        localPage.set(url);
        Log.e("safeDownloadFile","start_download");
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(),savePath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {

                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                userVedioCache.cacheVedio(localPage.get(),savePath);

            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(url, savePath, false);
                }
            }
        });
    }
}

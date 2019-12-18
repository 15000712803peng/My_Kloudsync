package com.ub.techexcel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.kloudsync.techexcel.help.MyVedioController;
import com.kloudsync.techexcel.tool.SocketMessageManager;

/**
 * Created by wang on 2018/6/21.
 */

public class ControllerVideoView extends VideoView {

    private MyVedioController vedioController;

    public void setVedioController(MyVedioController vedioController) {
        this.vedioController = vedioController;
    }

    public ControllerVideoView(Context context) {
        super(context);
    }

    public ControllerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        super.start();
        if(vedioController != null){
            vedioController.start();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if(vedioController != null){
            vedioController.pause();
        }
    }


    @Override
    public void seekTo(int pos) {
        super.seekTo(pos);
        if(vedioController != null){
            vedioController.seekTo(pos);
        }

    }


}
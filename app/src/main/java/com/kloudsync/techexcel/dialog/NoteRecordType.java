package com.kloudsync.techexcel.dialog;

public enum  NoteRecordType {

    DISPLAY_POPUP(300), //显示笔记到浮窗: (开始录的时候已经有笔记 前端记,后来有第一个笔记pengfei记)
    DISPLAY_POPUP_HOMEPAGE(301), //笔记从浮窗显示到主界面(前端)
    CHANGE_PAGE(302), //笔记换页(pengfei)
    CHANGE_LOCATION(303), //浮窗大小位置改变:(前端)
    DRAW_LINE(304), //划线(pengfei)
    CLOSE_POPUP_NOTE(305),//关闭浮窗笔记(前端)
    CLOSE_HOMEPAGE_NOTE(306),//关闭主界面笔记(前端)
    DISPLAY_HOME_POPUP(307),//笔记从主界面显示到浮窗(前端)
    DISPALY_HOMEPAGE(308);//直接显示笔记到主界面: (开始录的时候已经有笔记在主界面,前端记,后来有第一个笔记pengfei记)

    private final int actiontype;

    NoteRecordType(int actiontype) {
        this.actiontype = actiontype;
    }

    public int getActiontype() {
        return actiontype;
    }

}

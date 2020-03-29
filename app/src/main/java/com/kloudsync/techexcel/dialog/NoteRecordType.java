package com.kloudsync.techexcel.dialog;

public enum  NoteRecordType {

    DISPLAY_POPUP(300), //显示笔记到浮窗: (前端)
    DISPLAY_POPUP_HOMEPAGE(301), //笔记从浮窗显示到主界面(前端)
    CHANGE_PAGE(302), //笔记换页:(前端;meeting:1.笔在新一页写.2.手动换一个笔记)
    CHANGE_LOCATION(303), //浮窗大小位置改变:(前端,这个不做)
    DRAW_LINE(304), //划线(前端)
    CLOSE_POPUP_NOTE(305),//关闭浮窗笔记(前端)
    CLOSE_HOMEPAGE_NOTE(306),//关闭主界面笔记(前端)
    DISPLAY_HOMEPAGE_POPUP(307),//笔记从主界面显示到浮窗(前端)
    DISPALY_HOMEPAGE(308);//直接显示笔记到主界面: (前端,meeting:server)

    private final int actiontype;

    NoteRecordType(int actiontype) {
        this.actiontype = actiontype;
    }

    public int getActiontype() {
        return actiontype;
    }

}

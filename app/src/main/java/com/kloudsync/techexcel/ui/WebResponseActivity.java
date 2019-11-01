package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.BookNote;
import com.kloudsync.techexcel.tool.QueryLocalNoteTool;
import com.ub.service.activity.WatchCourseActivity3;

/**
 * Created by tonyan on 2019/11/1.
 */

public class WebResponseActivity extends Activity{

    TextView text;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_response);
        text = findViewById(R.id.txt);

        if(getIntent() != null){
            Uri uri = getIntent().getData();
            //完整路径
            String url = uri.toString();
            //authority
            String authority = uri.getAuthority(); //host:port
            //schema
            String schema = uri.getScheme();
            //host
            String host = uri.getHost();
            //port
            int port = uri.getPort();
            //path
            String path = uri.getPath();
            //query
            String query = uri.getQuery(); //queryParameter=queryString
            //param
            String noteId = uri.getQueryParameter("noteId");
            //最后组装
            String finalPath = schema+"://"+host+":"+port+"/"+path+"?"+query;
            text.setText("由Schema打开的笔记\n"+
                    "url="+url+"\n" + "noteId=" + noteId);
            if(!TextUtils.isEmpty(noteId)){
                openNote(noteId);
            }
        }
    }

    private void openNote(String noteId) {

        BookNote bookNote = null;
        if (TextUtils.isEmpty(noteId)) {
            bookNote = new BookNote().setTitle("new note").setJumpBackToNote(false);

        } else {
            bookNote = new BookNote().setDocumentId(noteId).setJumpBackToNote(false);
            if (!QueryLocalNoteTool.noteIsExist(this, noteId)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WebResponseActivity.this, "笔记在本地设备不存在", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }

        Intent intent = new Intent();
        intent.putExtra("OPEN_NOTE_BEAN", new Gson().toJson(bookNote));
        ComponentName comp = new ComponentName("com.onyx.android.note", "com.onyx.android.note.note.ui.ScribbleActivity");
        intent.setComponent(comp);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}

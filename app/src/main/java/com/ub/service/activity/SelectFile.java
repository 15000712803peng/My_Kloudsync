package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectFile extends Activity {

    private ListView listView;
    private TextView textView;
    // 记录当前父文件夹
    private File currentparent;
    private File[] currentfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectfile);
        // 获取列出全部文件的listviwe
        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.path);
        // 获取系统的SD卡目录
        File root = new File("/mnt/sdcard/");
        // 如果SD卡存在
        if (root.exists()) {
            currentparent = root;
            currentfiles = root.listFiles();
            // 使用当前目录下的全部文件 文件夹 来填充listview
            inflateListview(currentfiles);
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 用户单击了文件 直接返回 不做任何处理
                if (currentfiles[position].isFile()) {
                    File file = currentfiles[position];
                    Log.e("path", file.getAbsolutePath());
                    AppConfig.ISSUCCESS = true;
                    AppConfig.CURRENT_VALUES = file.getAbsolutePath();
                    finish();
                    overridePendingTransition(R.anim.tran_in6, R.anim.tran_out6);
                }
                File[] tmp = currentfiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {

                } else {
                    currentparent = currentfiles[position];
                    currentfiles = tmp;
                    inflateListview(currentfiles);
                }
            }
        });

        // 获取上一级目录的按钮
        Button button = (Button) findViewById(R.id.parent);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!currentparent.getCanonicalFile().equals("/mnt/sdcard")) {
                        // 获取上一级目录
                        currentparent = currentparent.getParentFile();
                        if (currentparent == null) {
                            finish();
                            overridePendingTransition(R.anim.tran_in6,
                                    R.anim.tran_out6);
                        } else {
                            // 列出当前目录下的所有文件
                            currentfiles = currentparent.listFiles();
                            // 再辞更新listview
                            inflateListview(currentfiles);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void inflateListview(File[] files) {
        // 创建一个listview集合 list集合的元素是map
        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> listitem = new HashMap<String, Object>();
            // 如果是文件夹 使用folder图标 否则使用file图标
            if (files[i].isDirectory()) { // 目录
                listitem.put("icon", R.drawable.folder);
            } else {
                listitem.put("icon", R.drawable.file);
            }
            listitem.put("filename", files[i].getName());
            listitems.add(listitem);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, listitems,
                R.layout.selectfile_tem, new String[]{"icon", "filename"},
                new int[]{R.id.icon, R.id.filename});
        listView.setAdapter(adapter);
        try {
            textView.setText(getString(R.string.currentpath) + currentparent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.tran_in6, R.anim.tran_out6);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

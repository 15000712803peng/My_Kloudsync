package com.kloudsync.techexcel.docment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FavouriteDocAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoriteDocumentsActivity extends BaseActivity implements View.OnClickListener, FavouriteDocAdapter.OnItemClickListener {

    ArrayList<Document> documents;
    FavouriteDocAdapter adapter;
    RelativeLayout backLayout;
    RecyclerView docList;
    int spaceID;

    @Override
    protected int setLayout() {
        return R.layout.activity_favorite_document;
    }

    @Override
    protected void initView() {
        documents = new ArrayList<>();
        adapter = new FavouriteDocAdapter(documents);
        spaceID = getIntent().getIntExtra("space_id", -1);
        backLayout = findViewById(R.id.layout_back);
        docList = findViewById(R.id.list_doc);
        docList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter.setOnItemClickListener(this);
        docList.setAdapter(adapter);
        backLayout.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDocuments();
    }

    private void getDocuments() {
        LoginGet get = new LoginGet();
        get.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {
                adapter.setDocuments(list);
            }
        });
        get.MyFavoriteRequest(this, 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();

        }
    }

    @Override
    public void onItemClick(Document favorite) {
        uploadFile(favorite);
    }

    private void uploadFile(final Document document) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/UploadFromFavorite?spaceID=" + spaceID
                                    + "&itemIDs=" + document.getItemID(), jsonObject);

                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        addSucc();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void addSucc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}

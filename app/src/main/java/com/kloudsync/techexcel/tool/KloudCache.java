package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.List;

public class KloudCache implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final SharedPreferences cachePreference;
    private final SharedPreferences userInfoPrefreence;
    private static KloudCache instance;
    Gson gson;
    public interface OnUserInfoChangedListener {
        void onUserInfoChanged(UserInCompany user);
    }

    OnUserInfoChangedListener onUserInfoChangedListener;

    private KloudCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_sync_cache", Context.MODE_PRIVATE);
        userInfoPrefreence = context.getSharedPreferences("kloud_sync_userinfo", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized KloudCache getInstance(Context context) {
        if (instance == null) {
            instance = new KloudCache(context);
        }
        return instance;
    }


    public void cacheSpaceList(List<TeamSpaceBean> spaceList) {
        if (spaceList == null || spaceList.isEmpty()) {
            cachePreference.edit().putString("space_list", "").commit();
            return;
        }
        cachePreference.edit().putString("space_list", new Gson().toJson(spaceList)).commit();
    }

    public List<TeamSpaceBean> getSpaceList() {
        String json = cachePreference.getString("space_list", "");
        if (TextUtil.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<List<TeamSpaceBean>>() {
        }.getType());
    }

    public void cacheDocList(List<Document> docList) {
        if (docList == null || docList.isEmpty()) {
            cachePreference.edit().putString("doc_list", "").commit();
            return;
        }
        cachePreference.edit().putString("doc_list", new Gson().toJson(docList)).commit();
    }

    public List<Document> getDocList() {
        String json = cachePreference.getString("doc_list", "");
        if (TextUtil.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<List<Document>>() {
        }.getType());
    }

    public void clear() {
        cachePreference.edit().putString("space_list", "").commit();
        cachePreference.edit().putString("doc_list", "").commit();
    }

    public void asyncCacheDocList(final List<Document> docList) {
        ThreadManager.getManager().execute(new Runnable() {
            @Override
            public void run() {
                cacheDocList(docList);
            }
        });
    }

    public void asyncCacheSpaceList(final List<TeamSpaceBean> spaceList) {
        ThreadManager.getManager().execute(new Runnable() {
            @Override
            public void run() {
                cacheSpaceList(spaceList);
            }
        });
    }

    public void deleteDoc(Document doc) {
        List<Document> docs = getDocList();
        if (docs != null && docs.size() > 0) {
            docs.remove(doc);
        }
        cacheDocList(docs);
    }

    public void deleteSpace(TeamSpaceBean space) {
        List<TeamSpaceBean> spaces = getSpaceList();
        if (spaces != null && spaces.size() > 0) {
            spaces.remove(space);
        }
        cacheSpaceList(spaces);
    }

    public void changeDocName(Document doc) {
        List<Document> docs = getDocList();
        if (docs != null && docs.size() > 0) {
            for (Document d : docs) {
                if (d.equals(doc)) {
                    d.setTitle(doc.getTitle());
                }
            }
        }
    }

    public void saveUserInCompany(String companyId, UserInCompany user) {
        if (user != null) {
            user.setCompanyID(companyId);
            user.setRondom(System.currentTimeMillis());
            userInfoPrefreence.edit().putString("user_in_company", new Gson().toJson(user)).commit();
        }
    }

    public UserInCompany getUserInfo() {
        String userJson = userInfoPrefreence.getString("user_in_company", "");
        if (!TextUtils.isEmpty(userJson)) {
            return gson.fromJson(userJson, UserInCompany.class);
        }
        return null;
    }

    public int getUserRole() {
        String userJson = userInfoPrefreence.getString("user_in_company", "");
        if (!TextUtils.isEmpty(userJson)) {
            return gson.fromJson(userJson, UserInCompany.class).getRole();
        }
        return -1;
    }

    public RoleInTeam getTeamRole() {
        String userJson = userInfoPrefreence.getString("user_in_company", "");
        if (!TextUtils.isEmpty(userJson)) {
            return gson.fromJson(userJson, UserInCompany.class).getRoleInTeam();
        }
        return new RoleInTeam();
    }

    public void registerUserInfoChangedListener(OnUserInfoChangedListener listener) {
        this.onUserInfoChangedListener = listener;
        userInfoPrefreence.registerOnSharedPreferenceChangeListener(this);
    }

    public void unregisterUserInfoChangedListener() {
        this.onUserInfoChangedListener = null;
        userInfoPrefreence.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences == userInfoPrefreence && key.equals("user_in_company")) {
            if (onUserInfoChangedListener != null) {
                onUserInfoChangedListener.onUserInfoChanged(gson.fromJson(userInfoPrefreence.getString("user_in_company", ""), UserInCompany.class));
            }
        }
    }
}

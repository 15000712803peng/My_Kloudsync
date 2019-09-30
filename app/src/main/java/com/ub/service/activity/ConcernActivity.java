package com.ub.service.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.CommonUsedAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DialogGetListener;
import com.umeng.analytics.MobclickAgent;

public class ConcernActivity extends Activity {

    private List<TextView> tvs = new ArrayList<TextView>();
    private TextView tv_set;
    private LinearLayout backll;
    private ListView lv_mused, lv_pop;
    private ScrollView sv_useful;
    private FrameLayout fl_pop;

    private int flag_se = -1;
    private int largeId = -1;
    private int smallId = -1;
    private int focusId = -1;
    private int tvIDs[] = {R.id.tv_all_l, R.id.tv_all_s};
    private ArrayList<CommonUse> main = new ArrayList<CommonUse>();

    private ArrayList<CommonUse> l_list = new ArrayList<CommonUse>();
    private ArrayList<CommonUse> s_list = new ArrayList<CommonUse>();
    private ArrayList<CommonUse> f_list = new ArrayList<CommonUse>();
    private ArrayList<CommonUse> show_list = new ArrayList<CommonUse>();

    private CommonUsedAdapter cAdapter;
    private CommonUsedAdapter ShowAdapter;

    float density;

    LoginGet loginget = new LoginGet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addservice_concern);
        initView();
    }

    private void initView() {
        backll = (LinearLayout) findViewById(R.id.backll);
        tv_set = (TextView) findViewById(R.id.tv_set);
        lv_mused = (ListView) findViewById(R.id.lv_mused);
        // lv_greet = (ListView) findViewById(R.id.lv_greet);
        lv_pop = (ListView) findViewById(R.id.lv_pop);
        sv_useful = (ScrollView) findViewById(R.id.sv_useful);
        fl_pop = (FrameLayout) findViewById(R.id.fl_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        for (int i = 0; i < tvIDs.length; i++) {
            TextView tv = (TextView) findViewById(tvIDs[i]);

            Drawable d;
            tv.setTextColor(getResources().getColor(R.color.darkgrey));
            d = getResources().getDrawable(R.drawable.select_d2);
            d.setBounds(0, 0, (int) (6 * density), (int) (6 * density));
            tv.setCompoundDrawables(null, null, d, null);
            tv.setOnClickListener(new myOnClick());
            tvs.add(tv);
        }
        backll.setOnClickListener(new myOnClick());
        tv_set.setOnClickListener(new myOnClick());

        GetConcernHierarchy();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ConcernActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ConcernActivity");
        MobclickAgent.onPause(this);
    }

    /**
     * 关注点的显示
     */
    private void showMused() {
        LayoutParams params = (LayoutParams) lv_mused.getLayoutParams();
        params.height = (int) (show_list.size() * 35 * density);
        ShowAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
        lv_mused.setAdapter(ShowAdapter);
        lv_mused.setOnItemClickListener(new MyShowItem());
    }

    /**
     * 得到关注点分类
     */
    private void GetConcernHierarchy() {
        loginget = new LoginGet();
        loginget.setDialogGetListener(new DialogGetListener() {
            @Override
            public void getUseful(ArrayList<CommonUse> list) {
                // TODO Auto-generated method stub
                showMused();
            }

            @Override
            public void getCH(ArrayList<CommonUse> list) {
                main = new ArrayList<CommonUse>();
                main.addAll(list);
                FilterList();
                if (l_list.size() > 0) {
                    GoTOSentence(0);
                }

            }
        });
        loginget.ConcernHierarchyRequest(ConcernActivity.this);
    }

    protected void FilterList() {
        l_list = new ArrayList<CommonUse>();
        s_list = new ArrayList<CommonUse>();
        f_list = new ArrayList<CommonUse>();
        for (int i = 0; i < main.size(); i++) {
            CommonUse cu = main.get(i);
            switch (cu.getNodeType()) {
                case 0: // 大类
                    l_list.add(cu);
                    break;
                case 1: // 小类
                    s_list.add(cu);
                    break;
                case 2: // 关注点
                    f_list.add(cu);
                    break;

                default:
                    break;
            }
        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_all_l:
                    GoTOSentence(0);
                    break;
                case R.id.tv_all_s:
                    if (largeId == -1) {
                        Toast.makeText(ConcernActivity.this, "请先选择大类",
                                Toast.LENGTH_LONG).show();
                    } else {
                        GoTOSentence(1);
                    }
                    break;
                case R.id.backll:
                    finish();
                    break;

                default:
                    break;
            }
        }

    }

    @SuppressLint("NewApi")
    public void GoTOSentence(int s) {
        for (int i = 0; i < tvs.size(); i++) {
            Drawable d;
            if (i == s && s != flag_se) {
                tvs.get(s).setTextColor(getResources().getColor(R.color.green));
                d = getResources().getDrawable(R.drawable.select_c2);
            } else {
                tvs.get(i).setTextColor(
                        getResources().getColor(R.color.darkgrey));
                d = getResources().getDrawable(R.drawable.select_d2);
            }
            d.setBounds(0, 0, (int) (6 * density), (int) (6 * density)); // 必须设置图片大小，否则不显示
            tvs.get(i).setCompoundDrawables(null, null, d, null);
        }

        if (s == flag_se) {
            ClosePop();
            flag_se = -1;
        } else {
            flag_se = s;
            ShowPop();
        }
        showTopList(s);

    }

    private void showTopList(int s) {
        CommonUse cu = new CommonUse();
        show_list = new ArrayList<CommonUse>();
        switch (s) {
            case 0:
                show_list = l_list;
                cAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
                cAdapter.SelectedItem(largeId);
                break;
            case 1:
                GetsmallList(cu);
                cAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
                cAdapter.SelectedItem(smallId);
                break;
            case 2:
            /*
				 * cAdapter = new CommonUsedAdapter(getApplicationContext(),
				 * show_list); cAdapter.SelectedItem(focusId);
				 */
                break;

            default:
                break;
        }
        lv_pop.setAdapter(cAdapter);
        lv_pop.setOnItemClickListener(new MyItem());
    }

    private class MyShowItem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            AppConfig.ISONRESUME = true;
            CommonUse cu = show_list.get(position);
            focusId = cu.getID();
            ShowAdapter.updateListView(show_list, position);
            AppConfig.tempServiceBean.setCategoryID(largeId);
            AppConfig.tempServiceBean.setSubCategoryID(smallId);
            AppConfig.tempServiceBean.setConcernID(focusId);
            AppConfig.tempServiceBean.setConcernName(cu.getName());
            finish();
        }

    }

    private class MyItem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            CommonUse cu = show_list.get(position);
            int ID = cu.getID();
            cAdapter.updateListView(show_list, ID);
            switch (flag_se) {
                case 0:
                    largeId = ID;
                    GoTOSentence(1);
                    break;
                case 1:
                    smallId = ID;
                    ClosePop();
                    GetFocusList(cu);
                    break;
                default:
                    break;
            }

        }

    }

    @SuppressLint("NewApi")
    private void ClosePop() {
        fl_pop.setVisibility(View.GONE);
        sv_useful.setAlpha(1.0f);
    }

    @SuppressLint("NewApi")
    private void ShowPop() {
        sv_useful.setAlpha(0.5f);
        fl_pop.setVisibility(View.VISIBLE);
    }

    private void GetFocusList(CommonUse cu) {
        show_list = new ArrayList<CommonUse>();
        for (int i = 0; i < s_list.size(); i++) {
            if (s_list.get(i).getID() == smallId) {
                cu = s_list.get(i);
                break;
            }
        }
        for (int i = 0; i < f_list.size(); i++) {
            if (smallId < 0) {
                show_list = f_list;
                break;
            }
            int id = f_list.get(i).getID();
            int length = (cu.getChildSelections() != null ? cu
                    .getChildSelections().length : 0);
            if (0 == length) {
                ClosePop();
                break;
            }
            for (int j = 0; j < length; j++) {
                if (id == cu.getChildSelections()[j]) {

                    show_list.add(f_list.get(i));

                }
            }
        }

        showMused();

    }

    private void GetsmallList(CommonUse cu) {
        for (int i = 0; i < l_list.size(); i++) {
            if (l_list.get(i).getID() == largeId) {
                cu = l_list.get(i);
                break;
            }
        }
        for (int i = 0; i < s_list.size(); i++) {
            if (largeId < 0) {
                show_list = s_list;
                break;
            }
            int id = s_list.get(i).getID();
            int length = (cu.getChildSelections() != null ? cu
                    .getChildSelections().length : 0);
            if (null == cu.getChildSelections()) {
                ClosePop();
                break;
            }
            for (int j = 0; j < length; j++) {
                if (id == cu.getChildSelections()[j]) {
                    show_list.add(s_list.get(i));
                }
            }
        }
    }

}

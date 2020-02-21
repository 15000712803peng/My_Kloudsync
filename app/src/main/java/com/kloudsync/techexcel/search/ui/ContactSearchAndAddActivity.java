package com.kloudsync.techexcel.search.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FriendContactAdapter;
import com.kloudsync.techexcel.adapter.SearchContactAdapter;
import com.kloudsync.techexcel.adapter.ViewHolder;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.ContactSearchData;
import com.kloudsync.techexcel.bean.EventFilterContact;
import com.kloudsync.techexcel.bean.FriendContact;
import com.kloudsync.techexcel.bean.SameLetterFriends;
import com.kloudsync.techexcel.bean.SearchContactInfo;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.PopFilterContact;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.search.view.VContactSearch;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.PingYinUtil;
import com.ub.techexcel.adapter.TeamAdapterV2;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import demo.Pinyin4jAppletDemo;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class ContactSearchAndAddActivity extends BaseActivity implements VContactSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener, SearchContactAdapter.OnItemClickListener {

    private ListView list;
    private ImageView backImage;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;
    SharedPreferences userPreferences;
    SearchResultAdapter adapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    List<Customer> customers;
    private List<SearchContactInfo> contactInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_contact_search_and_add;
    }

    @Override
    protected void initView() {
//        customers = (List<Customer>) getIntent().getExtras().getSerializable("customer_list");
        if (customers == null) {
            customers = new ArrayList<>();
        }
        list = findViewById(R.id.list);
//        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        backImage = findViewById(R.id.image_back);
        backImage.setOnClickListener(this);
        searchEdit = findViewById(R.id.et_search);
        searchEdit.addTextChangedListener(this);
        clearEditImage = findViewById(R.id.img_clear_edit);
        clearEditImage.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        loadingBar = findViewById(R.id.loading_progress);
        messageText = findViewById(R.id.txt_msg);

    }

    @Override
    public void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
        searchEdit.setEnabled(false);
    }

    @Override
    public void showEmpty(String message) {
        noDataLayout.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        list.setVisibility(View.GONE);
        messageText.setText(message);
    }

    @Override
    public void showContacts(List<Customer> conversations, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        adapter = new SearchResultAdapter();
        list.setVisibility(View.VISIBLE);
//        adapter.setKeyword(keyword);
//        adapter.setCustomers(conversations);
//        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                hideInput();
                finish();
                break;
            case R.id.img_clear_edit:
                searchEdit.setText("");
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(editRunnable);
        handler.postDelayed(editRunnable, 1000);
    }


    private Runnable editRunnable = new Runnable() {
        @Override
        public void run() {
            editCompleted();
        }
    };

    private void editCompleted() {
        searchStr = searchEdit.getText().toString().trim();
        if (TextUtils.isEmpty(searchStr)) {
            showEmpty("");
            return;
        }
        search(searchStr);
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void search(final String searchStr) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, Map<String, List<SearchContactInfo>>>() {
            @Override
            public Map<String, List<SearchContactInfo>> apply(String searchStr) throws Exception {
                String encodeStr = URLEncoder.encode(LoginGet.getBase64Password(searchStr), "UTF-8");
                Log.e("check_search", "encodeStr_1:" + encodeStr);
                if (!TextUtils.isEmpty(encodeStr) && encodeStr.endsWith("%0A")) {
                    encodeStr = encodeStr.substring(0, encodeStr.lastIndexOf("%0A"));
                }
                Log.e("check_search", "encodeStr_2:" + encodeStr);
                JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncSearchUserContact(encodeStr);
                allDatas.clear();
                contactsMap.clear();
                if (jsonObject.has("RetCode")) {
                    if (jsonObject.getInt("RetCode") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("RetData");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject contactData = jsonArray.getJSONObject(i);
                                SearchContactInfo contactInfo = new Gson().fromJson(contactData.toString(), SearchContactInfo.class);
                                if (!TextUtils.isEmpty(contactInfo.getUserName())) {
                                    String pinyin = PingYinUtil.getPingYin(contactInfo.getUserName());
                                    if (!TextUtils.isEmpty(pinyin)) {
                                        String fisrtChar = (pinyin.charAt(0) + "").toUpperCase();
                                        fillLetterContactToListDatas(fisrtChar, contactInfo);
                                    } else {
                                        fillLetterContactToListDatas("#", contactInfo);
                                    }
                                } else {
                                    fillLetterContactToListDatas("#", contactInfo);
                                }
                            }
                        }
                    }
                }

                return contactsMap;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Map<String, List<SearchContactInfo>>>() {
            @Override
            public void accept(Map<String, List<SearchContactInfo>> contactSearchData) throws Exception {
                searchEdit.setEnabled(true);
                loadingBar.setVisibility(View.GONE);
                handleResponse();
            }
        }).subscribe();
    }

    private void fillLetterContactToListDatas(String firstLetter, SearchContactInfo contact) {
        List<SearchContactInfo> contactInfos = null;
        if (contactsMap.containsKey(firstLetter)) {
            contactInfos = contactsMap.get(firstLetter);
            if (contactInfos == null) {
                contactInfos = new ArrayList<>();
            }
            contactInfos.add(contact);
        } else {
            contactInfos = new ArrayList<>();
            contactInfos.add(contact);
        }
        contactsMap.put(firstLetter, contactInfos);

    }

    private Map<String, List<SearchContactInfo>> contactsMap = new HashMap<>();

    List<LetterContactInfos> allDatas = new ArrayList<>();

    private void handleResponse() {
        allDatas.clear();
        if (contactsMap.size() > 0) {
            noDataLayout.setVisibility(View.GONE);
            Set<Map.Entry<String, List<SearchContactInfo>>> set = contactsMap.entrySet();
            Iterator<Map.Entry<String, List<SearchContactInfo>>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<SearchContactInfo>> entry = iterator.next();
                LetterContactInfos contactInfos = new LetterContactInfos();
                contactInfos.setLetter(entry.getKey());
                contactInfos.setContactInfos(entry.getValue());
                allDatas.add(contactInfos);
            }
            list.setVisibility(View.VISIBLE);
            list.setAdapter(new SearchResultAdapter());
        } else {
            noDataLayout.setVisibility(View.VISIBLE);
        }


    }

    private void handleResponse(List<Customer> customers, String keyword) {

        if (customers != null && customers.size() > 0) {
            showContacts(customers, keyword);
        } else {
            showEmpty(getString(R.string.no_data));
        }
    }

    @Override
    public void onItemClick(Team teamData) {

    }

    @Override
    public void onItemClick(int position, Customer customer) {
//        if (customer.isEnableChat()) {
//            AppConfig.Name = customer.getName();
//            AppConfig.isUpdateDialogue = true;
//            RongIM.getInstance().startPrivateChat(this,
//                    customer.getUBAOUserID(), customer.getName());
//        }
    }

    class SearchResultAdapter extends BaseAdapter {
        private static final int TYPE_CATEGORY_ITEM = 0;
        private static final int TYPE_ITEM = 1;

        @Override
        public int getCount() {
            int count = 0;
            if (null != allDatas) {
                //  所有分类中item的总和是ListVIew  Item的总个数
                for (LetterContactInfos letterFriends : allDatas) {
                    count += letterFriends.getItemCount();
                }
            }

            return count;
        }

        @Override
        public Object getItem(int position) {

            // 异常情况处理
            if (null == allDatas || position < 0 || position > getCount()) {
                return null;
            }

            // 同一分类内，第一个元素的索引值
            int categroyFirstIndex = 0;

            for (LetterContactInfos letterFriends : allDatas) {
                int size = letterFriends.getItemCount();
                // 在当前分类中的索引值
                int categoryIndex = position - categroyFirstIndex;
                // item在当前分类内
                if (categoryIndex < size) {
                    return letterFriends.getItem(categoryIndex);
                }
                // 索引移动到当前分类结尾，即下一个分类第一个元素索引
                categroyFirstIndex += size;
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            // 异常情况处理
            if (null == allDatas || position < 0 || position > getCount()) {
                return TYPE_ITEM;
            }

            int firstIndex = 0;
            for (LetterContactInfos letterFriends : allDatas) {
                int size = letterFriends.getItemCount();
                // 在当前分类中的索引值
                int categoryIndex = position - firstIndex;
                if (categoryIndex == 0) {
                    return TYPE_CATEGORY_ITEM;
                }
                firstIndex += size;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case TYPE_CATEGORY_ITEM:
                    TitleHolder titleHolder = null;
                    if (null == convertView) {
                        convertView = inflater.inflate(R.layout.letter_item, null);
                        titleHolder = new TitleHolder();
                        titleHolder.titleText = convertView.findViewById(R.id.tv_sort);
                        convertView.setTag(titleHolder);
                    } else {
                        titleHolder = (TitleHolder) convertView.getTag();
                    }
                    if (getItem(position) instanceof String) {
                        String title = (String) getItem(position);
                        titleHolder.titleText.setText(title);
                    }
                    break;

                case TYPE_ITEM:
                    ItemHolder itemHolder;
                    if (null == convertView) {
                        itemHolder = new ItemHolder();
                        convertView = inflater.inflate(R.layout.search_friend_contact_item, null);
                        itemHolder.img = convertView.findViewById(R.id.img_head);
                        itemHolder.name = convertView.findViewById(R.id.name);
                        itemHolder.phone = convertView.findViewById(R.id.txt_phone);
                        itemHolder.selectImage = convertView.findViewById(R.id.image_select);
                        convertView.setTag(itemHolder);
                    } else {
                        itemHolder = (ItemHolder) convertView.getTag();
                    }

                    if (getItem(position) instanceof SearchContactInfo) {
                        SearchContactInfo contactInfo = (SearchContactInfo) getItem(position);
                        String url = contactInfo.getAvatarUrl();
                        Uri imageUri = null;
                        if (!TextUtils.isEmpty(url)) {
                            imageUri = Uri.parse(url);
                        }
                        itemHolder.img.setImageURI(imageUri);
                        itemHolder.name.setText(contactInfo.getUserName());
                        itemHolder.phone.setText(contactInfo.getPhone());
                    }

                    break;
            }

            return convertView;
        }


        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) != TYPE_CATEGORY_ITEM;
        }


        LayoutInflater inflater;

        public SearchResultAdapter() {
            inflater = getLayoutInflater();
        }

    }

    class BaseHolder {

    }


    class ItemHolder extends BaseHolder {
        public SimpleDraweeView img;
        public TextView name;
        public ImageView selectImage;
        public TextView phone;
    }

    class SearchInfoResonse {
        private int RetCode;
        private List<SearchContactInfo> RetData;

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int retCode) {
            RetCode = retCode;
        }

        public List<SearchContactInfo> getRetData() {
            return RetData;
        }

        public void setRetData(List<SearchContactInfo> retData) {
            RetData = retData;
        }
    }


    class LetterContactInfos {
        String letter;
        List<SearchContactInfo> contactInfos;

        public Object getItem(int position) {
            // Category排在第一位
            if (position == 0) {
                return letter;
            } else {
                return contactInfos.get(position - 1);
            }
        }

        /**
         * 当前类别Item总数。Category也需要占用一个Item
         *
         * @return
         */
        public int getItemCount() {
            if (contactInfos == null || contactInfos.size() <= 0) {
                return 1;
            }
            return contactInfos.size() + 1;
        }

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
        }

        public List<SearchContactInfo> getContactInfos() {
            return contactInfos;
        }

        public void setContactInfos(List<SearchContactInfo> contactInfos) {
            this.contactInfos = contactInfos;
        }
    }

    class TitleHolder {
        public TextView titleText;
    }

    class ContactHolder {
        public SimpleDraweeView img;
        public TextView name;
    }


}

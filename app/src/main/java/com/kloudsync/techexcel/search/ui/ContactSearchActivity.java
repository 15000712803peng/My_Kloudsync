package com.kloudsync.techexcel.search.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FriendContactAdapter;
import com.kloudsync.techexcel.adapter.SearchContactAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.ContactSearchData;
import com.kloudsync.techexcel.bean.EventFilterContact;
import com.kloudsync.techexcel.bean.FriendContact;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.PopFilterContact;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.search.view.VContactSearch;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.adapter.TeamAdapterV2;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class ContactSearchActivity extends BaseActivity implements VContactSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener, SearchContactAdapter.OnItemClickListener {

    private ListView list;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;
    SharedPreferences userPreferences;
//    private SearchContactAdapter adapter;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);

    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_contact_search;
    }

    @Override
    protected void initView() {
//        customers = (List<Customer>) getIntent().getExtras().getSerializable("customer_list");
        if (customers == null) {
            customers = new ArrayList<>();
        }
        list = findViewById(R.id.list);
//        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cancelText = findViewById(R.id.tv_cancel);
        cancelText.setOnClickListener(this);
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
            case R.id.tv_cancel:
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
        handler.postDelayed(editRunnable, 600);
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
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, ContactSearchData>() {
            @Override
            public ContactSearchData apply(String searchStr) throws Exception {
                String encodeStr = URLEncoder.encode(LoginGet.getBase64Password(searchStr.trim()), "UTF-8");
                Log.e("check_search","encodeStr_1:" + encodeStr);
                if(!TextUtils.isEmpty(encodeStr) && encodeStr.endsWith("%0A")){
                    encodeStr = encodeStr.substring(0,encodeStr.lastIndexOf("%0A"));
                }
                Log.e("check_search","encodeStr_2:" + encodeStr);
                ContactSearchData contactSearchData = ServiceInterfaceTools.getinstance().syncSearchContact(userPreferences.getInt("SchoolID", -1),
                        userPreferences.getInt("contact_type", 1),encodeStr);
                if (contactSearchData == null) {
                    contactSearchData = new ContactSearchData();
                }
                return contactSearchData;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<ContactSearchData>() {
            @Override
            public void accept(ContactSearchData contactSearchData) throws Exception {
                searchEdit.setEnabled(true);
                loadingBar.setVisibility(View.GONE);
                handleResponse(contactSearchData);
            }
        }).subscribe();


//        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, List<Customer>>() {
//            @Override
//            public List<Customer> apply(String searchStr) throws Exception {
//                List<Customer> results = new ArrayList<>();
//                for (Customer customer : ContactSearchActivity.this.customers) {
//                    if (customer.getName().contains(searchStr)) {
//                        results.add(customer);
//                    }
//                }
//                return results;
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<Customer>>() {
//            @Override
//            public void accept(List<Customer> results) throws Exception {
//                handleResponse(results, searchStr);
//            }
//        }).subscribe();


    }

    List<FriendContact> allDatas = new ArrayList<>();
    private void handleResponse(ContactSearchData searchData){
        allDatas.clear();
        if(searchData == null){
            return;
        }
        List<FriendContact> myContactList = searchData.getMyContactList();
        if(myContactList != null && myContactList.size() > 0){
            FriendContact friendListtitle = new FriendContact();
            friendListtitle.setType(0);
            allDatas.add(friendListtitle);
            for(FriendContact contact : myContactList){
                contact.setType(1);
                allDatas.add(contact);
            }
        }

        List<FriendContact> companyContactList = searchData.getCompanyContactVOList();
        if(companyContactList != null && companyContactList.size() > 0){
            FriendContact friendListtitle = new FriendContact();
            friendListtitle.setType(2);
            allDatas.add(friendListtitle);
            for(FriendContact contact : companyContactList){
                contact.setType(1);
                allDatas.add(contact);
            }
        }
        list.setVisibility(View.VISIBLE);
        list.setAdapter(new SearchResultAdapter());


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
        if (customer.isEnableChat()) {
            AppConfig.Name = customer.getName();
            AppConfig.isUpdateDialogue = true;
            RongIM.getInstance().startPrivateChat(this,
                    customer.getUBAOUserID(), customer.getName());
        }
    }

    class SearchResultAdapter extends BaseAdapter{

        LayoutInflater inflater;
        public SearchResultAdapter(){
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return allDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return allDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            FriendContact friendContact = (FriendContact) getItem(position);
            return friendContact.getType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FriendContact friendContact = (FriendContact) getItem(position);
            int type = getItemViewType(position);
            BaseHolder baseHolder = null;
            if (null == convertView) {
                switch (type){
                    case 0:
                        convertView = inflater.inflate(R.layout.contact_title_one,null);
                        baseHolder = new TitleOneHolder();
                        ((TitleOneHolder)baseHolder).title = convertView.findViewById(R.id.txt_title);
                        ((TitleOneHolder)baseHolder).filterImage = convertView.findViewById(R.id.image_filter_contact);
                        break;
                    case 1:
                        convertView = inflater.inflate(R.layout.friend_contact_item,null);
                        baseHolder = new CommonItemHolder();

                        ((CommonItemHolder)baseHolder).img = convertView.findViewById(R.id.img_head);
                        ((CommonItemHolder)baseHolder).name = convertView.findViewById(R.id.name);
                        ((CommonItemHolder)baseHolder).chatImage = convertView.findViewById(R.id.img_chat);
                        break;
                    case 2:
                        convertView = inflater.inflate(R.layout.contact_title_two,null);
                        baseHolder = new TitleTwoHolder();
                        break;
                }
                convertView.setTag(baseHolder);
            }else {
                baseHolder = (BaseHolder) convertView.getTag();
                switch (type){
                    case 0:
                        TitleOneHolder oneHolder = (TitleOneHolder) baseHolder;
                        break;
                    case 1:
                        CommonItemHolder itemHolder = (CommonItemHolder) baseHolder;
                        break;
                    case 2:
                        TitleTwoHolder twoHolder = (TitleTwoHolder) baseHolder;
                        break;
                }
            }

            switch (type){
                case 0:
                    final TitleOneHolder oneHolder = (TitleOneHolder) baseHolder;
                    int filterType = userPreferences.getInt("contact_type",1);
                    Log.e("check_filter_type","filter_type:" + filterType);
                    if(filterType == 0){
                        oneHolder.title.setText("联系人(公司)");
                    }else if(type == 1){
                        oneHolder.title.setText("联系人(所有)");
                    }
                    oneHolder.filterImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFilterPop(oneHolder.filterImage);
                        }
                    });
                    break;
                case 1:
                    CommonItemHolder itemHolder = (CommonItemHolder) baseHolder;
                    String url = friendContact.getAvatarUrl();
                    Uri imageUri = null;
                    if (!TextUtils.isEmpty(url)) {
                        imageUri = Uri.parse(url);
                    }
                    itemHolder.img.setImageURI(imageUri);
                    itemHolder.name.setText(friendContact.getUserName());
                    if(friendContact.getStatus() == 1){
                        itemHolder.chatImage.setVisibility(View.VISIBLE);
                    }else {
                        itemHolder.chatImage.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    break;
            }

            return convertView;
        }
    }

    class BaseHolder{

    }

    class TitleOneHolder extends BaseHolder{
        public TextView title;
        public ImageView filterImage;
    }

    class TitleTwoHolder extends BaseHolder{
        public TextView title;

    }

    class CommonItemHolder extends BaseHolder{
        public SimpleDraweeView img;
        public TextView name;
        public ImageView chatImage;
    }

    PopFilterContact filterContactPop;
    private void showFilterPop(View view) {
        if (filterContactPop != null) {
            if (filterContactPop.isShowing()) {
                filterContactPop.dismiss();
            }
            filterContactPop = null;
        }

        filterContactPop = new PopFilterContact(this);
        filterContactPop.showAtBottom(view,userPreferences.getInt("contact_type",1));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void filterContact(EventFilterContact filterContact){
        Log.e("EventBus","filterContact");

    }


}

package com.ub.service.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventInviteUsers;
import com.kloudsync.techexcel.bean.Friend;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.MyFriend;

import com.kloudsync.techexcel.tool.PingYinUtil;

import com.ub.techexcel.tools.CharacterParser;
import com.ub.techexcel.tools.PinyinAddserviceComparator;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SiderIndex;
import com.ub.techexcel.view.SideBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wang on 2017/8/21.
 */

public class AddMeetingMemberActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private SideBar sideBar;
    private TextView dialog;
    private ListView listView;
    private FriendsAdapter myAdapter;
    private ProgressBar progressBar;

    private TextView done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addauditor);
        initView();
        requestMyFriends();
    }

    class FriendDatas{

        private List<MyFriend> friends;

        public List<MyFriend> getFriends() {
            return friends;
        }

        public void setFriends(List<MyFriend> friends) {
            this.friends = friends;
        }
    }



    private void requestMyFriends(){
        Observable.just(new FriendDatas()).observeOn(Schedulers.io()).doOnNext(new Consumer<FriendDatas>() {
            @Override
            public void accept(FriendDatas friendDatas) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetFrindList();
                if(result.has("RetCode")){
                    if(result.getInt("RetCode") == 0){
                        List<MyFriend> friends = new Gson().fromJson(result.getJSONArray("RetData").toString(), new TypeToken<List<MyFriend>>() {
                        }.getType());
                        if(friends != null && friends.size() > 0){

                            for(MyFriend friend : friends){
                                String sortLetter = null;
                                String name = friend.getName();
                                if (name == null
                                        || (name.length() > 0 && name.substring(0, 1)
                                        .equals(" ")) || name.equals("")) {
                                    sortLetter = "";
                                } else {
                                    sortLetter = PingYinUtil.getPingYin(name)
                                            .substring(0, 1).toUpperCase();
                                }
                                sortLetter = SideBarSortHelp.getAlpha(sortLetter);
                                friend.setSortLetters(sortLetter);
                            }
                            friendDatas.setFriends(friends);

                        }


                    }
                }

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<FriendDatas>() {
            @Override
            public void accept(FriendDatas friendDatas) throws Exception {
                if(friendDatas.getFriends() != null){
                    Log.e("doOnNext","doOnNext");
                    myAdapter = new FriendsAdapter(AddMeetingMemberActivity.this,friendDatas.getFriends()) ;
                    listView.setAdapter(myAdapter);
                }
            }
        }).subscribe();
    }


    private void initView() {
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        sideBar = (SideBar) findViewById(R.id.friends_sidrbar);
        listView = (ListView) findViewById(R.id.friends_myfriends);

        dialog = (TextView) findViewById(R.id.friends_dialog);
        progressBar = (ProgressBar) findViewById(R.id.pb_contacts);
        progressBar.setVisibility(View.GONE);
        sideBar.setTextView(dialog);
        sideBar.select(-1);
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                MyFriend bean = (MyFriend) myAdapter.getItem(arg2);
                if (bean.isSelected()) {
                    bean.setSelected(false);
                } else {
                    bean.setSelected(true);
                }
                myAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private String first;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (myAdapter != null) {
                    if (myAdapter.getlist() != null
                            && myAdapter.getlist().size() > 0) {
                        first = myAdapter.getlist().get(firstVisibleItem)
                                .getSortLetters();
                        int index = SiderIndex.stringtoint(first);
                        sideBar.select(index);
                    } else {
                        sideBar.select(-1);
                    }
                }
            }
        });

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (myAdapter != null) {
                    int position = myAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        listView.setSelection(position);
                    }
                }
            }
        });

    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();

    }


    List<MyFriend> friends;

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.backll:
                finish();
                break;
            case R.id.done:
                if(myAdapter != null){
                    if(!TextUtils.isEmpty(myAdapter.getSelectedFriends())){
                        EventInviteUsers inviteUsers = new EventInviteUsers();
                        inviteUsers.setUsers(myAdapter.getSelectedFriends());
                        EventBus.getDefault().post(inviteUsers);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }


    class FriendsAdapter extends BaseAdapter {

        private List<MyFriend> friends = null;
        private Context mContext;


        public FriendsAdapter(Context mContext, List<MyFriend> friends) {
            this.mContext = mContext;
            this.friends = friends;
        }

        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         *
         * @param friends
         */
        public void updateListView(List<MyFriend> friends) {
            this.friends = friends;
            notifyDataSetChanged();
        }

        public List<MyFriend> getlist() {
            if (friends != null && friends.size() > 0) {
                return friends;
            } else {
                return null;
            }
        }

        public String getSelectedFriends(){
            String userids = "";
            for(MyFriend friend : friends){
                if(friend.isSelected()){
                    userids += friend.getUserID() + ",";
                }
            }
            if(userids.endsWith(",")){
                userids = userids.substring(0,userids.length() - 1);
            }
            return userids;
        }

        public int getCount() {
            return friends.size();
        }

        public Object getItem(int position) {
            return friends.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup arg2) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.friends_item,
                        null);
                viewHolder.name = (TextView) view.findViewById(R.id.name); // 名字
                viewHolder.tvLetter = (TextView) view
                        .findViewById(R.id.item1_catalog);// 字母
                viewHolder.imageView = (SimpleDraweeView) view
                        .findViewById(R.id.image);
                viewHolder.img_selected = (ImageView) view.findViewById(R.id.img_selected);
                viewHolder.img_selected.setVisibility(View.VISIBLE);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            int position1 = position;
            final MyFriend friend = friends.get(position1);
            // 根据ListView的当前位置获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position1);
            if (position1 == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(friend.getSortLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            if (friend.isSelected()) {
                viewHolder.img_selected.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.select_b));
            } else {
                viewHolder.img_selected.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.unselect));
            }
            viewHolder.name.setText(friend.getName());
            String url = friend.getAvatarUrl();

            if (!TextUtils.isEmpty(url)) {
                Uri imageUri = Uri.parse(url);
                viewHolder.imageView.setImageURI(imageUri);
            }else {
                viewHolder.imageView.setImageResource(R.drawable.hello);
            }


            return view;
        }

        final class ViewHolder {
            TextView tvLetter; // 首字母
            TextView name;
            SimpleDraweeView imageView;
            ImageView img_selected;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值 == 与此时 当前位置 的字母的值相等
         */
        public int getSectionForPosition(int position) {
            return friends.get(position).getSortLetters().charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < friends.size(); i++) {
                String sortStr = friends.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 提取英文的首字母，非英文字母用#代替。
         *
         * @param str
         * @return
         */
        private String getAlpha(String str) {
            String sortStr = str.trim().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortStr.matches("[A-Z]")) {
                return sortStr;
            } else {
                return "#";
            }
        }

        public Object[] getSections() {
            return null;
        }


    }


}

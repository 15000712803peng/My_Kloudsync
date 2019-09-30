package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class TeamDocumentPopup {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private ListView listView;
    private View view;

    private TextView selectTeam, selectSpace;
    private TextView cancel, save;
    private ImageView addsavefile;
    DocumentAdapter documentAdapter;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private static FavoritePoPListener mFavoritePoPListener;


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void select(Document position, TeamSpaceBean teamName, TeamSpaceBean spaceName, List<Document> teamSpaceBeanFileList);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.popup_save_teamdocument, null);
        listView = (ListView) view.findViewById(R.id.listview);
        selectTeam = (TextView) view.findViewById(R.id.selectTeam);
        selectSpace = (TextView) view.findViewById(R.id.selectSpace);
        cancel = (TextView) view.findViewById(R.id.cancel);
        save = (TextView) view.findViewById(R.id.save);
        addsavefile = (ImageView) view.findViewById(R.id.addsavefile);
        addsavefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (documentAdapter != null) {
                    if (selectPosition != -1) {
                        mFavoritePoPListener.select(documentAdapter.getSelectFile(selectPosition), selectTeamBean, selectSpaceBean, teamSpaceBeanFileList);
                    }
                }
            }
        });
        selectTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                        TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                final List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                                final TeamSpacePopup teamSpacePopup = new TeamSpacePopup();
                                teamSpacePopup.getPopwindow(mContext, list);
                                teamSpacePopup.setFavoritePoPListener(new TeamSpacePopup.FavoritePoPListener() {
                                    @Override
                                    public void selectFavorite(int position) {
                                        teamSpacePopup.dismiss();
                                        selectTeam.setText(list.get(position).getName());
                                        selectTeamBean = list.get(position);
                                    }
                                });
                                teamSpacePopup.StartPop(viiw);
                            }
                        });
            }
        });

        selectSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTeamBean.getItemID() > 0) {
                    TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + selectTeamBean.getItemID(),
                            TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    final List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                                    final TeamSpacePopup teamSpacePopup = new TeamSpacePopup();
                                    teamSpacePopup.getPopwindow(mContext, list);
                                    teamSpacePopup.setFavoritePoPListener(new TeamSpacePopup.FavoritePoPListener() {
                                        @Override
                                        public void selectFavorite(int position) {
                                            teamSpacePopup.dismiss();
                                            selectSpace.setText(list.get(position).getName());
                                            selectSpaceBean = list.get(position);
                                            TeamSpaceInterfaceTools.getinstance().getSpaceDocumentList(AppConfig.URL_PUBLIC + "SpaceAttachment/List?spaceID=" + list.get(position).getItemID() + "&type=1&pageIndex=0&pageSize=20&searchText=",
                                                    TeamSpaceInterfaceTools.GETSPACEDOCUMENTLIST, new TeamSpaceInterfaceListener() {
                                                        @Override
                                                        public void getServiceReturnData(Object object) {
                                                            List<Document> list = (List<Document>) object;
                                                            teamSpaceBeanFileList.clear();
                                                            teamSpaceBeanFileList.addAll(list);
                                                            documentAdapter = new DocumentAdapter(mContext, list, R.layout.popup_video_item);
                                                            listView.setAdapter(documentAdapter);
                                                        }
                                                    });
                                        }
                                    });
                                    teamSpacePopup.StartPop(viiw);
                                }
                            });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectPosition = position;
                documentAdapter.notifyDataSetChanged();
            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private TeamSpaceBean selectTeamBean = new TeamSpaceBean();
    private TeamSpaceBean selectSpaceBean = new TeamSpaceBean();
    private List<Document> teamSpaceBeanFileList = new ArrayList<>();

    private View viiw;

    @SuppressLint("NewApi")
    public void StartPop(View v, TeamSpaceBean teamName, TeamSpaceBean spaceName, List<Document> teamSpaceBeanFileList2) {
        if (mPopupWindow != null) {
            this.viiw = v;
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            if (teamName != null) {
                selectTeam.setText(teamName.getName());
                selectTeamBean = teamName;
            }
            if (spaceName != null) {
                selectSpace.setText(spaceName.getName());
                selectSpaceBean = spaceName;
            }
            if (teamSpaceBeanFileList2.size() > 0) {
                teamSpaceBeanFileList.clear();
                teamSpaceBeanFileList.addAll(teamSpaceBeanFileList2);
                documentAdapter = new DocumentAdapter(mContext, teamSpaceBeanFileList2, R.layout.popup_video_item);
                listView.setAdapter(documentAdapter);
            }

        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.dismiss();
        }
    }

    int selectPosition = -1;

    public class DocumentAdapter extends BaseAdapter {
        private Context context;
        private List<Document> mDatas;
        private int itemLayoutId;

        public DocumentAdapter(Context context, List<Document> mDatas,
                               int itemLayoutId) {
            this.context = context;
            this.mDatas = mDatas;
            this.itemLayoutId = itemLayoutId;
        }


        public Document getSelectFile(int selectposition) {
            return mDatas.get(selectposition);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        itemLayoutId, null);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                holder.size = (TextView) convertView
                        .findViewById(R.id.filesize);
                holder.time = (TextView) convertView
                        .findViewById(R.id.totalTime);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(mDatas.get(position).getTitle());
            holder.time.setText("123kb");
            String create = mDatas.get(position).getCreatedDate();
            String createdate = "";
            if (!TextUtils.isEmpty(create)) {
                long dd = Long.parseLong(create);
                createdate = new SimpleDateFormat("yyyy.MM.dd").format(dd);
            }
            holder.size.setText(createdate);
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            return convertView;
        }


    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView size;
        ImageView imageview;
    }
}

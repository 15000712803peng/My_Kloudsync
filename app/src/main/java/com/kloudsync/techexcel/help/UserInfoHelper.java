package com.kloudsync.techexcel.help;

import android.content.Context;
import android.text.TextUtils;

import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.response.CanNullData;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.response.UserInCompanyResponse;
import com.kloudsync.techexcel.tool.KloudCache;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class UserInfoHelper {

    public static UserInCompany getUserInfoInCompany(final Context context, final String schoolID, final String userID, final String teamId) {

        UserInCompany userInCompany = new UserInCompany();
        Observable.just(userInCompany).observeOn(Schedulers.io()).map(new Function<UserInCompany, CanNullData<UserInCompany>>() {
            @Override
            public CanNullData<UserInCompany> apply(UserInCompany userInCompany) throws Exception {
                CanNullData<UserInCompany> data = new CanNullData<>();
                try {
                    Response<UserInCompanyResponse> response = ServiceInterfaceTools.getinstance().getUserInfoInCompany(schoolID, userID).execute();

                    if (response != null && response.isSuccessful()) {
                        data.setData(response.body().getRetData());
                    }

                } catch (UnknownHostException e) {
                    return data.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return data.setNull(true);
                }
                return data;
            }
        }).doOnNext(new Consumer<CanNullData<UserInCompany>>() {
            @Override
            public void accept(CanNullData<UserInCompany> data) throws Exception {
                if (!data.isNull()) {
                    if (!TextUtils.isEmpty(teamId)) {
                        try {
                            Response<TeamsResponse> response = ServiceInterfaceTools.getinstance().getCompanyTeams(schoolID).execute();
                            if (response != null && response.isSuccessful()) {
                                List<Team> teams = response.body().getRetData();
                                if (teams != null && teams.size() > 0) {
                                    for (Team team : teams) {
                                        if ((team.getItemID() + "").trim().equals(teamId)) {
                                            if (data != null && data.getData() != null) {
                                                data.getData().setRoleInTeam(team.getMemberType());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (SocketTimeoutException ex) {

                        }

                    }
                }
            }
        }).subscribe(new Consumer<CanNullData<UserInCompany>>() {
            @Override
            public void accept(CanNullData<UserInCompany> data) throws Exception {
                if (!data.isNull()) {
                    KloudCache.getInstance(context).saveUserInCompany(schoolID, data.getData());
                }
            }
        });
        return userInCompany;
    }


}

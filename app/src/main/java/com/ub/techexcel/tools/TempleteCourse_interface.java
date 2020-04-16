package com.ub.techexcel.tools;


import com.google.gson.JsonObject;
import com.kloudsync.techexcel.bean.DocumentData;
import com.kloudsync.techexcel.bean.DocumentDetail;
import com.kloudsync.techexcel.bean.FavoriteData;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.bean.SyncBook;
import com.kloudsync.techexcel.bean.params.AcceptFriendsRequestParams;
import com.kloudsync.techexcel.bean.params.AcceptInvitationsParams;
import com.kloudsync.techexcel.bean.params.InviteMultipleParams;
import com.kloudsync.techexcel.bean.params.InviteTeamAdminParams;
import com.kloudsync.techexcel.bean.params.InviteToCompanyParams;
import com.kloudsync.techexcel.info.MyFriend;
import com.kloudsync.techexcel.response.BindTvStatusResponse;
import com.kloudsync.techexcel.response.CompanyContactsResponse;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.response.FavoriteDocumentResponse;
import com.kloudsync.techexcel.response.FriendResponse;
import com.kloudsync.techexcel.response.InvitationsResponse;
import com.kloudsync.techexcel.response.InviteResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.response.OrganizationsResponse;
import com.kloudsync.techexcel.response.TeamAndSpaceSearchResponse;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.response.TeamSearchResponse;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.response.UserInCompanyResponse;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by wang on 2018/5/23.
 */

public interface TempleteCourse_interface {

    @GET("Course/List")
    Call<ResponseBody> getCourseByTemplete(@Header("UserToken") String userToken, @Query("listType") int listType,
                                           @Query("type") int type,
                                           @Query("templateType") int templateType,
                                           @Query("TeacherID") int TeacherID,
                                           @Query("sortBy") int sortBy,
                                           @Query("order") int order,
                                           @Query("pageIndex") int pageIndex,
                                           @Query("pageSize") int pageSize
    );

    @GET("Course/List")
    Call<ResponseBody> getCourseByTemplete2(@Header("UserToken") String userToken, @Query("listType") int listType,
                                            @Query("type") int type,
                                            @Query("templateType") int templateType,
                                            @Query("SchoolID") int SchoolID,
                                            @Query("sortBy") int sortBy,
                                            @Query("order") int order,
                                            @Query("pageIndex") int pageIndex,
                                            @Query("pageSize") int pageSize
    );


    @GET("TopicAttachment/List")
    Call<ResponseBody> getAllDocument(@Header("UserToken") String userToken, @Query("topicID") int topicID);


    @GET("MeetingServer/member/join_role")
    Call<ResponseBody> getJoinRole(@Header("UserToken") String userToken, @Query("meetingId") String meetingId);


    @GET("Course/List")
    Observable<ResponseBody> getCourseByRxJava(@Header("UserToken") String userToken, @Query("listType") int listType,
                                               @Query("type") int type,
                                               @Query("templateType") int templateType,
                                               @Query("SchoolID") int SchoolID,
                                               @Query("TeacherID") int TeacherID,
                                               @Query("sortBy") int sortBy,
                                               @Query("order") int order,
                                               @Query("pageIndex") int pageIndex,
                                               @Query("pageSize") int pageSize
    );

    @GET("TeamSpace/SearchSpaceAndDocument")
    Call<TeamAndSpaceSearchResponse> searchSpacesAndDocs(@Header("UserToken") String userToken, @Query("companyID") int companyID,
                                                         @Query("teamID") int teamID, @Query("keyword") String keyword);

    @GET("TeamSpace/List")
    Call<TeamSearchResponse> searchTeams(@Header("UserToken") String userToken, @Query("companyID") int companyID,
                                         @Query("type") int type, @Query("keyword") String keyword);

    @GET("TeamSpace/List")
    Call<NetworkResponse<List<TeamSpaceBean>>> searchSpaces(@Header("UserToken") String userToken, @Query("companyID") int companyID,
                                                            @Query("type") int type, @Query("parentID") String parentID, @Query("keyword") String keyword);

    @POST("Invite/InviteNewToCompany")
    Call<ResponseBody> inviteNewToSpace(@Header("UserToken") String userToken, @Query("CompanyID") int companyID, @Query("Mobile") String mobile,
                                        @Query("InviteToType") int inviteToType, @Query("RequestAddFriend") int requestAddFriend);

    @POST("Invite/InviteNewToCompany")
    Call<InviteResponse> inviteNewToCompany(@Header("UserToken") String userToken, @Body InviteToCompanyParams params);

    @GET("Invite/CompanyInvitations")
    Call<InvitationsResponse> getInvitations(@Header("UserToken") String userToken);

    @POST("Invite/AcceptInvitations")
    Call<NetworkResponse> acceptInvitations(@Header("UserToken") String userToken, @Body AcceptInvitationsParams paras);

    @GET("Invite/FriendRequest")
    Call<FriendResponse> friendRequest(@Header("UserToken") String userToken, @Query("CompanyID") int companyID);

    @POST("Invite/AcceptFriendRequest")
    Call<NetworkResponse> acceptFriendsRequest(@Header("UserToken") String userToken, @Body AcceptFriendsRequestParams params);

    @GET("School/UserSchoolList")
    Call<OrganizationsResponse> searchOrganizations(@Header("UserToken") String userToken, @Query("keyword") String keyword);

    @GET("SchoolContact/Item")
    Call<UserInCompanyResponse> getUserInfoInCompany(@Header("UserToken") String userToken, @Query("schoolID") String schoolID, @Query("userID") String userID);

    @GET("TeamSpace/List")
    Call<TeamsResponse> getCompanyTeams(@Header("UserToken") String userToken, @Query("type") int type, @Query("companyID") String companyID);

    @GET("TeamSpace/List")
    Call<TeamsResponse> getAllTeams(@Header("UserToken") String userToken, @Query("type") int type, @Query("companyID") String companyID,
                                    @Query("showAll") int showAll);

    @GET("TeamSpace/List")
    Call<TeamsResponse> getAllSpaces(@Header("UserToken") String userToken, @Query("type") int type, @Query("companyID") String companyID,
                                     @Query("parentID") String parentID, @Query("showAll") int showAll);

    @GET("TeamSpace/MemberList")
    Call<TeamMembersResponse> getTeamMembers(@Header("UserToken") String userToken, @Query("TeamSpaceID") String teamSpaceID);

    @GET("TeamSpace/SearchContact")
    Call<CompanyContactsResponse> searchCompanyContactInTeam(@Header("UserToken") String userToken, @Query("companyID") String companyID,
                                                             @Query("spaceID") String teamID, @Query("keyword") String keyword);

    @POST("Invite/InviteCompanyMemberAsTeamAdmin")
    Call<NetworkResponse> inviteCompanyMemberAsTeamAdmin(@Header("UserToken") String userToken, @Body InviteTeamAdminParams params);

    @GET("TeamSpace/MemberList")
    Call<TeamMembersResponse> getSpaceMembers(@Header("UserToken") String userToken, @Query("TeamSpaceID") String teamSpaceID);

    @GET("SpaceAttachment/List")
    Call<NetworkResponse<DocumentDetail>> searchDocumentsInSpace(@Header("UserToken") String userToken, @Query("type") int type, @Query("spaceID") String spaceId,
                                                                 @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize, @Query("searchText") String searchText);

    @GET("Login")
    Call<NetworkResponse<LoginData>> login(@Query("login") String login, @Query("password") String password, @Query("role") int role,
                                           @Query("deviceID") String deviceID, @Query("deviceType") int deviceType, @Query("DeviceName") String DeviceName);

    @GET("RongCloudUserToken")
    Call<NetworkResponse<RongCloudData>> getRongCloudInfo(@Header("UserToken") String userToken);

    @GET("RongCloud/OnlineStatus")
    Call<NetworkResponse<Integer>> getRongCloudOnlineStatus(@Header("UserToken") String userToken);

    @GET("Friend/FriendList")
    Call<NetworkResponse<List<MyFriend>>> getFriendList(@Header("UserToken") String userToken);

    @GET("FavoriteAttachment/MyFavoriteAttachments")
    Call<NetworkResponse<FavoriteData>> searchFavoriteDocuments(@Header("UserToken") String userToken, @Query("type") int type, @Query("schoolID") String schoolID,
                                                                @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize, @Query("searchText") String searchText);

    @GET("FavoriteAttachment/MyFavoriteAttachmentsNew")
    Call<NetworkResponse<FavoriteDocumentResponse>> getFavoriteDocuments(@Header("UserToken") String userToken, @Query("type") int type);

    @GET("SpaceAttachment/TeamDocumentList")
    Call<NetworkResponse<DocumentData>> getAllDocumentList(@Header("UserToken") String userToken, @Query("companyID") String companyID, @Query("teamID") String teamID,
                                                           @Query("type") int type, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    @POST("Invite/InviteMultipleNewToCompany")
    Call<NetworkResponse> inviteMultipleToCompany(@Header("UserToken") String userToken, @Body InviteMultipleParams params);

    @GET("SpaceAttachment/List")
    Call<NetworkResponse<DocumentDetail>> searchHelpDocuments(@Header("UserToken") String userToken, @Query("spaceID") String spaceId, @Query("type") int type, @Query("searchText") String searchText);

    @GET()
    Call<DevicesResponse> getBindTvs(@Url String url, @Header("UserToken") String userToken);

    @GET()
    Call<JsonObject> getAppNames(@Url String url, @Header("UserToken") String userToken);

   @POST()
    Call<BindTvStatusResponse> changeBindTvStatus(@Url String url, @Header("UserToken") String userToken, @Query("status") int status);

//    https://api.peertime.cn/peertime/V1/SyncRoom/GetSyncBookOutline?syncroomID=1895736

    @GET("SyncRoom/GetSyncBookOutline")
    Call<NetworkResponse<SyncBook>> getSyncbookOutline(@Header("UserToken") String userToken, @Query("syncroomID") String syncroomID);

    @GET()
    Call<ResponseBody> getBindTvs2(@Url String url, @Header("UserToken") String userToken);

    @GET
    Call<ResponseBody> getUserPreference(@Header("UserToken") String userToken);

    @GET()
    Call<ResponseBody> getLessionIdByItemId(@Url String url, @Header("UserToken") String userToken);

}

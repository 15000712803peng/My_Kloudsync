package com.kloudsync.techexcel.tool;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventMeetingDocuments;
import com.kloudsync.techexcel.bean.EventRefreshDocs;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyan on 2019/11/20.
 */

public class DocumentModel {

    public static void asyncGetDocumentsInDocAndShowPage(final MeetingConfig meetingConfig, final boolean needShow) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject response = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + meetingConfig.getLessionId());
                Log.e("getDocuments","Lesson/Item?lessonID=" + meetingConfig.getLessionId() + ",result:" + response);
                if (response == null) {

                } else {
                    try {
                        JSONObject retData = response.getJSONObject("RetData");
                        Log.e("asyncGetDocumentDetail", "ret data:" + retData);
                        if (retData != null) {
                            JSONArray array = retData.getJSONArray("AttachmentList");
                            if (array != null) {
                                Gson gson = new Gson();
                                Log.e("array", "array:" + array);
                                List<MeetingDocument> documents = new ArrayList<>();
                                for (int i = 0; i < array.length(); ++i) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    MeetingDocument document = gson.fromJson(jsonObject.toString(), MeetingDocument.class);
                                    String attachmentUrl = document.getAttachmentUrl();
                                    String preUrl = "";
                                    String endUrl = "";
                                    if (!TextUtils.isEmpty(attachmentUrl)) {
                                        int index = attachmentUrl.lastIndexOf("<");
                                        int index2 = attachmentUrl.lastIndexOf(">");
                                        if (index > 0) {
                                            preUrl = attachmentUrl.substring(0, index);
                                        }
                                        if (index2 > 0) {
                                            endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                                        }
                                    }

                                    List<DocumentPage> pages = new ArrayList<>();
                                    for (int j = 0; j < document.getPageCount(); ++j) {
                                        String pageUrl = "";
                                        DocumentPage  page= new DocumentPage();
                                        page.setPageNumber(j + 1);
                                        page.setDocumentId(document.getItemID());
                                        if (TextUtils.isEmpty(preUrl)) {
                                            page.setPageUrl(pageUrl);
                                        } else {
                                            page.setPageUrl(preUrl + (j + 1)+"_4K" + endUrl);
                                        }
                                        pages.add(page);
                                    }
                                    document.setDocumentPages(pages);
                                    documents.add(document);
                                }
                                EventMeetingDocuments meetingDocuments = new EventMeetingDocuments();
                                meetingDocuments.setDocuments(documents);
                                if(needShow){
                                    EventBus.getDefault().post(meetingDocuments);
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start(ThreadManager.getManager());
    }

    public static JSONObject syncQueryDocumentInDoc(final String url, final String newPath) {
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            jsonObject.put("Key", keyJson);
            JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
            Log.e("syncQueryDocumentInDoc","url:" + url + ",returnjson:" + returnjson);
            return returnjson;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void asyncGetDocumentsInDocAndRefreshFileList(final MeetingConfig meetingConfig, final int itemId, final int pageNumber) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject response = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + meetingConfig.getLessionId());
                if (response == null) {

                } else {
                    try {
                        JSONObject retData = response.getJSONObject("RetData");
                        Log.e("asyncGetDocumentDetail", "ret data:" + retData);
                        if (retData != null) {
                            JSONArray array = retData.getJSONArray("AttachmentList");
                            if (array != null) {
                                Gson gson = new Gson();
                                Log.e("array", "array:" + array);
                                List<MeetingDocument> documents = new ArrayList<>();
                                for (int i = 0; i < array.length(); ++i) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    MeetingDocument document = gson.fromJson(jsonObject.toString(), MeetingDocument.class);
                                    String attachmentUrl = document.getAttachmentUrl();
                                    String preUrl = "";
                                    String endUrl = "";
                                    if (!TextUtils.isEmpty(attachmentUrl)) {
                                        int index = attachmentUrl.lastIndexOf("<");
                                        int index2 = attachmentUrl.lastIndexOf(">");
                                        if (index > 0) {
                                            preUrl = attachmentUrl.substring(0, index);
                                        }
                                        if (index2 > 0) {
                                            endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                                        }
                                    }

                                    List<DocumentPage> pages = new ArrayList<>();
                                    for (int j = 0; j < document.getPageCount(); ++j) {
                                        String pageUrl = "";
                                        DocumentPage  page= new DocumentPage();
                                        page.setPageNumber(j + 1);
                                        page.setDocumentId(document.getItemID());
                                        if (TextUtils.isEmpty(preUrl)) {
                                            page.setPageUrl(pageUrl);
                                        } else {
                                            page.setPageUrl(preUrl + (j + 1)+"_4K" + endUrl);
                                        }
                                        pages.add(page);
                                    }
                                    document.setDocumentPages(pages);
                                    documents.add(document);
                                }
                                EventRefreshDocs refreshDocs = new EventRefreshDocs();
                                refreshDocs.setPageNumber(pageNumber);
                                refreshDocs.setItemId(itemId);
                                refreshDocs.setRefresh(true);
                                refreshDocs.setDocuments(documents);
                                EventBus.getDefault().post(refreshDocs);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start(ThreadManager.getManager());
    }

    public static EventRefreshDocs syncGetDocumentsInDocAndRefreshFileList(final MeetingConfig meetingConfig,final int itemId) {

        EventRefreshDocs refreshDocs = new EventRefreshDocs();
            JSONObject response = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + meetingConfig.getLessionId());
            if (response == null) {

            } else {

                try {
                    JSONObject retData = response.getJSONObject("RetData");
                    Log.e("syncGetDocumentDetail", "ret data:" + retData);
                    if (retData != null) {
                        JSONArray array = retData.getJSONArray("AttachmentList");
                        if (array != null) {
                            Gson gson = new Gson();
                            Log.e("array", "array:" + array);
                            List<MeetingDocument> documents = new ArrayList<>();
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                MeetingDocument document = gson.fromJson(jsonObject.toString(), MeetingDocument.class);
                                String attachmentUrl = document.getAttachmentUrl();
                                String preUrl = "";
                                String endUrl = "";
                                if (!TextUtils.isEmpty(attachmentUrl)) {
                                    int index = attachmentUrl.lastIndexOf("<");
                                    int index2 = attachmentUrl.lastIndexOf(">");
                                    if (index > 0) {
                                        preUrl = attachmentUrl.substring(0, index);
                                    }
                                    if (index2 > 0) {
                                        endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                                    }
                                }

                                List<DocumentPage> pages = new ArrayList<>();
                                for (int j = 0; j < document.getPageCount(); ++j) {
                                    String pageUrl = "";
                                    DocumentPage  page= new DocumentPage();
                                    page.setPageNumber(j + 1);
                                    page.setDocumentId(document.getItemID());
                                    if (TextUtils.isEmpty(preUrl)) {
                                        page.setPageUrl(pageUrl);
                                    } else {
                                        page.setPageUrl(preUrl + (j + 1)+"_4K" + endUrl);
                                    }
                                    pages.add(page);
                                }
                                document.setDocumentPages(pages);
                                documents.add(document);
                            }

                            refreshDocs.setItemId(itemId);
                            refreshDocs.setRefresh(true);
                            refreshDocs.setDocuments(documents);
                            EventBus.getDefault().post(refreshDocs);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return refreshDocs;

    }



}

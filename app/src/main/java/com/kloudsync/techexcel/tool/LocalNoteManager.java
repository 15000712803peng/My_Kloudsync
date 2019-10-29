package com.kloudsync.techexcel.tool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.BookNote;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.onyx.android.sdk.scribble.data.MultipleExportResult;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NoteModelList;
import com.onyx.android.sdk.scribble.data.NoteProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by tonyan on 2019/10/25.
 */

public class LocalNoteManager {

    private static LocalNoteManager mgr;
    Context context;
    String documentId;
    String pageIndex;
    int spaceId = 0;
    String syncroomId;

    public void exportPdfAndUpload(Context context ,BookNote note, String exportPath,String documentId,String pageIndex,int spaceId,String syncroomId) {

        if (note == null || TextUtils.isEmpty(note.documentId)) {
            return;
        }
        this.syncroomId = syncroomId;
        this.spaceId = spaceId;
        this.documentId = documentId;
        this.pageIndex = pageIndex;
        this.syncroomId = syncroomId;
        initExportProgressReceiver(context,exportPath,note.documentId);
        Intent intent = new Intent();
        intent.setAction(NoteConstants.NOTE_SERVICE_ACTION);
        intent.setPackage(NoteConstants.NOTE_PACKAGE_NAME);
        intent.putExtra(NoteConstants.SERVICE_ACTION, NoteConstants.ACTION_EXPORT_NOTE);
        intent.putExtra(NoteConstants.NOTE_MODEL_LIST, new NoteModelList(getNoteModelList(note)));
        intent.putExtra(NoteConstants.EXPORT_PATH, exportPath);
        Log.e("export_pdf","note:" + note + ",export path:" + exportPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private List<NoteModel> getNoteModelList(BookNote note) {
        ArrayList<NoteModel> noteModels = new ArrayList<>();
        noteModels.add(NoteModel.createNote(note.documentId, note.parentUniqueId, note.title));
        return noteModels;
    }

    public static synchronized LocalNoteManager getMgr(Context context){

        if(mgr == null){
            mgr = new LocalNoteManager(context);
        }
        return mgr;
    }

    BroadcastReceiver exportProgressReceiver;


    private void initExportProgressReceiver(Context context,final String exportPath,final String noteId){
        exportProgressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String serviceAction = intent.getStringExtra(NoteConstants.SERVICE_ACTION);
                if (StringUtils.isNullOrEmpty(action) ||
                        !StringUtils.safelyEquals(NoteConstants.ACTION_SERVICE_INTENT_RESULT, action) ||
                        !StringUtils.safelyEquals(NoteConstants.ACTION_EXPORT_NOTE, serviceAction)) {
                    return;
                }

                MultipleExportResult result = (MultipleExportResult) intent.getSerializableExtra(NoteConstants.SERVICE_INTENT_RESULT);
                if (result.inProgress()) {
                    Log.e("export_pdf","isProgress");
                } else if (result.isSuccess()) {
                    Log.e("export_pdf","isSuccess");
                    unregisterReceivers(context);
                    AddDocumentTool.addLocalNote((Activity) context,exportPath,noteId,documentId,pageIndex,spaceId,syncroomId);
                } else if (result.isFail()) {
                    Log.e("export_pdf","isFail");
                    unregisterReceivers(context);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(NoteConstants.ACTION_SERVICE_INTENT_RESULT);
        context.registerReceiver(exportProgressReceiver, filter);
    }

    private LocalNoteManager(Context context){
        this.context = context;
    }

    public class NoteConstants {
        public static final String KEY_OPEN_NOTE_BEAN_JSON = "OPEN_NOTE_BEAN_JSON";
        public static final String OPEN_NOTE_BEAN = "OPEN_NOTE_BEAN";
        public static final String NOTE_SERVICE_ACTION = "android.intent.action.NoteService";
        public static final String NOTE_PACKAGE_NAME = "com.onyx.android.note";
        public static final String NOTE_MODEL_LIST = "noteModelList";
        public static final String SERVICE_ACTION = "SERVICE_ACTION";
        public static final String ACTION_EXPORT_NOTE = "MULTIPLE_EXPORT_NOTE";
        public static final String EXPORT_PATH = "EXPORT_PATH";
        public static final String SERVICE_INTENT_RESULT = "ServiceIntentResult";
        public static final String SCRIBBLE_ACTIVITY_CLASS_PATH = "com.onyx.android.note.note.ui.ScribbleActivity";
        public static final String ACTION_SERVICE_INTENT_RESULT = "com.onyx.android.sdk.note.ui.service.SERVICE_INTENT_RESULT";
    }

    public void unregisterReceivers(Context context){
        if(exportProgressReceiver != null){
            context.unregisterReceiver(exportProgressReceiver);
            exportProgressReceiver = null;
        }
    }



}

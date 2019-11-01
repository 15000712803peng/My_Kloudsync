package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by tonyan on 2019/10/31.
 */

public class QueryLocalNoteTool {
    public static final String AUTHORITY = "com.onyx.android.sdk.note.ContentProvider";
    public static final String BASE_CONTENT_URI = "content://";

    public static Uri buildUri(String... paths) {
        Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
        String[] _paths = paths;
        int length = paths.length;

        for(int i = 0; i < length; ++i) {
            String path = _paths[i];
            builder.appendPath(path);
        }

        return builder.build();
    }

    public static boolean noteIsExist(Context context,String noteId){

        Cursor cursor = context.getContentResolver().query(buildUri("NoteModel"),new String[]{"uniqueId"}," uniqueId=?",new String[]{noteId},null,null);
        if(cursor != null){
            cursor.moveToFirst();
            Log.e("note","note:" + cursor.getString(0));
            return cursor.getCount() == 1;
        }
        return false;
    }


}

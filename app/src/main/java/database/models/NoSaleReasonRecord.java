package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class NoSaleReasonRecord extends Table {

    public static final String TAG = "no_sale_reason_record_table";

    public static final String TABLE        = "no_sale_reason_record";

    public static final String ID_VISIT         = "id_visit";
    public static final String ID_REASON        = "id_reason";
    public static final String COMMENTS         = "comments";
    public static final String SIGNATURE        = "signature";
    public static final String STATUS           = "status";

    public static long insert(Context context, String id_visit, String id_reason, String comments, String signature) {

        ContentValues cv = new ContentValues();
        cv.put(ID_VISIT, id_visit);
        cv.put(ID_REASON, id_reason);
        cv.put(COMMENTS, comments);
        cv.put(SIGNATURE, signature);
        cv.put(STATUS, "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, "id" + "=" + id, null);
    }

    public static Map<String,String> getAllNoSaleReasonRecordInVisit(Context context,String visit_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ID_VISIT, ID_REASON,
                        COMMENTS,SIGNATURE}, "id_visit = ?",
                new String[] { visit_id},
                null,
                null,
                ID_VISIT);
        if (cursor != null && cursor.moveToFirst()) {
            Map<String,String> map    = new HashMap<String, String>();
            map.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
            map.put(ID_REASON,cursor.getString(cursor.getColumnIndexOrThrow(ID_REASON)));
            map.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
            map.put(SIGNATURE,cursor.getString(cursor.getColumnIndexOrThrow(SIGNATURE)));

            return map;
        }
        return null;
    }

    public static Map<String,String> getAllNoSentNoSaleReasonRecordInVisit(Context context,String visit_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ID_VISIT, ID_REASON,
                        COMMENTS,SIGNATURE}, "visit_id = ? AND status = ?",
                new String[] { visit_id, "NO_SENT"},
                null,
                null,
                ID_VISIT);
        if (cursor != null && cursor.moveToFirst()) {
            Map<String,String> map    = new HashMap<String, String>();
            map.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
            map.put(ID_REASON,cursor.getString(cursor.getColumnIndexOrThrow(ID_REASON)));
            map.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
            map.put(SIGNATURE,cursor.getString(cursor.getColumnIndexOrThrow(SIGNATURE)));
            map.put(STATUS,cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));

            return map;
        }
        return null;
    }

    public static void updateNoSaleReasonToSentInVisit(Context context, String visit_id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE,cv,ID_VISIT + " = ?", new String[]{visit_id});
    }

}

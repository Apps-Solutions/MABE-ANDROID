package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class PartialPay extends Table {

    public static final String TABLE = "partial_payment";

    public static final String ID               = "id";
    public static final String NUMBER           = "number";
    public static final String TOTAL            = "total";
    public static final String DATE             = "date";
    public static final String EVIDENCE         = "evidence";
    public static final String STATUS           = "status";

    public static long insert(Context context, String id, String number, String total,String date,String evidence) {
        ContentValues cv = new ContentValues();
        cv.put(ID,      id);
        cv.put(NUMBER,  number);
        cv.put(TOTAL,   total);
        cv.put(DATE,    date);
        cv.put(EVIDENCE,evidence);


        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updatePartialPayToStatusSent(Context context, String id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static ArrayList<Map<String,String>> getAllPartialPaymentsNoSent(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM partial_payment WHERE status = ?; ", new String[] {"NO_SENT"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(NUMBER,cursor.getString(cursor.getColumnIndexOrThrow(NUMBER)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void clearPartialPayments (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }
}
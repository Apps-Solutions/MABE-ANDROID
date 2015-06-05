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

public class Fueling extends Table {

    public static final String TABLE = "fueling";

    public static final String ID               = "id";
    public static final String FUEL_TYPE        = "fuel_type";
    public static final String PLACE            = "place";
    public static final String KM               = "km";
    public static final String LITER            = "liter";
    public static final String TOTAL            = "total";
    public static final String FOLIO            = "folio";
    public static final String EVIDENCE         = "evidence";
    public static final String COMMENTS         = "comments";

    public static final String STATUS           = "status";

    public static long insert(Context context, String id, String fuel_type, String place,String km,String liter,String total,String folio,String evidence, String comments) {
        ContentValues cv = new ContentValues();
        cv.put(FUEL_TYPE,   fuel_type);
        cv.put(PLACE,       place);
        cv.put(KM,          km);
        cv.put(LITER,       liter);
        cv.put(TOTAL,       total);
        cv.put(FOLIO,       folio);
        cv.put(EVIDENCE,    evidence);
        cv.put(COMMENTS,    comments);

        cv.put(STATUS,      "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updateFuelingToStatusSent(Context context, String id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static ArrayList<Map<String,String>> getAllFuelingNoSent(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM fueling WHERE status = ?; ", new String[] {"NO_SENT"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(FUEL_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(FUEL_TYPE)));
                map_prod.put(PLACE,cursor.getString(cursor.getColumnIndexOrThrow(PLACE)));
                map_prod.put(KM,cursor.getString(cursor.getColumnIndexOrThrow(KM)));
                map_prod.put("liters",cursor.getString(cursor.getColumnIndexOrThrow(LITER)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
                map_prod.put(FOLIO,cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)));
                map_prod.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));

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

    public static void clearFueling (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }
}
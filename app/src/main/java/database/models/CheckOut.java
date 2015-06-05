package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class CheckOut extends Table {

    public static final String TAG = "check_in_table";

    public static final String TABLE = "check_out";

    //fields
    public static final String ID             = "id";
    public static final String LATITUDE       = "latitude";
    public static final String LONGITUDE      = "longitude";
    public static final String DATE_TIME      = "date_time";
    public static final String VISIT_ID       = "visit_id";
    public static final String PDV_ID         = "pdv_id";
    public static final String STATUS         = "status";

    public static long insert(Context context,String latitude,String longitude,String date_time,
                              String visit_id,String pdv_id,String status) {

        ContentValues cv = new ContentValues();

        cv.put(LATITUDE,latitude);
        cv.put(LONGITUDE,longitude);
        cv.put(DATE_TIME,date_time);
        cv.put(VISIT_ID,visit_id);
        cv.put(PDV_ID,pdv_id);
        cv.put(STATUS,"NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updateCheckOutStatus(Context context, String value){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, value);
        DataBaseAdapter.getDB(context).update(TABLE, cv, null,null);
    }

    public static void updateCheckOutStatusToSent(Context context, String visit_id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, VISIT_ID + "=" + visit_id, null);
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null,ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_mark_quest = new HashMap<String, String>();
                map_mark_quest.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_mark_quest.put("latitude",""+cursor.getInt(cursor.getColumnIndexOrThrow(LATITUDE)));
                map_mark_quest.put("longitude",""+cursor.getInt(cursor.getColumnIndexOrThrow(LONGITUDE)));
                map_mark_quest.put("date_time",""+cursor.getInt(cursor.getColumnIndexOrThrow(DATE_TIME)));
                map_mark_quest.put("visit_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
                map_mark_quest.put("pdv_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(PDV_ID)));
                map_mark_quest.put("status",""+cursor.getDouble(cursor.getColumnIndexOrThrow(STATUS)));

                list.add(map_mark_quest);
            }
            cursor.close();
        }
        return list;
    }

    public static Map<String,String> getCheckOutByVisit(Context context, String visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, VISIT_ID + "=" + visit_id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Map<String, String> map = new HashMap<String, String>();

            map.put(ID, cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(LATITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
            map.put(LONGITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
            map.put(DATE_TIME, cursor.getString(cursor.getColumnIndexOrThrow(DATE_TIME)));
            map.put(PDV_ID, cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            map.put(STATUS, cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
            cursor.close();
            return map;
        }
        return null;
    }

    public static Map<String,String> getCheckOutNoSentByVisit(Context context, String visit_id) {

        String status = "NO_SENT";
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "visit_id = ? AND status = ?",
                new String[] { visit_id,status},
                null,
                null,
                VISIT_ID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            Map<String, String> map = new HashMap<String, String>();

            map.put(ID, cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(LATITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
            map.put(LONGITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
            map.put(DATE_TIME, cursor.getString(cursor.getColumnIndexOrThrow(DATE_TIME)));
            map.put(PDV_ID, cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            map.put(STATUS, cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
            cursor.close();
            return map;
        }
        return null;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                CheckOut.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }
}
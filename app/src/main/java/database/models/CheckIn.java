package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by hugo.figueroa on 05/03/15.
 */
public class CheckIn extends Table {

    public static final String TAG = "check_in_table";

    public static final String TABLE = "check_in";

    //fields
    public static final String LATITUDE       = "latitude";
    public static final String LONGITUDE      = "longitude";
    public static final String DATE_TIME      = "date_time";
    public static final String VISIT_ID       = "visit_id";
    public static final String PDV_ID         = "pdv_id";
    public static final String STATUS         = "status";

    public static long insert(Context context,String latitude,String longitude,String date_time,
                              String visit_id,String pdv_id) {

        ContentValues cv = new ContentValues();

        cv.put(LATITUDE,latitude);
        cv.put(LONGITUDE,longitude);
        cv.put(DATE_TIME,date_time);
        cv.put(VISIT_ID,visit_id);
        cv.put(PDV_ID,pdv_id);
        cv.put(STATUS,"NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updateCheckInStatusToSent(Context context, String visit_id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, VISIT_ID + "=" + visit_id, null);
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null,VISIT_ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map = new HashMap<String, String>();
                map.put(LATITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
                map.put(LONGITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
                map.put(DATE_TIME, cursor.getString(cursor.getColumnIndexOrThrow(DATE_TIME)));
                map.put(VISIT_ID,""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
                map.put(PDV_ID, ""+cursor.getInt(cursor.getColumnIndexOrThrow(PDV_ID)));
                map.put(STATUS, cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));

                list.add(map);
            }

            cursor.close();
        }
        return list;
    }

    public static Map<String,String> getCheckInNoSentByVisit(Context context, String visit_id) {

        String status = "NO_SENT";
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "visit_id = ? AND status = ?",
                new String[] { visit_id,status},
                null,
                null,
                VISIT_ID);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            Map<String, String> map = new HashMap<String, String>();

            map.put(LATITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
            map.put(LONGITUDE, cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
            map.put(DATE_TIME, cursor.getString(cursor.getColumnIndexOrThrow(DATE_TIME)));
            map.put(VISIT_ID,""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
            map.put(PDV_ID, ""+cursor.getInt(cursor.getColumnIndexOrThrow(PDV_ID)));
            cursor.close();
            return map;
        }
        return null;
    }

    public static void clearCheckIn (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }
}

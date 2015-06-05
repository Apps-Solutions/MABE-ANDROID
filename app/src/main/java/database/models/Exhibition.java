package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

import static util.LogUtil.makeLogTag;

public class Exhibition extends Table {

    public static final String TAG = "exhibition_table";

    //Table Name
    public static final String TABLE = "exhibition";
    //***Datos de Persona
    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String TIMESTAMP = "timestamp";
    public static final String STATUS = "status";

    public static long insert(Context context, String id,String description,String timestamp,String status) {
        ContentValues cv = new ContentValues();

        cv.put(ID, id);
        cv.put(DESCRIPTION, description);
        cv.put(TIMESTAMP, timestamp);
        cv.put(STATUS, status);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Cursor getAll(Context _context){
        Cursor mC = DataBaseAdapter.getDB(_context).query(TABLE, null, null, null, null, null, null);
        if (mC != null && mC.getCount()>0)
            mC.moveToFirst();
        return mC;
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_prod.put("timestamp",cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                map_prod.put("status",cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
                list.add(map_prod);
            }

            cursor.close();
        }
        return list;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                Exhibition.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void insertExhibitionsFromJSONArray(Context context, JSONArray array){
        if (Exhibition.getAll(context) != null )    // Products stored in database
            Exhibition.removeAll(context);
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                //Context context, String id,String description,String timestamp,String status
                Exhibition.insert(context,obj.getString("id_exhibition_type"),obj.getString("ext_exhibition_type"),
                                    obj.getString("ext_status"),obj.getString("ext_timestamp"));
                //Log.d(TAG, "inserting");
            } catch (JSONException e) {
                e.printStackTrace();}
        }
    }
}
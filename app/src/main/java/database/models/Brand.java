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

public class Brand extends Table {

    public static final String TAG = "brand_table";

    public static final String TABLE        = "brand";

    public static final String ID           = "id";
    public static final String DESCRIPTION  = "description";
    public static final String RIVAL        = "rival";
    public static final String TIMESTAMP    = "timestamp";
    public static final String STATUS       = "status";

    public static long insert(Context context, String id, String description,String rival,String timestamp,String status){

        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(DESCRIPTION, description);
        cv.put(RIVAL, rival);
        cv.put(TIMESTAMP, timestamp);
        cv.put(STATUS, status);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static Cursor All(Context context) {
        Cursor mc = DataBaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(mc !=null)
            mc.moveToFirst();
        return mc;
    }

    public static void insertBrandsFromJSONArray(Context context, JSONArray array){
        if (Brand.getAllInMaps(context) != null )    // Products stored in database
            Brand.removeAll(context);
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                //Context context, String id,String description,String timestamp,String status
                Brand.insert(context,obj.getString("id_brand"),obj.getString("ba_brand"),
                        obj.getString("ba_rival"),obj.getString("ba_timestamp"),obj.getString("ba_status"));
                //Log.d(TAG, "inserting");
            } catch (JSONException e) {
                e.printStackTrace();}
        }
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                Brand.delete(context,id);
            }
        }
        cursor.close();
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, RIVAL + " = 0", null, null, null, null);
        //Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_brand = new HashMap<String, String>();
                map_brand.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_brand.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_brand.put("rival",cursor.getString(cursor.getColumnIndexOrThrow(RIVAL)));
                map_brand.put("timestamp",cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                map_brand.put("status",cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));

                list.add(map_brand);
            }
            cursor.close();
        }
        return list;
    }
}

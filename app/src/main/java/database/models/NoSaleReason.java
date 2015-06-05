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

public class NoSaleReason extends Table {

    public static final String TAG = "no_sale_reason_table";

    public static final String TABLE        = "no_sale_reason";

    public static final String ID           = "id";
    public static final String DESCRIPTION  = "description";

    public static long insert(Context context, String id, String description){

        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(DESCRIPTION, description);

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

    public static void insertNoSaleReasonFromJSONArray(Context context, JSONArray array){
        if (NoSaleReason.getAllInMaps(context) != null )
            NoSaleReason.removeAll(context);
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                NoSaleReason.insert(context,obj.getString("id_no_sale_reason"),obj.getString("no_sale_reason"));
            } catch (JSONException e) {
                e.printStackTrace();}
        }
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                NoSaleReason.delete(context,id);
            }
        }
        cursor.close();
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> no_sales_reason_map = new HashMap<String, String>();
                no_sales_reason_map.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                no_sales_reason_map.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));

                list.add(no_sales_reason_map);
            }
            cursor.close();
        }
        return list;
    }
}

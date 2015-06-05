package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;


public class WholeSalesPrices extends Table{

    public static final String TABLE = "wholesales_prices";

    public static final String ID               = "id";
    public static final String ID_VISIT         = "id_visit";
    public static final String EVIDENCE         = "evidence";
    public static final String COMMENTS         = "comments";

    public static final String STATUS           = "status";

    public static long insert(Context context, String id_visit, String comments,String evidence) {
        ContentValues cv = new ContentValues();
        cv.put(ID_VISIT,   id_visit);
        cv.put(COMMENTS,    comments);
        cv.put(EVIDENCE,    evidence);

        cv.put(STATUS,      "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updateWholeSalesPricesToStatusSent(Context context, String id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static ArrayList<Map<String,String>> getAllWholeSalesPricesNoSent(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM wholesales_prices WHERE status = ?; ", new String[] {"NO_SENT"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
                map_prod.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
                map_prod.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static Map<String,String> getWholeSalesPricesNoSentInVisit(Context context, String id_visit){
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM wholesales_prices WHERE id_visit = ? AND status = ?; ", new String[] {id_visit,"NO_SENT"});
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            Map<String,String> map_prod = new HashMap<String, String>();
            map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map_prod.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
            map_prod.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
            map_prod.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();
            return map_prod;
        }
        return null;
    }

    public static Map<String,String> getWholeSalesPricesInVisit(Context context, String id_visit){
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM wholesales_prices WHERE id_visit = ?; ", new String[] {id_visit});
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            Map<String,String> map_prod = new HashMap<String, String>();
            map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map_prod.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
            map_prod.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
            map_prod.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();
            return map_prod;
        }
        return null;
    }

    public static String getWholeSalesPricesIDInVisit(Context context, String id_visit){
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM wholesales_prices WHERE id_visit = ?; ", new String[] {id_visit});
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
            return id;
        }
        return "X";
    }

    public static ArrayList<Map<String,String>> getAllWholeSalesPrices(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM wholesales_prices",null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(ID_VISIT)));
                map_prod.put(COMMENTS,cursor.getString(cursor.getColumnIndexOrThrow(COMMENTS)));
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

    public static void clearWholeSalesPrices (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

}

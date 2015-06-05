package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class PendingOrderProduct extends Table{

    public static final String TAG = "pending_order_product_table";

    public static final String TABLE = "pending_order_product";

    //fields
    public static final String ID                   = "id";
    public static final String PRICE                = "price";
    public static final String NAME                 = "name";
    public static final String TAX                  = "tax";
    public static final String ORDER_ID             = "order_id";
    public static final String PRODUCT_PRESENTATION = "id_product_presentation";
    public static final String QUANTITY             = "quantity";


    public static long insert(Context context,
                              String id, String price, String tax,
                              String order_id, String id_product_presentation,
                              String quantity,String name){

        ContentValues cv = new ContentValues();
        cv.put(ID,id);
        cv.put(PRICE, price);
        cv.put(NAME, name);
        cv.put(TAX, tax);
        cv.put(ORDER_ID, order_id);
        cv.put(PRODUCT_PRESENTATION, id_product_presentation);
        cv.put(QUANTITY, quantity);

        //Log.d(TAG,"Inserting");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void clearPendingOrderProduct (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                PendingOrderProduct.delete(context,id);
            }
        }
        cursor.close();
    }

    public static void removeAllInOrder(Context context, String order_id){
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM pending_order_product WHERE order_id = ?; ", new String[] {order_id});
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                PendingOrderProduct.delete(context,id);
            }
        }
        cursor.close();
    }

    public static ArrayList<Map<String,String>> getAllFromOrderInMaps(Context context,String order_id){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM pending_order_product WHERE order_id = ?; ", new String[] {order_id});
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                map_prod.put(NAME,cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put(TAX,cursor.getString(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put(ORDER_ID,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
                map_prod.put(PRODUCT_PRESENTATION,cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_PRESENTATION)));
                map_prod.put(QUANTITY,cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));

                list.add(map_prod);
            }

            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                map_prod.put(TAX,cursor.getString(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put(NAME,cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put(ORDER_ID,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
                map_prod.put(PRODUCT_PRESENTATION,cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_PRESENTATION)));
                map_prod.put(QUANTITY,cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));

                list.add(map_prod);
            }

            cursor.close();
        }
        return list;
    }

    public static Cursor getAllCursor(Context context){
        Cursor c = DataBaseAdapter.getDB(context).query(TABLE,null,null,null,null,null,ID);
        if (c != null) c.moveToFirst();
        return c;
    }
}


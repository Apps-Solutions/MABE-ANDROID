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
import java.util.List;
import java.util.Map;

import database.DataBaseAdapter;

public class OrderSentProduct extends Table{

    public static final String TAG = "order_sent_product_table";

    public static final String TABLE = "order_sent_product";

    //fields
    public static final String ID                   = "id";
    public static final String ORDER_ID             = "order_id";
    public static final String PRODUCT_ID           = "id_product";
    public static final String PRICE                = "price";
    public static final String NAME                 = "name";
    public static final String JDE                  = "jde";
    public static final String QUANTITY             = "quantity";

    public static long insert(Context context,  String order_id,
                                                String name,
                                                String jde,
                                                String product_id,
                                                String price,
                                                String quantity) {
        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_ID, product_id);
        cv.put(ORDER_ID, order_id);
        cv.put(NAME,name);
        cv.put(JDE,jde);
        cv.put(PRICE, price);
        cv.put(QUANTITY, quantity);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context,
                             String order_id, String new_order_id) {

        ContentValues cv = new ContentValues();
        cv.put(ORDER_ID, new_order_id);

        return DataBaseAdapter.getDB(context).update(TABLE,cv,ORDER_ID + " = ?", new String[]{order_id});
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void clearOrderSentProduct (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static List<Map<String,String>> getAllProductsInOrderSent(Context context, String order_id) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "= '" + order_id+"'", null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID, cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map.put(ORDER_ID, cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
                map.put(PRODUCT_ID, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                map.put(PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                map.put(QUANTITY, cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));
                map.put(NAME, cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map.put(JDE, cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static List<Map<String,String>> getAllProductsInOrderSentToSynchronizer(Context context, String order_id) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "=?", new String[]{order_id}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(PRODUCT_ID, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                map.put(PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                map.put(QUANTITY, cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static void removeAllProductsInOrder(Context context, String order_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "=?", new String[]{order_id}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                OrderSentProduct.delete(context,id);
            }
        }
        cursor.close();
    }
}


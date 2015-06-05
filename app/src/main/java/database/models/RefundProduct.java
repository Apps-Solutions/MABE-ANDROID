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

public class RefundProduct extends Table{

    public static final String TAG = "order_sent_product_table";

    public static final String TABLE = "refund_product";

    public static final String ID                   = "id";
    public static final String ORDER_ID             = "order_id";
    public static final String PRODUCT_ID           = "product_id";
    public static final String PRODUCT_JDE          = "jde";
    public static final String NAME                 = "name";
    public static final String QUANTITY             = "quantity";

    public static long insert(Context context,  String order_id,
                                                String product_id,
                                                String jde,
                                                String name,
                                                String quantity) {
        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_ID, product_id);
        cv.put(ORDER_ID, order_id);
        cv.put(PRODUCT_JDE, jde);
        cv.put(NAME, name);
        cv.put(QUANTITY, quantity);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context,
                             String order_id, String new_order_id) {

        ContentValues cv = new ContentValues();
        cv.put(ORDER_ID, new_order_id);

        return DataBaseAdapter.getDB(context).update(TABLE, cv, ORDER_ID + "=" + order_id, null);
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static int deleteAllRefundProductsInVisit(Context context, String visit_id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + visit_id, null);
    }

    public static void clearRefundProduct (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static List<Map<String,String>> getAllProductsInRefundInOrder(Context context, String order_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "=" + order_id, null, null, null, null);
        if (cursor != null && cursor.getCount() >= 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID, cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map.put(ORDER_ID, cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
                map.put(PRODUCT_ID, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                map.put(PRODUCT_JDE, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_JDE)));
                map.put(NAME, cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map.put(QUANTITY, cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));
                list.add(map);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static void removeAllProductsInRefund(Context context, String order_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "=" + order_id, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                RefundProduct.delete(context,id);
            }
        }
        cursor.close();
    }
}


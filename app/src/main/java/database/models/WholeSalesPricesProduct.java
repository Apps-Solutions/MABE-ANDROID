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

public class WholeSalesPricesProduct extends Table{

    public static final String TAG = "whole_sales_prices_product";

    public static final String TABLE = "whole_sales_prices_product";

    public static final String ID                   = "id";
    public static final String ID_ORDER             = "id_order";
    public static final String ID_VISIT             = "id_visit";
    public static final String ID_PRODUCT           = "id_product";
    public static final String NAME                 = "name";
    public static final String PRICE                = "price";


    public static long insert(Context context, String id_order, String id_visit, String id_product, String name, String price){
        Log.d(TAG,"Inserting");
        ContentValues cv = new ContentValues();
        cv.put(ID_ORDER,    id_order);
        cv.put(ID_VISIT,    id_visit);
        cv.put(ID_PRODUCT,  id_product);
        cv.put(NAME,        name);
        cv.put(PRICE,       price);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context,
                             String id_visit, String id_product, String new_order_id) {

        ContentValues cv = new ContentValues();
        cv.put(ID_ORDER, new_order_id);

        return DataBaseAdapter.getDB(context).update(TABLE,cv,"id_visit  = ? AND id_product = ?", new String[]{id_visit,id_product});
    }

    public static int updatePrice(Context context,
                             String id, String price) {

        ContentValues cv = new ContentValues();
        cv.put(PRICE, price);

        return DataBaseAdapter.getDB(context).update(TABLE,cv,"id  = ?", new String[]{id});
    }

    public static List<Map<String,String>> getProductsInOrder(Context context, String order_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_ORDER + "=" + order_id, null, null, null, null);
        if (cursor != null && cursor.getCount() >= 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                list.add(map);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static List<Map<String,String>> getProductsInVisit(Context context, String id_visit) {
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ?",
                new String[] { id_visit},
                null,
                null,
                ID_PRODUCT);
        if (cursor != null && cursor.getCount() >= 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));
                map.put(NAME,cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }
}


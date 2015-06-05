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

public class Shelf_ReviewProduct extends Table{

    public static final String TAG = "shelf_review_product_table";

    public static final String TABLE = "shelf_review_product";

    public static final String ID_PRODUCT           = "id_product";
    public static final String SHELF_REVIEW_ID      = "shelf_review_id";
    public static final String CURRENT              = "current";
    public static final String SHELF_BOXES          = "shelf_boxes";
    public static final String EXHIBITION_BOXES     = "exhibition_boxes";
    public static final String EXPIRATION           = "expiration";
    public static final String PRICE                = "price";


    public static long insert(Context context, String id_product, String shelf_review_id,
                              String current,String shelf_boxes, String exhibition_boxes,
                              String expiration, String price){
        ContentValues cv = new ContentValues();
        cv.put(ID_PRODUCT, id_product);
        cv.put(SHELF_REVIEW_ID, shelf_review_id);

        cv.put(CURRENT, current);
        cv.put(SHELF_BOXES, shelf_boxes);
        cv.put(EXHIBITION_BOXES, exhibition_boxes);
        cv.put(EXPIRATION, expiration);
        cv.put(PRICE, price);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, SHELF_REVIEW_ID + "=" + id, null);
    }

    public static Map<String,String> getProductInShelfReview(Context context, String shelf_review_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "shelf_review_id = ?",
                new String[] { shelf_review_id},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            cursor.moveToFirst();

            Map<String,String> map = new HashMap<String, String>();
            map.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
            map.put(CURRENT,cursor.getString(cursor.getColumnIndexOrThrow(CURRENT)));
            map.put(SHELF_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(SHELF_BOXES)));
            map.put(EXHIBITION_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(EXHIBITION_BOXES)));
            map.put(EXPIRATION,cursor.getString(cursor.getColumnIndexOrThrow(EXPIRATION)));
            map.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));

            cursor.close();

            return map;
        }
        return null;
    }
}


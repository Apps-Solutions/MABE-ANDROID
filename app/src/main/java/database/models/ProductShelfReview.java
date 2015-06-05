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

public class ProductShelfReview extends Table{

    public static final String TAG = "product_shelf_review_product";

    public static final String TABLE = "product_shelf_review";

    public static final String ID                   = "id";
    public static final String ID_VISIT             = "id_visit";
    public static final String ID_PRODUCT           = "id_product";
    public static final String CURRENT_SHELF        = "current_shelf";
    public static final String CURRENT_EXHIB        = "current_exhibition";
    public static final String SHELF_BOXES          = "shelf_boxes";
    public static final String EXHIBITION_BOXES     = "exhibition_boxes";
    public static final String EXPIRATION           = "expiration";
    public static final String PRICE                = "price";

    public static final String FRONT                = "front";
    public static final String RIVAL1               = "rival1";
    public static final String RIVAL2               = "rival2";
    public static final String TOTAL                = "total";

    public static final String STATUS               = "status";


    public static long insert(Context context, String id_product, String id_visit,

                              String current_shelf,String current_exhib,

                              String shelf_boxes,
                              String exhibition_boxes,String expiration,
                              String price,
                              String front,
                              String rival1,
                              String rival2,
                              String total){
        ContentValues cv = new ContentValues();
        cv.put(ID_PRODUCT, id_product);
        cv.put(ID_VISIT, id_visit);

        cv.put(CURRENT_SHELF, current_shelf);
        cv.put(CURRENT_EXHIB, current_exhib);

        cv.put(SHELF_BOXES, shelf_boxes);
        cv.put(EXHIBITION_BOXES, exhibition_boxes);
        cv.put(EXPIRATION, expiration);
        cv.put(PRICE, price);

        cv.put(FRONT, front);
        cv.put(RIVAL1, rival1);
        cv.put(RIVAL2, rival2);
        cv.put(TOTAL, total);

        cv.put(STATUS, "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Map<String,String> getProductShelfReviewNoSentWithProductInVisit(Context context, String id_product, String id_visit) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product = ? AND status = ?",
                new String[] { id_visit,id_product,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            cursor.moveToFirst();

            Map<String,String> map = new HashMap<String, String>();
            map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));

            map.put(CURRENT_SHELF,cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_SHELF)));
            map.put(CURRENT_EXHIB,cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_EXHIB)));

            map.put(SHELF_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(SHELF_BOXES)));
            map.put(EXHIBITION_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(EXHIBITION_BOXES)));
            map.put(EXPIRATION,cursor.getString(cursor.getColumnIndexOrThrow(EXPIRATION)));
            map.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));

            map.put(FRONT,cursor.getString(cursor.getColumnIndexOrThrow(FRONT)));
            map.put(RIVAL1,cursor.getString(cursor.getColumnIndexOrThrow(RIVAL1)));
            map.put(RIVAL2,cursor.getString(cursor.getColumnIndexOrThrow(RIVAL2)));
            map.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static String getProductShelfReviewIDWithProductInVisit(Context context, String id_product, String id_visit) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product = ?",
                new String[] { id_visit,id_product},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            String id   = cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT));
            cursor.close();
            return id;
        }
        return null;
    }

    public static List<Map<String,String>> getAllProductShelfReviewsNoSentInVisit(Context context, String id_visit) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status = ?",
                new String[] { id_visit,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));

                map.put(CURRENT_SHELF,cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_SHELF)));
                map.put(CURRENT_EXHIB,cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_EXHIB)));

                map.put(SHELF_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(SHELF_BOXES)));
                map.put(EXHIBITION_BOXES,cursor.getString(cursor.getColumnIndexOrThrow(EXHIBITION_BOXES)));
                map.put(EXPIRATION,cursor.getString(cursor.getColumnIndexOrThrow(EXPIRATION)));
                map.put(PRICE,cursor.getString(cursor.getColumnIndexOrThrow(PRICE)));

                map.put(FRONT,cursor.getString(cursor.getColumnIndexOrThrow(FRONT)));
                map.put(RIVAL1,cursor.getString(cursor.getColumnIndexOrThrow(RIVAL1)));
                map.put(RIVAL2,cursor.getString(cursor.getColumnIndexOrThrow(RIVAL2)));
                map.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static void updateProductShelfReviewToSent(Context context, String id_product, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ? AND id_product = ?", new String[] {id_visit,id_product});
    }

    public static void updateAllProductShelfReviewInVisitToSent(Context context, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ?", new String[] {id_visit});
    }
}


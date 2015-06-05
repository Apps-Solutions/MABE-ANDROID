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
import java.util.List;
import java.util.Map;

import database.DataBaseAdapter;

import static util.LogUtil.makeLogTag;

public class Offers extends Table {

    public static final String TAG = "offers_table";
    public static final String TABLE = "offers";

    public static final String ID                   = "id";
    public static final String ID_VISIT             = "id_visit";

    public static final String ID_PRODUCT           = "id_product";

    public static final String OWN_ID_PRODUCT       = "own_id_product";

    public static final String PUBLIC_PRICE         = "public_price";
    public static final String PROMOTION_PRICE      = "promotion_price";
    public static final String PROMOTION_START      = "promotion_start";
    public static final String PROMOTION_END        = "promotion_end";
    public static final String ID_GRAPHIC_MATERIAL  = "id_graphic_material";
    public static final String ACTIVATION           = "activation";
    public static final String ID_PROMOTION_TYPE    = "id_promotion_type";

    public static final String MECHANICS            = "mechanics";
    public static final String DIFFERED_SKU         = "differed_sku";
    public static final String SKU                  = "sku";
    public static final String EXHIBITION           = "exhibition";
    public static final String ID_EXHIBITION_TYPE   = "id_exhibition_type";
    public static final String EXISTENCE            = "existence";
    public static final String ID_RESOURCE_TYPE     = "id_resource_type";
    public static final String EVIDENCE             = "evidence";

    public static final String PRODUCT_NAME         = "name_product";
    public static final String STATUS               = "status";
    public static final String TYPE                 = "type";   // OWN, RIVAL


    public static long insert(Context context,String id_visit, String id_product,String public_price,String promotion_price,
                              String promotion_start, String promotion_end,String id_graphic_material,
                              String activation,String id_promotion_type,String mechanics,
                              String differed_sku,String exhibition,String id_exhibition_type,
                              String existence,String id_resource_type,String evidence, String type) {
        ContentValues cv = new ContentValues();

        cv.put(ID_VISIT,id_visit);
        cv.put(ID_PRODUCT,id_product);
        cv.put(PUBLIC_PRICE,public_price);
        cv.put(PROMOTION_PRICE,promotion_price);
        cv.put(PROMOTION_START,promotion_start);
        cv.put(PROMOTION_END,promotion_end);
        cv.put(ID_GRAPHIC_MATERIAL,id_graphic_material);
        cv.put(ACTIVATION,activation);
        cv.put(ID_PROMOTION_TYPE,id_promotion_type);

        cv.put(MECHANICS,mechanics);
        cv.put(DIFFERED_SKU,differed_sku);
        cv.put(EXHIBITION,exhibition);
        cv.put(ID_EXHIBITION_TYPE,id_exhibition_type);
        cv.put(EXISTENCE,existence);
        cv.put(ID_RESOURCE_TYPE,id_resource_type);
        cv.put(EVIDENCE,evidence);

        cv.put(TYPE,type);
        cv.put(STATUS,"NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static long insertFromMap(Context context,Map<String,String> data) {
        ContentValues cv = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
            cv.put(entry.getKey(),entry.getValue());

        cv.put(STATUS,"NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static List<Map<String,String>> getAllNoSentOffersInVisit(Context context, String id_visit) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status=?",
                new String[] { id_visit,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();
                map.put(ID_PRODUCT, cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(PUBLIC_PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PUBLIC_PRICE)));
                map.put(PROMOTION_PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_PRICE)));
                map.put(PROMOTION_START, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_START)));
                map.put(PROMOTION_END, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_END)));
                map.put(ID_GRAPHIC_MATERIAL, cursor.getString(cursor.getColumnIndexOrThrow(ID_GRAPHIC_MATERIAL)));
                map.put(ACTIVATION, cursor.getString(cursor.getColumnIndexOrThrow(ACTIVATION)));
                map.put(ID_PROMOTION_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_PROMOTION_TYPE)));
                map.put(MECHANICS, cursor.getString(cursor.getColumnIndexOrThrow(MECHANICS)));
                map.put(DIFFERED_SKU, cursor.getString(cursor.getColumnIndexOrThrow(DIFFERED_SKU)));
                map.put(EXHIBITION, cursor.getString(cursor.getColumnIndexOrThrow(EXHIBITION)));
                map.put(ID_EXHIBITION_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_EXHIBITION_TYPE)));
                map.put(EXISTENCE, cursor.getString(cursor.getColumnIndexOrThrow(EXISTENCE)));
                map.put(ID_RESOURCE_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_RESOURCE_TYPE)));
                map.put(SKU, cursor.getString(cursor.getColumnIndexOrThrow(SKU)));

                map.put(EVIDENCE, cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static String getNoSentOfferIDInVisit(Context context, String id_visit, String id_product) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status=? AND id_product=?",
                new String[] { id_visit,"NO_SENT",id_product},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            cursor.moveToFirst();
            String ret = cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT));
            cursor.close();
            return ret;
        }
        return null;
    }

    public static List<Map<String,String>> getAllRivalNoSentOffersInVisitByOwnProduct(Context context, String id_visit, String own_id_product) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND own_id_product = ? AND status=? AND type = ?",
                new String[] { id_visit,own_id_product,"NO_SENT","RIVAL"},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();
                map.put(ID, ""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map.put(ID_PRODUCT, cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(PRODUCT_NAME, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_NAME)));
                map.put(PUBLIC_PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PUBLIC_PRICE)));
                map.put(PROMOTION_PRICE, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_PRICE)));
                map.put(PROMOTION_START, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_START)));
                map.put(PROMOTION_END, cursor.getString(cursor.getColumnIndexOrThrow(PROMOTION_END)));
                map.put(ID_GRAPHIC_MATERIAL, cursor.getString(cursor.getColumnIndexOrThrow(ID_GRAPHIC_MATERIAL)));
                map.put(ACTIVATION, cursor.getString(cursor.getColumnIndexOrThrow(ACTIVATION)));
                map.put(ID_PROMOTION_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_PROMOTION_TYPE)));
                map.put(MECHANICS, cursor.getString(cursor.getColumnIndexOrThrow(MECHANICS)));
                map.put(DIFFERED_SKU, cursor.getString(cursor.getColumnIndexOrThrow(DIFFERED_SKU)));
                map.put(EXHIBITION, cursor.getString(cursor.getColumnIndexOrThrow(EXHIBITION)));
                map.put(ID_EXHIBITION_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_EXHIBITION_TYPE)));
                map.put(EXISTENCE, cursor.getString(cursor.getColumnIndexOrThrow(EXISTENCE)));
                map.put(ID_RESOURCE_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_RESOURCE_TYPE)));
                map.put(EVIDENCE, cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static void updateOfferToSent(Context context, String id_product, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ? AND id_product = ?", new String[] {id_visit,id_product});
    }

    public static void updateAllOffersInVisitToSent(Context context, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ?", new String[] {id_visit});
    }
}
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

public class InventoryProduct extends Table{

    public static final String TAG = "inventory_product_table";

    public static final String TABLE = "inventory_product";

    public static final String ID                   = "id";
    public static final String ID_VISIT             = "id_visit";
    public static final String ID_PRODUCT           = "id_product";
    public static final String STOCK                = "stock";
    public static final String SYSTEM_STOCK         = "system_stock";
    public static final String COMMENT              = "comment";
    public static final String STATUS               = "status";

    public static long insert(Context context,String id_visit, String id_product, String stock, String system_stock, String comment){

        ContentValues cv = new ContentValues();
        cv.put(ID_VISIT,id_visit);
        cv.put(ID_PRODUCT,id_product);
        cv.put(STOCK,stock);
        cv.put(SYSTEM_STOCK,system_stock);
        cv.put(COMMENT,comment);
        cv.put(STATUS,"NO_SENT");
        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static ArrayList<Map<String,String>> getInventoryNoSentInVisit(Context context, String id_visit){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status=?",
                new String[] { id_visit,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);
        if (cursor != null && cursor.getCount() > 0) {

            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map_prod.put(STOCK,cursor.getString(cursor.getColumnIndexOrThrow(STOCK)));
                map_prod.put(SYSTEM_STOCK,cursor.getString(cursor.getColumnIndexOrThrow(SYSTEM_STOCK)));
                map_prod.put(COMMENT,cursor.getString(cursor.getColumnIndexOrThrow(COMMENT)));

                list.add(map_prod);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static Map<String,String> getInventoryProductInVisit(Context context, String id_visit, String id_product){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product=?",
                new String[] { id_visit,id_product},
                null,
                null,
                ID_PRODUCT);
        if (cursor != null && cursor.getCount() != 0) {

            cursor.moveToFirst();

            Map<String,String> map_prod = new HashMap<String, String>();
            map_prod.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
            map_prod.put(STOCK,cursor.getString(cursor.getColumnIndexOrThrow(STOCK)));
            map_prod.put(SYSTEM_STOCK,cursor.getString(cursor.getColumnIndexOrThrow(SYSTEM_STOCK)));
            map_prod.put(COMMENT,cursor.getString(cursor.getColumnIndexOrThrow(COMMENT)));

            cursor.close();
            return map_prod;
        }
        return null;
    }

    public static int updateInventoryProductToSent(Context context,String id_visit) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        return DataBaseAdapter.getDB(context).update(TABLE,cv,ID_VISIT + " = ?", new String[]{id_visit});
    }
}


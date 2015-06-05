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

public class Warehouse extends Table {

    public static final String TAG          = "warehouse_table";

    public static final String TABLE        = "warehouse";

    public static final String ID           = "id";
    public static final String ID_VISIT     = "id_visit";
    public static final String ID_PRODUCT   = "id_product";
    public static final String NAME_PRODUCT = "name_product";
    public static final String FINAl_WARE   = "final_stock";
    public static final String FINAL_INV    = "boxes_out";

    public static final String WARE_INV     = "ware_inv";
    public static final String SHELF_INV    = "shelf_inv";
    public static final String EXHIB_INV    = "exhib_inv";
    public static final String STATUS       = "status";

    public static long insert(Context context, String id_visit,String id_product,String name_product,String final_stock,String boxes_out,
                              String ware_inv,String shelf_inv, String exhib_inv) {
        ContentValues cv = new ContentValues();

        cv.put(ID_VISIT, id_visit);
        cv.put(ID_PRODUCT, id_product);
        cv.put(NAME_PRODUCT, name_product);
        cv.put(FINAl_WARE, final_stock);
        cv.put(FINAL_INV, boxes_out);

        cv.put(WARE_INV, ware_inv);
        cv.put(SHELF_INV, shelf_inv);
        cv.put(EXHIB_INV, exhib_inv);

        cv.put(STATUS, "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static List<Map<String,String>> getAllWarehouseNoSentInVisit(Context context, String id_visit){

        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status=?",
                new String[] { id_visit,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map_prod.put(NAME_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(NAME_PRODUCT)));
                map_prod.put(FINAl_WARE,cursor.getString(cursor.getColumnIndexOrThrow(FINAl_WARE)));
                map_prod.put(FINAL_INV,cursor.getString(cursor.getColumnIndexOrThrow(FINAL_INV)));

                map_prod.put(WARE_INV,cursor.getString(cursor.getColumnIndexOrThrow(WARE_INV)));
                map_prod.put(SHELF_INV,cursor.getString(cursor.getColumnIndexOrThrow(SHELF_INV)));
                map_prod.put(EXHIB_INV,cursor.getString(cursor.getColumnIndexOrThrow(EXHIB_INV)));

                list.add(map_prod);
            }
            cursor.close();
        }
        return list;
    }

    public static Map<String,String> getWarehouseProductInVisit(Context context, String id_visit,String id_product){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product=?",
                new String[] { id_visit,id_product},
                null,
                null,
                ID_PRODUCT);
        if (cursor != null && cursor.getCount() != 0) {

            cursor.moveToFirst();

            Map<String,String> map_prod = new HashMap<String, String>();
            map_prod.put(ID,""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
            map_prod.put(ID_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
            map_prod.put(NAME_PRODUCT,cursor.getString(cursor.getColumnIndexOrThrow(NAME_PRODUCT)));
            map_prod.put(FINAl_WARE,cursor.getString(cursor.getColumnIndexOrThrow(FINAl_WARE)));
            map_prod.put(FINAL_INV,cursor.getString(cursor.getColumnIndexOrThrow(FINAL_INV)));

            map_prod.put(WARE_INV,cursor.getString(cursor.getColumnIndexOrThrow(WARE_INV)));
            map_prod.put(SHELF_INV,cursor.getString(cursor.getColumnIndexOrThrow(SHELF_INV)));
            map_prod.put(EXHIB_INV,cursor.getString(cursor.getColumnIndexOrThrow(EXHIB_INV)));

            cursor.close();
            return map_prod;
        }
        return null;
    }

    public static void updateAllWarehouseProductsInVisitToSent(Context context, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ?", new String[] {id_visit});
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }
}
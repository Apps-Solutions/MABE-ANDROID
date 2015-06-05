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
import java.util.Map;

import database.DataBaseAdapter;

import static util.LogUtil.makeLogTag;

public class PendingOrder extends Table {

    public static final String TAG = "pending_order_table";

    public static final String TABLE    = "pending_order";

    public static final String ID       = "id";
    public static final String TOTAL    = "total";
    public static final String SUBTOTAL = "subtotal";
    public static final String TAX      = "tax";
    public static final String ID_PDV   = "id_pdv";
    public static final String USER     = "user";
    public static final String DATE     = "date";

    public static long insert(Context context,  String id,
                                                String id_pdv,
                                                String user,
                                                String date,
                                                String total,
                                                String subtotal,
                                                String tax) {

        ContentValues cv = new ContentValues();

        cv.put(ID, id);
        cv.put(ID_PDV, id_pdv);
        cv.put(USER, user);
        cv.put(DATE, date);
        cv.put(TOTAL, total);
        cv.put(SUBTOTAL, subtotal);
        cv.put(TAX, tax);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void savePendingOrder(Context context, JSONObject pending_orders_info){
        try {
            PendingOrder.insert(context, pending_orders_info.getString("id_order"),
                    pending_orders_info.getString("id_pdv"),
                    pending_orders_info.getString("user"),
                    pending_orders_info.getString("date"),
                    pending_orders_info.getString("total"),
                    pending_orders_info.getString("subtotal"),
                    pending_orders_info.getString("tax"));
            JSONArray products     = pending_orders_info.getJSONArray("detail");
            for (int i=0; i<products.length(); i++){
                JSONObject  product = products.getJSONObject(i);

                PendingOrderProduct.insert(context,
                        product.getString("id_product"),
                        product.getString("price"),
                        product.getString("tax"),
                        pending_orders_info.getString("id_order"),
                        product.getString("id_product_presentation"),
                        product.getString("quantity"),
                        product.getString("product"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Cursor getAll(Context _context){
        Cursor mC = DataBaseAdapter.getDB(_context).query(TABLE, null, null, null, null, null, null);
        if (mC != null && mC.getCount()>0)
            mC.moveToFirst();
        return mC;
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(ID_PDV,cursor.getString(cursor.getColumnIndexOrThrow(ID_PDV)));
                map_prod.put(USER,cursor.getString(cursor.getColumnIndexOrThrow(USER)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
                map_prod.put(SUBTOTAL,cursor.getString(cursor.getColumnIndexOrThrow(SUBTOTAL)));
                map_prod.put(TAX,cursor.getString(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                list.add(map_prod);
            }
            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getPendingOrderFromPDV(Context context, String pdv){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_PDV + "=" + pdv, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(ID_PDV,cursor.getString(cursor.getColumnIndexOrThrow(ID_PDV)));
                map_prod.put(USER,cursor.getString(cursor.getColumnIndexOrThrow(USER)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
                map_prod.put(SUBTOTAL,cursor.getString(cursor.getColumnIndexOrThrow(SUBTOTAL)));
                map_prod.put(TAX,cursor.getString(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put("type"," ");
                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                PendingOrder.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void clearPendingOrder (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static int deletePendingOrderFromPDV(Context context, String id_pdv) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID_PDV + "=" + id_pdv, null);
    }
}
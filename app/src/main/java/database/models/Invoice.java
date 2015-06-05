package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class Invoice extends Table {

    public static final String TABLE = "invoice";

    public static final String ID               = "id";
    public static final String PDV_ID           = "pdv_id";
    public static final String PDV_NAME         = "pdv_name";
    public static final String FOLIO            = "folio";
    public static final String DATE             = "inv_date";
    public static final String TOTAL            = "total";
    public static final String PAID             = "paid";
    public static final String STATUS           = "status";

    public static final String METHOD_ID        = "method_id";


    public static final String PAYMENT_DATE     = "payment_date";

    public static long insert(Context context, String id, String pdv_id, String pdv_name,String folio,String date,String total) {
        ContentValues cv = new ContentValues();
        cv.put(ID,      id);
        cv.put(PDV_ID,  pdv_id);
        cv.put(PDV_NAME,pdv_name);
        cv.put(FOLIO,   folio);
        cv.put(DATE,    date);
        cv.put(TOTAL,   total);
        cv.put(PAID,    "NO_PAID");
        cv.put(STATUS,  "NO_SENT");
        cv.put(PAYMENT_DATE,  "");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void insertInvoicesOrdersFromJSONArray(Context context, JSONArray array){
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                Invoice.insert(context,obj.getString("invoice_id"),
                        obj.getString("pdv_id"),
                        obj.getString("pdv_name"),
                        obj.getString("folio"),
                        obj.getString("date"),
                        obj.getString("in_total"));
            } catch (JSONException e) {
                e.printStackTrace();}
        }
    }

    public static void updateInvoiceToStatusPaid(Context context, String invoice_id, String method_id,String payment_date){
        ContentValues cv = new ContentValues();
        cv.put(PAID, "PAID");
        cv.put(PAYMENT_DATE, payment_date);
        cv.put(METHOD_ID,method_id);
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + invoice_id, null);
    }

    public static void updateInvoiceToStatusSent(Context context, String invoice_id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + invoice_id, null);
    }

    public static ArrayList<Map<String,String>> getAllInvoicesPaidInMapsForPDV(Context context,String pdv_id){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM invoice WHERE pdv_id = ? AND paid = ?; ", new String[] {pdv_id,"PAID"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                map_prod.put(PDV_NAME,cursor.getString(cursor.getColumnIndexOrThrow(PDV_NAME)));
                map_prod.put(FOLIO,cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static ArrayList<Map<String,String>> getAllInvoicesPaidAndNoSentInMapsForPDV(Context context,String pdv_id){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM invoice WHERE pdv_id = ? AND paid = ? AND status = ?; ", new String[] {pdv_id,"PAID","NO_SENT"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                map_prod.put(PDV_NAME,cursor.getString(cursor.getColumnIndexOrThrow(PDV_NAME)));
                map_prod.put(FOLIO,cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
                map_prod.put(METHOD_ID,cursor.getString(cursor.getColumnIndexOrThrow(METHOD_ID)));

                map_prod.put("date",cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_DATE)));

                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }


    public static Cursor getAll(Context _context){
        Cursor mC = DataBaseAdapter.getDB(_context).query(TABLE, null, null, null, null, null, null);
        if (mC != null && mC.getCount()>0)
            mC.moveToFirst();
        return mC;
    }

    public static ArrayList<Map<String,String>> getAllInvoicesNotPaidInMapsForPDV(Context context,String pdv_id){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM invoice WHERE pdv_id = ? AND paid = ?; ", new String[] {pdv_id,"NO_PAID"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                map_prod.put(PDV_NAME,cursor.getString(cursor.getColumnIndexOrThrow(PDV_NAME)));
                map_prod.put(FOLIO,cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

                list.add(map_prod);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static ArrayList<Map<String,String>> getAllInvoicesInMaps(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM invoice",null);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                map_prod.put(PDV_NAME,cursor.getString(cursor.getColumnIndexOrThrow(PDV_NAME)));
                map_prod.put(FOLIO,cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)));
                map_prod.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                map_prod.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

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
                Invoice.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static void clearInvoices (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }
}
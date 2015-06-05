package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DataBaseAdapter;

public class OrderSent extends Table {

    public static final String TABLE = "order_sent";

    public static final String ID               = "id";
    public static final String ORDER_DATE       = "date_time";
    public static final String ORDER_ID         = "order_id";
    public static final String PDV_ID           = "pdv_id";
    public static final String VISIT_ID         = "visit_id";
    public static final String USERNAME         = "username";

    public static final String TOTAL            = "total";
    public static final String SUBTOTAL         = "subtotal";
    public static final String TAX              = "tax";

    public static final String METHOD_ID        = "method_id";

    public static final String STATUS           = "status";
    public static final String SENT             = "sent";
    public static final String TYPE             = "type";
    public static final String PAID             = "paid";

    public static final String PAYMENT_DATE     = "payment_date";

    public static long insert(Context context, String order_date, String order_id, String pdv_id, String visit_id, String username,String total, String subtotal, String tax) {
        ContentValues cv = new ContentValues();
        cv.put(ORDER_DATE,  order_date);
        cv.put(ORDER_ID,    order_id);
        cv.put(PDV_ID,      pdv_id);
        cv.put(VISIT_ID,    visit_id);
        cv.put(USERNAME,    username);

        cv.put(METHOD_ID,"");

        cv.put(TOTAL,       total);
        cv.put(SUBTOTAL,    subtotal);
        cv.put(TAX,         tax);

        cv.put(STATUS, "ACTIVE");
        cv.put(SENT,   "NO_SENT");
        cv.put(TYPE,   "PRE_ORDER");
        cv.put(PAID,   "NO_PAID");

        cv.put(PAYMENT_DATE,"DATE");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static long insertInactive(Context context, String order_date, String order_id, String pdv_id, String visit_id, String username,String total, String subtotal, String tax) {
        ContentValues cv = new ContentValues();
        cv.put(ORDER_DATE,  order_date);
        cv.put(ORDER_ID,    order_id);
        cv.put(PDV_ID,      pdv_id);
        cv.put(VISIT_ID,    visit_id);
        cv.put(USERNAME,    username);

        cv.put(TOTAL,       total);
        cv.put(SUBTOTAL,    subtotal);
        cv.put(TAX,         tax);

        cv.put(METHOD_ID,"");

        cv.put(STATUS, "INACTIVE");
        cv.put(SENT,   "NO_SENT");
        cv.put(TYPE,   "PRE_ORDER");
        cv.put(PAID,   "NO_PAID");

        cv.put(PAYMENT_DATE,"DATE");



        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void clearOrderSent (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static int delete(Context context, String order_id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ORDER_ID + "=" + order_id, null);
    }

    public static void updateIdInOrderSent(Context context, String order_id, String newId){
        ContentValues cv = new ContentValues();
        cv.put(SENT, "SENT");
        cv.put(ORDER_ID, newId);
        DataBaseAdapter.getDB(context).update(TABLE,cv,ORDER_ID + " = ?", new String[]{order_id});
    }

    public static void updateOrderSentToNoSentPaidAndNewType(Context context, String order_id,String newType, String date_payment){
        ContentValues cv = new ContentValues();
        cv.put(SENT, "NO_SENT");

        cv.put(METHOD_ID, newType);
        cv.put(PAID, "PAID");
        cv.put(PAYMENT_DATE, date_payment);

        String type = "PRE_ORDER";
        if (newType.equalsIgnoreCase("1"))
            type    = "CASH";
        else if (newType.equalsIgnoreCase("2"))
            type    = "CREDIT";
        else if (newType.equalsIgnoreCase("3"))
            type    = "DEPOSIT";

        cv.put(TYPE, type);

        Log.d(TABLE,"New type: "+newType+" method: "+type);

        DataBaseAdapter.getDB(context).update(TABLE,cv,ORDER_ID + " = ?", new String[]{order_id});
    }

    public static void updateOrderSentToSent(Context context, String order_id){
        ContentValues cv = new ContentValues();
        cv.put(SENT, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE,cv,ORDER_ID + " = ?", new String[]{order_id});
    }

    public static String getIDActiveOrderSentInVisit(Context context, String visit_id){
        String status = "ACTIVE";
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ORDER_DATE, ORDER_ID,
                        PDV_ID, VISIT_ID, USERNAME,STATUS,SENT}, "visit_id = ? AND status = ?",
                new String[] { visit_id,status},
                null,
                null,
                ID);

        if (cursor != null && cursor.moveToFirst()) {
            String order_id = cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID));
            cursor.close();
            return order_id;
        }
        return null;
    }

    public static String getActiveOrderSentTypeInVisit(Context context, String visit_id){
        String status = "ACTIVE";
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {TYPE}, "visit_id = ? AND status = ?",
                new String[] { visit_id,status},
                null,
                null,
                ID);

        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
            cursor.close();
            return type;
        }
        return null;
    }

    public static void updateOrderSentToInactive(Context context, String order_id){
        String newStatus    = "INACTIVE";

        DataBaseAdapter.getDB(context).execSQL("UPDATE "+TABLE+" SET "
                +"  "+STATUS+" = '"+newStatus+"'"
                +" WHERE "+ORDER_ID+" = '"+order_id+"'");
    }

    public static void updateOrderSentToInactiveAndSent(Context context, String order_id){
        String newStatus    = "INACTIVE";
        String newSent      = "SENT";
        DataBaseAdapter.getDB(context).execSQL("UPDATE "+TABLE+" SET "
                +"  "+STATUS+" = '"+newStatus+"',"
                +"  "+SENT+" = "+newSent
                +" WHERE "+ORDER_ID+"= '"+order_id+"'");
    }

    public static Map<String,String> getActiveOrderSentInVisit(Context context,String visit_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ORDER_DATE, ORDER_ID,
                        PDV_ID,SENT,TYPE,PAID,TOTAL,SUBTOTAL,TAX}, "visit_id = ? AND status = ?",
                new String[] { visit_id, "ACTIVE"},
                null,
                null,
                ID);
        if (cursor != null && cursor.moveToFirst()) {
            Map<String,String> order    = new HashMap<String, String>();
            order.put(ORDER_DATE,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_DATE)));
            order.put(ORDER_ID,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
            order.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            order.put(SENT,cursor.getString(cursor.getColumnIndexOrThrow(SENT)));
            order.put(TYPE,cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));
            order.put(PAID,cursor.getString(cursor.getColumnIndexOrThrow(PAID)));

            order.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));
            order.put(SUBTOTAL,cursor.getString(cursor.getColumnIndexOrThrow(SUBTOTAL)));
            order.put(TAX,cursor.getString(cursor.getColumnIndexOrThrow(TAX)));

            order.put("date",cursor.getString(cursor.getColumnIndexOrThrow(ORDER_DATE)));

            return order;
        }
        return null;
    }

    public static List<Map<String,String>> getAllNoSentOrderSentInVisit(Context context,String visit_id){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "visit_id = ? AND sent = ?",
                new String[] { visit_id, "NO_SENT"},
                null,
                null,
                ID);

        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String, String> order = new HashMap<String, String>();
                order.put(ORDER_ID,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
                order.put(ORDER_DATE,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_DATE)));
                order.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                order.put(VISIT_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_ID)));

                order.put(METHOD_ID,cursor.getString(cursor.getColumnIndexOrThrow(METHOD_ID)));

                order.put(SENT,cursor.getString(cursor.getColumnIndexOrThrow(SENT)));
                order.put(TYPE,cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));
                order.put(STATUS,cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
                order.put(PAID,cursor.getString(cursor.getColumnIndexOrThrow(PAID)));

                order.put(TOTAL,cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)));

                order.put("date",cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_DATE)));

                list.add(order);
            }
            return list;
        }
        return null;
    }
}

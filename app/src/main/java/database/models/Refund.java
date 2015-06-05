package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class Refund extends Table {

    public static final String TABLE = "refund";

    public static final String ID               = "id";
    public static final String ORDER_DATE       = "order_date";
    public static final String ORDER_ID         = "order_id";
    public static final String PDV_ID           = "pdv_id";
    public static final String VISIT_ID         = "visit_id";
    public static final String USERNAME         = "username";
    public static final String STATUS           = "status";
    public static final String SENT             = "sent";

    public static long insert(Context context, String order_date, String order_id, String pdv_id,String visit_id, String username) {
        ContentValues cv = new ContentValues();
        cv.put(ORDER_DATE,  order_date);
        cv.put(ORDER_ID,    order_id);
        cv.put(PDV_ID,      pdv_id);
        cv.put(VISIT_ID,    visit_id);
        cv.put(USERNAME,    username);
        cv.put(STATUS,      "ACTIVE");
        cv.put(SENT,        "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void clearRefund (Context context){
        DataBaseAdapter.getDB(context).execSQL("delete from "+ TABLE);
    }

    public static int delete(Context context, String order_id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ORDER_ID + "=" + order_id, null);
    }

    public static int deleteRefundInVisit(Context context, String visit_id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, VISIT_ID + "=" + visit_id, null);
    }

    public static void updateRefundToSent(Context context, String visit_id){
        String newSent      = "SENT";
        DataBaseAdapter.getDB(context).execSQL("UPDATE "+TABLE+" SET "
                +"  "+SENT+" = "+newSent
                +" WHERE "+VISIT_ID+"= "+visit_id);
    }

    public static Map<String,String> getActiveRefundByVisit(Context context, String s_visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ORDER_DATE, ORDER_ID,
                        PDV_ID, VISIT_ID, USERNAME,STATUS,SENT}, "visit_id = ? AND status = ?",
                new String[] { s_visit_id,"ACTIVE"},
                null,
                null,
                ID);

        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> refund   = new HashMap<String,String>();
            refund.put(ORDER_DATE,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_DATE)));
            refund.put(ORDER_ID,cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID)));
            refund.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            refund.put(VISIT_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_ID)));
            refund.put(USERNAME,cursor.getString(cursor.getColumnIndexOrThrow(USERNAME)));
            refund.put(STATUS,cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
            refund.put(SENT,cursor.getString(cursor.getColumnIndexOrThrow(SENT)));

            cursor.close();

            return refund;
        }
        return null;
    }

    public static String getActiveRefundIdByVisit(Context context, String s_visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ORDER_DATE, ORDER_ID,
                        PDV_ID, VISIT_ID, USERNAME,STATUS,SENT}, "visit_id = ? AND status = ?",
                new String[] { s_visit_id,"ACTIVE"},
                null,
                null,
                ID);

        if (cursor != null && cursor.moveToFirst()) {
            String  order_id    = cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ID));
            cursor.close();
            return order_id;
        }
        return null;
    }
}

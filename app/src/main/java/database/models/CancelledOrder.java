package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 18/07/14.
 */
public class CancelledOrder extends Table {

    public static final String TABLE = "cancelled_order";

    public static final String ID                           = "id";
    public static final String ID_PDV                       = "id_pdv";
    public static final String CANCELLATION_ORDER_DATE      = "cancellation_order_date";
    public static final String DATE                         = "order_date_time";



    public static long insert(Context context,  String id,
                                                String cancellation_order_date,
                                                String date_time,
                                                String id_pdv) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(CANCELLATION_ORDER_DATE, cancellation_order_date);
        cv.put(DATE, date_time);
        cv.put(ID_PDV, id_pdv);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Map<String,String> getCancelledOrderInMap(Context context, String id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map = new HashMap<String, String>();

            map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(CANCELLATION_ORDER_DATE,cursor.getString(cursor.getColumnIndexOrThrow(CANCELLATION_ORDER_DATE)));
            map.put(DATE,cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
            map.put(ID_PDV,cursor.getString(cursor.getColumnIndexOrThrow(ID_PDV)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static int delete(Context context, String order_id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + order_id, null);
    }
}

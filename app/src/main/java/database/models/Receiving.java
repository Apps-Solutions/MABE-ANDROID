package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class Receiving extends Table {

    public static final String TABLE = "receiving";

    public static final String ID = "id";
    public static final String TIMESTAMP = "timestamp";
    public static final String PAYMENT_TYPE_ID = "payment_type_id";
    public static final String SESSION_ID = "session_id";
    public static final String SUPPLIER_ID = "supplier_id";
    public static final String RECEIPT_ID = "receipt_id";

    private int id;
    private String timestamp;
    private String paymentType;
    private Session session;
    private Bitmap receipt;

    public static long insert(Context context, int payment_type_id, int session_id, int supplier_id, int receipt_id){

        ContentValues cv = new ContentValues();
        cv.put(PAYMENT_TYPE_ID, payment_type_id);
        cv.put(SESSION_ID, session_id);
        cv.put(SUPPLIER_ID, supplier_id);
        cv.put(RECEIPT_ID, receipt_id);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context, int id, int payment_type_id, int session_id, int supplier_id, int receipt_id){

        ContentValues cv = new ContentValues();
        if (payment_type_id != -1)
            cv.put(PAYMENT_TYPE_ID, payment_type_id);
        if (session_id != -1)
            cv.put(SESSION_ID, session_id);
        if (supplier_id != -1)
            cv.put(SUPPLIER_ID, supplier_id);
        if (receipt_id != -1)
            cv.put(RECEIPT_ID, receipt_id);

        return DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static Cursor All(Context context){
        Cursor mc = DataBaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(mc !=null)
            mc.moveToFirst();
        return mc;
    }

    public Receiving(int id, String timestamp, String paymentType, Session session, Bitmap receipt) {

        this.id = id;
        this.timestamp = timestamp;
        this.paymentType = paymentType;
        this.session = session;
        this.receipt = receipt;
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Session getSession() {
        return session;
    }

    public Bitmap getReceipt() {
        return receipt;
    }

    public static Receiving getReceiving(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP));
            int paymentTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_TYPE_ID));
            String paymentType = PaymentType.getName(context, paymentTypeId);
            int sessionId = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_ID));
            Session session = Session.getSession(context, sessionId);
            int receiptId = cursor.getInt(cursor.getColumnIndexOrThrow(RECEIPT_ID));
            Bitmap receipt = Receipt.getImage(context, receiptId);

            cursor.close();

            return new Receiving(id, timestamp, paymentType, session, receipt);
        }
        return null;
    }
}

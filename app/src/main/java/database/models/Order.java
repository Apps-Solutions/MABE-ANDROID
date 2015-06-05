package database.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 18/07/14.
 */
public class Order extends Table {

    public static final String TABLE = "orders";

    public static final String ID               = "id";
    public static final String ORDER_DATE       = "order_date";
    public static final String REQUIRED_DATE    = "required_date";
    public static final String SHIPPED_DATE     = "shipped_date";
    public static final String CUSTOMER_ID      = "customer_id";
    public static final String SESSION_ID       = "session_id";
    public static final String ORDER_STATUS_ID  = "order_status_id";
    public static final String RECEIPT_ID       = "receipt_id";

    private int id;
    private String orderDate;
    private String requiredDate;
    private String shippedDate;
    private int customerId;
    private Session session;
    private String orderStatus;
    private Bitmap receipt;


    public Order(int id, String orderDate, String requiredDate, String shippedDate, int customerId, Session session, String orderStatus, Bitmap receipt) {

        this.id = id;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.shippedDate = shippedDate;
        this.customerId = customerId;
        this.session = session;
        this.orderStatus = orderStatus;
        this.receipt = receipt;
    }

    public static long insert(Context context, String required_date, String shipped_date, int customer_id, int session_id, int order_status_id, int receipt_id) {
        ContentValues cv = new ContentValues();
        cv.put(REQUIRED_DATE, required_date);
        cv.put(SHIPPED_DATE, shipped_date);
        cv.put(CUSTOMER_ID, customer_id);
        cv.put(SESSION_ID, session_id);
        cv.put(ORDER_STATUS_ID, order_status_id);
        cv.put(RECEIPT_ID, receipt_id);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public int getId() {
        return id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getRequiredDate() {
        return requiredDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Session getSession() {
        return session;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Bitmap getReceipt() {
        return receipt;
    }

    public static Order getOrder(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(ORDER_DATE));
            String requiredDate = cursor.getString(cursor.getColumnIndexOrThrow(REQUIRED_DATE));
            String shippedDate = cursor.getString(cursor.getColumnIndexOrThrow(SHIPPED_DATE));
            int customerId = cursor.getInt(cursor.getColumnIndexOrThrow(CUSTOMER_ID));
            int sessionId = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_ID));
            Session session = Session.getSession(context, sessionId);
            int orderStatusId = cursor.getInt(cursor.getColumnIndexOrThrow(ORDER_STATUS_ID));
            String orderStatus = OrderStatus.getStatus(context, orderStatusId);
            int receiptId = cursor.getInt(cursor.getColumnIndexOrThrow(RECEIPT_ID));
            Bitmap receipt = Receipt.getImage(context, receiptId);

            cursor.close();

            return new Order(id, orderDate, requiredDate, shippedDate, customerId, session, orderStatus, receipt);
        }
        return null;
    }
}

package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 18/07/14.
 */
public class OrderDetail extends Table {

    public static final String TABLE = "order_detail";

    public static final String ID = "id";
    public static final String ORDER_ID = "order_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String UNIT_PRICE = "unit_price";
    public static final String QUANTITY = "quantity";
    public static final String DISCOUNT = "discount";

    private int id;
    private int orderId;
    private int productId;
    private float unitPrice;
    private int quantity;
    private float discount;

    public OrderDetail(int id, int orderId, int productId, float unitPrice, int quantity, float discount) {

        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
    }


    public static long insert(Context context, int order_id, int product_id, float unit_price, int quantity, float discount) {
        ContentValues cv = new ContentValues();
        cv.put(ORDER_ID, order_id);
        cv.put(PRODUCT_ID, product_id);
        cv.put(UNIT_PRICE, unit_price);
        cv.put(QUANTITY, quantity);
        cv.put(DISCOUNT, discount);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getDiscount() {
        return discount;
    }

    public static OrderDetail getOrderDetail(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(ORDER_ID));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
            float unitPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(UNIT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
            float discount = cursor.getFloat(cursor.getColumnIndexOrThrow(DISCOUNT));

            cursor.close();

            return new OrderDetail(id, orderId, productId, unitPrice, quantity, discount);
        }

        return null;
    }

    public static ArrayList<OrderDetail> getBySaleId(Context context, int orderId) {

        ArrayList<OrderDetail> list = new ArrayList<OrderDetail>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ORDER_ID + "=" + orderId, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
                float itemCost = cursor.getFloat(cursor.getColumnIndexOrThrow(UNIT_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
                float discount = cursor.getFloat(cursor.getColumnIndexOrThrow(DISCOUNT));

                OrderDetail item = new OrderDetail(id, orderId, productId, itemCost, quantity, discount);
                list.add(item);

            }
            cursor.close();
        }
        return list;
    }
}

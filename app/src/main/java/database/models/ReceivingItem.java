package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class ReceivingItem extends Table {

    public static final String TABLE = "receiving_item";

    public static final String ID = "id";
    public static final String RECEIVING_ID = "receiving_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String QUANTITY = "quantity";
    public static final String ITEM_COST = "item_cost";

    private int id;
    private int receivingId;
    private int productId;
    private int quantity;
    private double itemCost;

    public ReceivingItem(int id, int receivingId, int productId, int quantity, double itemCost) {

        this.id = id;
        this.receivingId = receivingId;
        this.productId = productId;
        this.quantity = quantity;
        this.itemCost = itemCost;
    }

    public static long insert(Context context, int receiving_id, int product_id, int quantity, double item_cost) {
        ContentValues cv = new ContentValues();
        cv.put(RECEIVING_ID, receiving_id);
        cv.put(PRODUCT_ID, product_id);
        cv.put(QUANTITY, quantity);
        cv.put(ITEM_COST, item_cost);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Cursor All(Context context) {
        Cursor mc = DataBaseAdapter.getDB(context).rawQuery("select * from "+TABLE,null);
        if(mc !=null)
            mc.moveToFirst();
        return mc;
    }

    public static ArrayList<ReceivingItem> getByReceivingId(Context context, int receivingId) {

        ArrayList<ReceivingItem> list = new ArrayList<ReceivingItem>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, RECEIVING_ID + "=" + receivingId, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
                double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow(ITEM_COST));

                ReceivingItem item = new ReceivingItem(id, receivingId, productId, quantity, itemCost);
                list.add(item);
            }
            cursor.close();
        }
        return list;
    }

    public static void deleteByReceivingId(Context context, int receivingId) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, RECEIVING_ID + "=" + receivingId, null, null, null, null);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
            }
            cursor.close();
        }
    }

    public int getId() {
        return id;
    }

    public int getReceivingId() {
        return receivingId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getItemCost() {
        return itemCost;
    }

    public static ReceivingItem getReceivingItem(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int receivingId = cursor.getInt(cursor.getColumnIndexOrThrow(RECEIVING_ID));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
            double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow(ITEM_COST));

            cursor.close();
            return new ReceivingItem(id, receivingId, productId, quantity, itemCost);
        }
        return null;
    }
}

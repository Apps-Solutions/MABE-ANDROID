package OfflineMode;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.models.PendingOrder;
import database.models.PendingOrderProduct;

/**
 * Created by raymundo.piedra on 01/03/15.
 */
public class OfflineManager {

    public static void savePendingOrders(Context context, JSONObject pending_orders_info){
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
}

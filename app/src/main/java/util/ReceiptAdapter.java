package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.text.DecimalFormat;
import java.util.List;

import database.models.OrderDetail;
import database.models.Product;
import database.models.ReceivingItem;
import database.models.SaleItem;

/**
 * Created by juanc.jimenez on 03/07/14.
 */
public class ReceiptAdapter extends BaseAdapter {

    //OPCIONES PARA FRAGMENT RECEIPT
    public static final int RECEIVING = 1;
    public static final int SALE      = 2;
    public static final int ORDER     = 3;
    public static final int CASHING   = 4;

    Context context;
    List products;
    int receiptType;

    public ReceiptAdapter(Context context, int receiptType, List products){

        this.context = context;
        this.products = products;
        this.receiptType = receiptType;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_receipt, viewGroup, false);

            holder = new ViewHolder();
            holder.productQuantity = (TextView) view.findViewById(R.id.product_quantity);
            holder.productDescription = (TextView) view.findViewById(R.id.product_description);
            holder.productUnitPrice = (TextView) view.findViewById(R.id.product_unit_price);
            holder.productPrice = (TextView) view.findViewById(R.id.product_price);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        Product product;
        String description;
        int quantity;
        double unitPrice, price;

        switch (receiptType) {
            case RECEIVING:
                ReceivingItem receivingItem = (ReceivingItem) getItem(position);
                product = Product.getProduct(context, receivingItem.getProductId());
                description = product.getName() + "\n" + product.getDescription();
                quantity = receivingItem.getQuantity();
                unitPrice = receivingItem.getItemCost();
                price = quantity * unitPrice;
                break;

            case SALE:
                SaleItem saleItem = (SaleItem) getItem(position);
                product = Product.getProduct(context, saleItem.getProductId());
                description = product.getName() + "\n" + product.getDescription();
                quantity = saleItem.getQuantity();
                unitPrice = saleItem.getPriceUnit();
                price = quantity * unitPrice;
                break;

            case ORDER:
                OrderDetail orderDetail = (OrderDetail) getItem(position);
                product = Product.getProduct(context, orderDetail.getProductId());
                description = product.getName() + "\n" + product.getDescription();
                quantity = orderDetail.getQuantity();
                unitPrice = orderDetail.getUnitPrice();
                price = quantity * unitPrice;
                break;

            default:
                quantity = 0;
                description = "";
                unitPrice = 0.0;
                price = 0.0;
                break;
        }

        DecimalFormat form = new DecimalFormat("0.00");
        holder.productQuantity.setText(String.valueOf(quantity));
        holder.productDescription.setText(description);
        holder.productUnitPrice.setText(form.format(unitPrice));
        holder.productPrice.setText(form.format(price));

        return view;
    }

    static class ViewHolder {

        TextView productQuantity;
        TextView productDescription;
        TextView productUnitPrice;
        TextView productPrice;
    }
}

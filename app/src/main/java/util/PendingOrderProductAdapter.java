package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by raymundo.piedra on 04/02/15.
 */
public class PendingOrderProductAdapter extends BaseAdapter {

    String TAG = "FILTER_ADAPTER_LOG";

    private Context context;
    private List<Map<String,String>>    products;
    public InteractWithProduct          listener;

    public PendingOrderProductAdapter(Context context, List<Map<String,String>> products){
        super();
        this.context    = context;
        this.products   = products;
    }

    class MyViewHolder{
        TextView    txt_pending_order_product_name,txt_pending_order_product_quantity,txt_pending_order_product_price;
        int position;
        int productId;
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
    public int getCount() {
        return products.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyViewHolder holder;
        if (row == null) {
            holder = new MyViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_pending_order_product, parent, false);
            holder.txt_pending_order_product_name       = (TextView) row.findViewById(R.id.txt_pending_order_product_name);
            holder.txt_pending_order_product_quantity   = (TextView) row.findViewById(R.id.txt_pending_order_product_quantity);
            holder.txt_pending_order_product_price      = (TextView) row.findViewById(R.id.txt_pending_order_product_price);

            row.setTag(holder);
        }else{
            holder = (MyViewHolder) row.getTag();
        }

        Map<String,String> item = products.get(position);
        holder.position     = position;
        holder.productId    = Integer.parseInt(item.get("id"));
        holder.txt_pending_order_product_name.setText(item.get("name"));
        holder.txt_pending_order_product_quantity.setText(item.get("quantity"));
        holder.txt_pending_order_product_price.setText("$ "+item.get("price"));

        Log.d(TAG, "Name :" + item.get("name"));
        Log.d(TAG,"Position :"+position);

        return row;
    }
    public void removeElement(Map<String,String> prod){
        notifyDataSetChanged();
    }

    public interface InteractWithProduct{
        public void showProductDetail(int position);
    }
}

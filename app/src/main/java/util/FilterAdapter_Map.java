package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.models.Product;

/**
 * Created by dmolinero on 26/06/14.
 */
public class FilterAdapter_Map extends BaseAdapter {

    String TAG = "FILTER_ADAPTER_LOG";

    private Context context;
    private List<Map<String,String>> products;
    private List<Map<String,String>> products_copy;

    public FilterAdapter_Map(Context context, List<Map<String,String>> products){
        super();
        this.context    = context;
        this.products   = products;
        products_copy   = new ArrayList<Map<String, String>>(this.products);
    }

    class MyViewHolder{
        TextView productName;
        TextView productDescription;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyViewHolder holder;
        if (row == null) {
            holder = new MyViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_filter, parent, false);
            holder.productName = (TextView) row.findViewById(R.id.filter_product_name);
            holder.productDescription = (TextView) row.findViewById(R.id.filter_product_description);


            row.setTag(holder);
        }else{
            holder = (MyViewHolder) row.getTag();
        }

        Map<String,String> item = products.get(position);
        holder.position     = position;
        holder.productId    = Integer.parseInt(item.get("id"));
        holder.productName.setText(item.get("name"));
        holder.productDescription.setText(item.get("description"));

        return row;
    }

    public void filterProducts(String query){

        query   = query.toLowerCase();
        products.clear();

        if (query.isEmpty())
            products.addAll(products_copy);
        else{
            for (Map<String,String> item :products_copy){
                String prod_name = (item.get("name")).toLowerCase();
                if(prod_name.startsWith(query)){
                    if (!products.contains(item)){
                        products.add(item);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void removeElement(Map<String,String> prod){
        products_copy.remove(prod);
        notifyDataSetChanged();
    }

    public void addElement(Map<String,String> product) {
        products_copy.add(product);
        notifyDataSetChanged();
    }
}
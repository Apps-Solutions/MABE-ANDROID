package util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.List;
import java.util.Map;

import database.models.Product;
import database.models.WholeSalesPricesProduct;


public class WholeSalePricesAdapter extends BaseAdapter {

    String TAG = "WHOLESALE_ADAPTER_LOG";

    private Context context;
    private List<Map<String,String>>    products;
    public InteractWithProduct          listener;

    public WholeSalePricesAdapter(Context context, List<Map<String,String>> products){
        super();
        this.context    = context;
        this.products   = products;
    }

    class MyViewHolder{
        TextView    txt_whp_product_name;
        EditText    edt_whp_price;
        Button      edt_whp_delete;
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
            row = inflater.inflate(R.layout.item_wholesale_prices, parent, false);
            holder.txt_whp_product_name   = (TextView) row.findViewById(R.id.txt_whp_product_name);
            holder.edt_whp_price          = (EditText) row.findViewById(R.id.edt_whp_price);
            holder.edt_whp_delete         = (Button) row.findViewById(R.id.edt_whp_delete);

            row.setTag(holder);
        }else{
            holder = (MyViewHolder) row.getTag();
        }

        final Map<String,String> item = products.get(position);
        holder.position     = position;
        holder.productId    = Integer.parseInt(item.get("id_product"));
        holder.txt_whp_product_name.setText(item.get("name"));
        holder.edt_whp_price.setText(item.get("price"));

        holder.edt_whp_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG,"OnTextChange: "+charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG,"AfterChange: "+editable.toString());

                String str = editable.toString();

                if (str.isEmpty())
                    str = "0";

                item.put("price",str);
                WholeSalesPricesProduct.updatePrice(context,item.get("id"),str);

            }
        });

        holder.edt_whp_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.remove(position);
                WholeSalesPricesProduct.delete(context,Integer.parseInt(item.get("id")));
                notifyDataSetChanged();
            }
        });

        Log.d(TAG,"Name :"+item.get("name"));
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
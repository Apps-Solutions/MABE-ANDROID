package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.List;
import java.util.Map;

import database.models.Product;


public class ShelfReviewProductAdapter extends BaseAdapter {

    String TAG = "FILTER_ADAPTER_LOG";

    private Context context;
    private List<Map<String,String>>    products;
    public InteractWithProduct          listener;

    public ShelfReviewProductAdapter(Context context, List<Map<String,String>> products){
        super();
        this.context    = context;
        this.products   = products;
    }

    class MyViewHolder{
        TextView    txt_shelf_review_product_name;
        Button      btn_shelf_review_detail,btn_shelf_review_delete;
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
            row = inflater.inflate(R.layout.item_shelf_review_product, parent, false);
            holder.txt_shelf_review_product_name    = (TextView) row.findViewById(R.id.txt_shelf_review_product_name);
            holder.btn_shelf_review_detail          = (Button) row.findViewById(R.id.btn_shelf_review_detail);
            holder.btn_shelf_review_delete          = (Button) row.findViewById(R.id.btn_shelf_review_delete);

            row.setTag(holder);
        }else{
            holder = (MyViewHolder) row.getTag();
        }

        Map<String,String> item = products.get(position);
        holder.position     = position;
        holder.productId    = Integer.parseInt(item.get("id"));
        holder.txt_shelf_review_product_name.setText(item.get("name"));

        holder.btn_shelf_review_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showProductDetail(position);
            }
        });

        holder.btn_shelf_review_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.remove(position);
                notifyDataSetChanged();
            }
        });

        Log.d(TAG,"Name :"+item.get("name"));
        Log.d(TAG,"Posotion :"+position);

        return row;
    }
    public void removeElement(Map<String,String> prod){
        notifyDataSetChanged();
    }

    public interface InteractWithProduct{
        public void showProductDetail(int position);
    }
}
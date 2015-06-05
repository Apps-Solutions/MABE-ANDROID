package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellcom.tracker.FragmentInventory;
//import com.sellcom.tracker.FragmentInventoryInOut;
import com.sellcom.tracker.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import database.models.Product;
import database.models.ProductPic;
import database.models.Warehouse;


public class WarehouseAdapter  extends BaseAdapter{

    public static String TAG = "WarehouseAdapter";

    private Context context;
    private List<Map<String,String>> products;

    OnProductDeletedListener mCallback;

    public WarehouseAdapter(Context context, List<Map<String,String>> products) {

        this.context = context;
        this.products = products;
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

        final ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_warehouse, parent, false);

            holder = new ViewHolder();

            holder.edt_ware_product_name    = (TextView) convertView.findViewById(R.id.edt_ware_product_name);
            holder.edt_ware_warehouse       = (TextView) convertView.findViewById(R.id.edt_ware_warehouse);
            holder.edt_ware_shelf           = (TextView) convertView.findViewById(R.id.edt_ware_shelf);
            holder.edt_ware_exhibition      = (TextView) convertView.findViewById(R.id.edt_ware_exhibition);
            holder.btn_ware_delete          = (Button) convertView.findViewById(R.id.btn_ware_delete);

            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Map<String,String> item = products.get(position);

        holder.edt_ware_product_name.setText(item.get("name_product"));

        holder.edt_ware_warehouse.setText(item.get("ware_inv").concat("%"));
        holder.edt_ware_shelf.setText(item.get("shelf_inv").concat("%"));
        holder.edt_ware_exhibition.setText(item.get("exhib_inv").concat("%"));

        holder.btn_ware_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Click");
                final int id = Integer.parseInt(item.get("id"));
                Warehouse.delete(context,id);
                mCallback.onProductDeleted();
            }
        });

        return convertView;
    }

    public interface OnProductDeletedListener {
        public void onProductDeleted();
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {
        mCallback = listener;
    }

    static class ViewHolder {

        TextView edt_ware_product_name,edt_ware_warehouse,edt_ware_shelf,edt_ware_exhibition;

        Button btn_ware_delete;
    }
}

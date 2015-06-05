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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.List;
import java.util.Map;

/**
 * Created by jonathan.vazquez on 27/02/15.
 */
public class InventoryQuestionnaireAdapter extends BaseAdapter {

    String TAG = "Inventory Adapter";

    private Context context;
    private List<Map<String,String>> products;

    public Map<String,String> item;

    private List<Map<String,String>> productsR;
    public Map<String,String> itemR;

    public InventoryQuestionnaireAdapter(Context context, List<Map<String,String>> products){
        super();
        this.context    = context;
        this.products   = products;
    }

    class ViewHolder{
        private TextView txt_inv_product;
        private EditText et_inv_existence, et_inv_existence_sys, et_inv_difference, et_inv_difference_comment;
        private ImageButton btn_inv_del_product;
        private Switch swt_adjust_made;

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
        final ViewHolder holder;

        if (row == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_inventory_questions, parent, false);

            holder.txt_inv_product              = (TextView) row.findViewById(R.id.txt_inv_product);
            holder.btn_inv_del_product          = (ImageButton) row.findViewById(R.id.btn_inv_del_product);
            holder.et_inv_existence             = (EditText) row.findViewById(R.id.et_inv_existence);
            holder.et_inv_existence_sys         = (EditText) row.findViewById(R.id.et_inv_existence_sys);
            holder.et_inv_difference            = (EditText) row.findViewById(R.id.et_inv_difference);
            holder.et_inv_difference.setText("0");
            holder.et_inv_difference_comment    = (EditText) row.findViewById(R.id.et_inv_difference_comment);

            holder.swt_adjust_made              = (Switch)row.findViewById(R.id.swt_adjust_made);

            row.setTag(holder);

        }else{
            holder = (ViewHolder) row.getTag();
        }

        item = products.get(position);

        holder.position     = position;
        holder.productId    = Integer.parseInt(item.get("id"));
        holder.txt_inv_product.setText(item.get("name"));

        holder.et_inv_existence.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, "OnTextChange: " + charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG,"AfterChange: "+editable.toString());
                String str = editable.toString();

                if (str.isEmpty())
                    str = "0";

                item.put("stock", str);

                int system_stock    = Integer.parseInt(item.get("system_stock"));
                int stock           = Integer.parseInt(item.get("stock"));

                int total = system_stock - stock;

                holder.et_inv_difference.setText(Integer.toString(total));

                Log.d(TAG, "Total: " + total);
            }
        });

        holder.et_inv_existence_sys.addTextChangedListener(new TextWatcher() {
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

                item.put("system_stock", str);
                int system_stock    = Integer.parseInt(item.get("system_stock"));
                int stock           = Integer.parseInt(item.get("stock"));

                int total = system_stock - stock;

                holder.et_inv_difference.setText(Integer.toString(total));

                Log.d(TAG, "Total: " + total);
            }
        });

        holder.et_inv_difference_comment.addTextChangedListener(new TextWatcher() {
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
                    str = "";

                item.put("comment", str);
            }
        });

        holder.btn_inv_del_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.swt_adjust_made.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    item.put("change", "1");
                }
                else {
                    item.put("change", "0");
                }
            }
        });

        Log.d(TAG, "Name :" + item.get("name"));
        Log.d(TAG,"Position :"+position);

        return row;
    }
}
package util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by juanc.jimenez on 15/07/14.
 */
public class OrdersAdapter_Map extends BaseAdapter implements View.OnClickListener{

    String TAG  = "ORDER_ADAPTER_TAG";

    Context                     context;
    public List<Map<String,String>>    products;
    UpdateFooterInterface       footerUpdater;
    OnProductDeletedListener    productDeletedListener;

    public boolean                     hideButtons;

    public OrdersAdapter_Map(Context context, List<Map<String,String>> products) {

        hideButtons     = true;
        this.context    = context;
        this.products   = products;
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
    public View getView(final int position, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        view = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_orders, viewGroup, false);

            viewHolder = new ViewHolder();

            viewHolder.deleteButton             = (ImageButton) view.findViewById(R.id.delete_button);

            viewHolder.txt_product_name         = (TextView) view.findViewById(R.id.txt_product_name);
            viewHolder.txt_product_price        = (TextView) view.findViewById(R.id.txt_product_price);

            viewHolder.edt_product_pieces       = (EditText) view.findViewById(R.id.edt_product_pieces);
            viewHolder.edt_product_boxes        = (EditText) view.findViewById(R.id.edt_product_boxes);
            viewHolder.edt_product_total        = (EditText) view.findViewById(R.id.edt_product_total);

            viewHolder.deleteButton.setOnClickListener(this);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        final Map<String,String> item = products.get(position);

        viewHolder.txt_product_name.setText(item.get("name"));
        viewHolder.txt_product_price.setText("$"+item.get("price"));

        if (!item.get("pieces").equalsIgnoreCase("0"))
            viewHolder.edt_product_pieces.setText(item.get("pieces"));
        else
            viewHolder.edt_product_pieces.setText("");

        if (!item.get("boxes").equalsIgnoreCase("0"))
            viewHolder.edt_product_boxes.setText(item.get("boxes"));
        else
            viewHolder.edt_product_boxes.setText("");

        if (!item.get("total_pieces").equalsIgnoreCase("0"))
            viewHolder.edt_product_total.setText(item.get("total_pieces"));
        else
            viewHolder.edt_product_total.setText("0");

        if (hideButtons)
            viewHolder.deleteButton.setVisibility(View.GONE);
        else
            viewHolder.deleteButton.setVisibility(View.VISIBLE);

        viewHolder.deleteButton.setTag(viewHolder);
        viewHolder.position = position;

        viewHolder.edt_product_pieces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String str = editable.toString();

                if (str.isEmpty())
                    str = "0";

                viewHolder.pieces_in_pieces = Integer.parseInt(str);
                viewHolder.total_pieces = viewHolder.pieces_in_pieces + viewHolder.pieces_in_boxes;
                item.put("pieces",""+viewHolder.pieces_in_pieces);
                int total = Integer.parseInt(item.get("pieces"))+ (12*(Integer.parseInt(item.get("boxes"))));
                item.put("total_pieces",""+total);

                viewHolder.edt_product_total.setText(item.get("total_pieces"));
                item.put("total_price",""+(total*Float.parseFloat(item.get("price"))));

                footerUpdater.updateFooter();
            }
        });

        viewHolder.edt_product_boxes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String str = editable.toString();

                if (str.isEmpty())
                    str = "0";

                viewHolder.pieces_in_boxes = Integer.parseInt(str);
                item.put("boxes",""+viewHolder.pieces_in_boxes);

                int total = Integer.parseInt(item.get("pieces"))+ (12*(Integer.parseInt(item.get("boxes"))));
                item.put("total_pieces",""+total);
                item.put("total_price",""+(total*Float.parseFloat(item.get("price"))));

                viewHolder.edt_product_total.setText(item.get("total_pieces"));

                footerUpdater.updateFooter();
            }
        });

        viewHolder.edt_product_total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //item.put("total_pieces",""+viewHolder.total_pieces);
                //item.put("total_price",""+(viewHolder.total_pieces*Float.parseFloat(item.get("price"))));
                //footerUpdater.updateFooter();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {

        ViewHolder holder = (ViewHolder) view.getTag();
        int id = view.getId();
        if (id == R.id.delete_button) {
            Map<String,String> toDelete = products.get(holder.position);
            deleteProduct(toDelete, holder.position);
            holder  = null;
        } else {

        }
        footerUpdater.updateFooter();
    }

    public void deleteProduct(Map<String,String> product, int position) {

        products.remove(position);
        hideButtons = true;
        notifyDataSetChanged();

        productDeletedListener.onProductDeleted(product);
        footerUpdater.updateFooter();
    }

    public void deleteAllProducts(){
        for (int i=0; i<products.size(); i++){
            Map<String,String> toDelete = products.get(i);
            deleteProduct(toDelete, i);
        }
        footerUpdater.updateFooter();
    }

    public interface UpdateFooterInterface{

        public void updateFooter();
    }

    public interface OnProductDeletedListener {
        public void onProductDeleted(Map<String,String> product);
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {

        productDeletedListener = listener;
    }

    public void setFooterUpdater(UpdateFooterInterface listener) {

        footerUpdater = listener;
    }

    class ViewHolder{

        ImageButton deleteButton;

        TextView    txt_product_name;
        TextView    txt_product_price;

        EditText    edt_product_pieces;
        EditText    edt_product_boxes;
        EditText    edt_product_total;

        int         position;
        int         pieces_in_pieces,pieces_in_boxes;
        int         total_pieces;
        int         total_price;
    }
}

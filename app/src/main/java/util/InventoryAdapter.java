package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
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

import database.models.Product;
import database.models.ProductPic;

/**
 * Created by dmolinero on 26/06/14.
 */
public class InventoryAdapter  extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<InventoryProduct> products;
    boolean isEntry;

    OnProductDeletedListener mCallback;

    public InventoryAdapter(Context context, List<InventoryProduct> products, boolean isEntry) {

        this.context = context;
        this.products = products;
        this.isEntry = isEntry;
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
            convertView = inflater.inflate(R.layout.item_inventory, parent, false);

            holder = new ViewHolder();

            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productBrand = (TextView) convertView.findViewById(R.id.product_brand);
            holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.product_delete);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);

            holder.incButton = (ImageButton) convertView.findViewById(R.id.incButton);
            holder.decButton = (ImageButton) convertView.findViewById(R.id.decButton);
            holder.quantity = (EditText) convertView.findViewById(R.id.numberEditText);

            holder.deleteButton.setOnClickListener(this);

            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.quantity.setTag(position + 1);
        holder.incButton.setTag(position);
        holder.decButton.setTag(position);
        holder.deleteButton.setId(position);

        final InventoryProduct item = products.get(position);

        holder.position = position;
        holder.productId = item.getId();
        holder.productName.setText(item.getName());
        holder.productBrand.setText(item.getBrand());
        DecimalFormat form = new DecimalFormat("0.00");
        holder.productPrice.setText("$" + form.format(item.getPrice()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        holder.incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = holder.quantity.getText().toString();
                int currentQuantity = 0;
                if (!text.isEmpty())
                    currentQuantity = Integer.parseInt(text);

                if (isEntry)
                    currentQuantity++;
                else if (currentQuantity < item.getStock())
                    currentQuantity++;

                text = String.valueOf(currentQuantity);
                holder.quantity.setText(text);
            }
        });

        holder.decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = holder.quantity.getText().toString();
                int currentQuantity = 0;
                if (!text.isEmpty())
                    currentQuantity = Integer.parseInt(text);

                if (currentQuantity > 0)
                    currentQuantity--;
                else return;

                text = String.valueOf(currentQuantity);
                holder.quantity.setText(text);
            }
        });

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString();
                int currentQuantity = 0;
                if (!text.isEmpty())
                    currentQuantity = Integer.parseInt(text);

                int position = (Integer) holder.quantity.getTag();
                products.get(position - 1).setQuantity(currentQuantity);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*if (FragmentInventoryInOut.hideButtons)
            holder.deleteButton.setVisibility(View.GONE);
        else
            holder.deleteButton.setVisibility(View.VISIBLE);
        */
        new AsyncTask<ViewHolder, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(ViewHolder... viewHolders) {
                viewHolder = viewHolders[0];
                return ProductPic.getMainForProduct(context, viewHolder.productId);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if (bitmap == null) {
                    viewHolder.productImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    return;
                }

                if (viewHolder.position == position)
                    viewHolder.productImage.setImageBitmap(bitmap);
            }
        }.execute(holder);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        int position = view.getId();
        Product toRemove = products.get(position);
        products.remove(position);
        notifyDataSetChanged();
        mCallback.onProductDeleted(toRemove);
    }

    public interface OnProductDeletedListener {
        public void onProductDeleted(Product product);
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {
        mCallback = listener;
    }

    static class ViewHolder {

        TextView productName;
        TextView productBrand;
        TextView productPrice;
        ImageView productImage;

        ImageButton incButton;
        ImageButton decButton;
        EditText quantity;

        ImageButton deleteButton;
        int position;
        int productId;
    }
}

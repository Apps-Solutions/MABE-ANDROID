package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellcom.tracker.FragmentProducts;
import com.sellcom.tracker.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import database.models.Product;
import database.models.ProductPic;

/**
 * Created by juanc.jimenez on 05/06/14.
 */
public class ProductsAdapter extends BaseAdapter implements View.OnClickListener{

    private Context context;
    private List<Product> products;
    private List<Product> copyOfProducts;
    OnProductDeletedListener mCallback;

    public ProductsAdapter(Context context, List<Product> products) {

        this.context = context;
        this.products = products;
        copyOfProducts = new ArrayList<Product>();
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

        ViewHolder holder;
        Boolean isScrolling = FragmentProducts.isScrolling;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_products, parent, false);

            holder = new ViewHolder();
            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productBrand = (TextView) convertView.findViewById(R.id.product_brand);
            holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
            holder.productStock = (TextView) convertView.findViewById(R.id.product_stock);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.delete_button);

            holder.deleteButton.setId(position);
            holder.deleteButton.setOnClickListener(this);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product item = products.get(position);
        holder.position = position;
        holder.productId = item.getId();
        holder.productName.setText(item.getName());
        holder.productBrand.setText(item.getBrand());
        DecimalFormat form = new DecimalFormat("0.00");
        holder.productPrice.setText("$" + form.format(item.getPrice()));
        holder.productStock.setText(String.valueOf(item.getStock()));

        if (FragmentProducts.hideButtons)
            holder.deleteButton.setVisibility(View.GONE);
        else
            holder.deleteButton.setVisibility(View.VISIBLE);

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

        Animation animation;
        if (isScrolling) {
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_right);
            animation.setDuration(400);
            convertView.startAnimation(animation);
        }

        return convertView;
    }

    public void filterProducts(String query) {

        query = query.toLowerCase();

        copyOfProducts = Product.getAll(context);
        products.clear();

        if (query.isEmpty()) {
            products.addAll(copyOfProducts);
        } else {
            for (Product item : copyOfProducts)
            {
                if (item.getName().toLowerCase().contains(query)
                        || item.getBrand().toLowerCase().contains(query)
                        || item.getCode().contains(query)) {
                    if (!products.contains(item))
                        products.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

        int position = view.getId();
        Product.delete(context, products.get(position).getId());
        products.remove(position);
        notifyDataSetChanged();

        mCallback.onProductDeleted(position);
    }

    public interface OnProductDeletedListener {
        public void onProductDeleted(int position);
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {
        mCallback = listener;
    }

    static class ViewHolder {

        TextView productName;
        TextView productBrand;
        TextView productPrice;
        TextView productStock;
        ImageView productImage;
        ImageButton deleteButton;
        int position;
        int productId;
    }
}

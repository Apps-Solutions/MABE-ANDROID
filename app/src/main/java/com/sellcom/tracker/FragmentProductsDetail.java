package com.sellcom.tracker;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import database.models.Product;
import database.models.ProductPic;

/**
 * Created by juanc.jimenez on 06/06/14.
 */
public class FragmentProductsDetail extends Fragment implements View.OnClickListener{

    public final static String TAG = "detail";

    private TextView productName, productBrand, productCode, productPrice, productCost, productTax, productStock, productCentralStock, productDescription, productCategory;
    private ImageView productPhoto;
    private LinearLayout productPhotoList, photosLayout;
    private LoadProductImages loadProductImages;
    private Product product;

    public static FragmentProductsDetail newInstance(int productId) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", productId);
        FragmentProductsDetail productsDetail = new FragmentProductsDetail();
        productsDetail.setArguments(bundle);
        return productsDetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            int id = bundle.getInt("id", 0);
            product = Product.getProduct(getActivity(), id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_detail, container, false);

        if (view != null) {

            productName = (TextView) view.findViewById(R.id.product_name);
            productBrand = (TextView) view.findViewById(R.id.product_brand);
            productCode = (TextView) view.findViewById(R.id.product_code);
            productPrice = (TextView) view.findViewById(R.id.product_price);
            productCost = (TextView) view.findViewById(R.id.product_cost);
            productTax = (TextView) view.findViewById(R.id.product_tax);
            productStock = (TextView) view.findViewById(R.id.product_stock);
            productCentralStock = (TextView) view.findViewById(R.id.product_central_stock);
            productDescription = (TextView) view.findViewById(R.id.product_description);
            productCategory = (TextView) view.findViewById(R.id.product_category);
            productPhoto = (ImageView) view.findViewById(R.id.product_photo);
            productPhotoList = (LinearLayout) view.findViewById(R.id.product_photo_list);
            photosLayout = (LinearLayout) view.findViewById(R.id.photos_layout);

            updateDetail(product);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.edit, menu);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void updateDetail(Product product) {

        if (product != null) {

            DecimalFormat form = new DecimalFormat("0.00");

            String tab = "\t";
            String price = getString(R.string.product_price_indicator) + tab + "$" + form.format(product.getPrice());
            String cost = getString(R.string.product_public_price_indicator) + tab + "$" + form.format(product.getCost());
            String tax = getString(R.string.product_tax_indicator) + tab + "%" + form.format(product.getTax());
            String stock = getString(R.string.product_stock_indicator) + tab + String.valueOf(product.getStock()) + " pz";
            String stock_central = getString(R.string.product_stock_central_indicator) + tab + String.valueOf(product.getStock_central()) + " pz";
            String description = getString(R.string.product_description_hint) + ":\n\n" + product.getDescription();
            String category = product.getCategory();

            productName.setText(product.getName());
            productBrand.setText(product.getBrand());
            productCode.setText(product.getCode());
            productPrice.setText(price);
            productCost.setText(cost);
            productTax.setText(tax);
            productStock.setText(stock);
            productCentralStock.setText(stock_central);
            productDescription.setText(description);
            productCategory.setText(category);

            loadProductImages = new LoadProductImages();
            loadProductImages.execute(product.getId());

        }
    }

    @Override
    public void onClick(View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        Drawable drawable = ((ImageView) view).getDrawable();
        ImageView previewItem = new ImageView(getActivity());
        previewItem.setImageDrawable(drawable);
        previewItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
        previewItem.setPadding(0, 20, 0, 20);
        dialogBuilder.setView(previewItem);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.edit:

                Bundle bundle = new Bundle();
                bundle.putInt("id", product.getId());
                FragmentProductsAddEdit fragmentProductsAddEdit = new FragmentProductsAddEdit();
                fragmentProductsAddEdit.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.replace(R.id.container, fragmentProductsAddEdit, FragmentProductsAddEdit.TAG).commit();

                ((MainActivity) getActivity()).depthCounter = 2;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        loadProductImages.cancel(true);
    }

    private class LoadProductImages extends AsyncTask<Integer, Void, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(Integer... integers) {
            int productId = integers[0];
            return ProductPic.getAllThumbnailsForProduct(getActivity(), productId);
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);

            productPhotoList.removeAllViews();
            productPhoto.setImageDrawable(null);

            if (!bitmaps.isEmpty()) {
                photosLayout.setVisibility(View.VISIBLE);
                productPhoto.setImageBitmap(bitmaps.get(0));
                Animation scale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scale.setDuration(500);
                productPhoto.startAnimation(scale);
                productPhoto.setVisibility(View.VISIBLE);

                int wc = LinearLayout.LayoutParams.WRAP_CONTENT;
                int mp = LinearLayout.LayoutParams.MATCH_PARENT;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wc, mp);
                params.setMargins(10, 0, 10, 0);

                for (Bitmap pic : bitmaps) {
                    ImageView thumbnail = new ImageView(getActivity());
                    thumbnail.setVisibility(View.GONE);
                    thumbnail.setAdjustViewBounds(true);
                    thumbnail.setImageBitmap(pic);
                    thumbnail.setTag("thumbnail");
                    thumbnail.setClickable(true);
                    thumbnail.setOnClickListener(FragmentProductsDetail.this);
                    productPhotoList.addView(thumbnail, params);
                    thumbnail.startAnimation(scale);
                    thumbnail.setVisibility(View.VISIBLE);
                }
            } else {
                photosLayout.setVisibility(View.GONE);
                productPhoto.setVisibility(View.GONE);
            }
        }
    }
}

package com.sellcom.tracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;import android.support.v4.app.DialogFragment;
import android.view.View;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import database.models.Product;
import database.models.ProductBrand;
import database.models.ProductPic;
import util.DialogProductAddCategory;
import util.FragmentCamera;
import util.LogUtil;
import util.Utilities;

/**
 * Created by juanc.jimenez on 05/06/14.
 */
public class FragmentProductsAddEdit extends DialogFragment implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher{

    public static final String TAG = "add_edit";
    public static final String DIALOG_TAG = "add_edit_dialog";

    private EditText productName, productCode, productPrice, productCost, productTax, productDescription;
    private AutoCompleteTextView productBrand;
    private TextView productCategory;
    private int categoryId;
    private ArrayList<String> photos;
    private LinearLayout productPhotoList;
    private int focusedField;
    private Product productToEdit;
    private ArrayAdapter<String> brandsAdapter;
    private int mainPhotoId = 0;
    private boolean isEditingProduct = false;
    private boolean isDialog = false;
    private static final int MAX_PHOTOS_ALLOWED = 5;
    private static final int THUMBNAIL_ID = 1;


    public static FragmentProductsAddEdit newInstance(int productId) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", productId);
        FragmentProductsAddEdit productsAddEdit = new FragmentProductsAddEdit();
        productsAddEdit.setArguments(bundle);
        return productsAddEdit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isDialog = true;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_products_add_edit, container, false);

        if (view != null) {
            productName = (EditText) view.findViewById(R.id.product_name);
            productBrand = (AutoCompleteTextView) view.findViewById(R.id.product_brand);
            productCode = (EditText) view.findViewById(R.id.product_code);
            productPrice = (EditText) view.findViewById(R.id.product_price);
            productCost = (EditText) view.findViewById(R.id.product_cost);
            productTax = (EditText) view.findViewById(R.id.product_tax);
            productDescription = (EditText) view.findViewById(R.id.product_description);
            ImageButton scanButton = (ImageButton) view.findViewById(R.id.scan_button);
            ImageButton addCategoryButton = (ImageButton) view.findViewById(R.id.add_category_button);
            productPhotoList = (LinearLayout) view.findViewById(R.id.product_photo_list);
            productCategory = (TextView) view.findViewById(R.id.category_string);

            scanButton.setOnClickListener(this);
            addCategoryButton.setOnClickListener(this);
            productName.setOnFocusChangeListener(this);
            productName.addTextChangedListener(this);
            productBrand.setOnFocusChangeListener(this);
            productBrand.addTextChangedListener(this);
            productCode.setOnFocusChangeListener(this);
            productCode.addTextChangedListener(this);
            productPrice.setOnFocusChangeListener(this);
            productPrice.addTextChangedListener(this);
            productCost.setOnFocusChangeListener(this);
            productCost.addTextChangedListener(this);
            productTax.setOnFocusChangeListener(this);
            productTax.addTextChangedListener(this);
            productDescription.setOnFocusChangeListener(this);
            productDescription.addTextChangedListener(this);
            photos = new ArrayList<String>(MAX_PHOTOS_ALLOWED);

            brandsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ProductBrand.getAll(getActivity()));
            productBrand.setAdapter(brandsAdapter);

            if (!isDialog) {
                FragmentCamera fragmentCamera = new FragmentCamera();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.camera_container, fragmentCamera, "camara");
                fragmentTransaction.commit();
            } else {
                view.findViewById(R.id.photos_layout).setVisibility(View.GONE);
                view.findViewById(R.id.buttons_layout).setVisibility(View.VISIBLE);
                Button saveButton = (Button) view.findViewById(R.id.ok_button);
                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                saveButton.setOnClickListener(this);
                cancelButton.setOnClickListener(this);
            }

            Bundle bundle = getArguments();
            if(bundle != null) {
                int id = bundle.getInt("id", 0);
                productToEdit = Product.getProduct(getActivity(), id);
                setUpForEdit(productToEdit);
                isEditingProduct = true;
            }
        }

        return view;
    }

    public void setUpForEdit(Product product){

        productName.setText(product.getName());
        productBrand.setText(product.getBrand());
        productCode.setText(product.getCode());
        productPrice.setText(String.valueOf(product.getPrice()));
        productCost.setText(String.valueOf(product.getCost()));
        productTax.setText(String.valueOf(product.getTax()));
        productDescription.setText(product.getDescription());
        productCategory.setText(product.getCategory());

        new loadProductImages().execute(product.getId());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isDialog)
            setHasOptionsMenu(true);
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.scan_button:
                new IntentIntegrator(getActivity()).initiateScan();
                break;
            case R.id.add_category_button:
                showCategoryDialog();
                break;
            case THUMBNAIL_ID:
                ImageView clickedItem = (ImageView)view;
                AlertDialog dialog = createPhotoDetailDialog(clickedItem);
                dialog.show();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            case R.id.ok_button:
                int newProductId = saveProduct();
                if (newProductId != 0) {
                    //((FragmentInventoryInOut)getTargetFragment()).updateFilter(Product.getProduct(getActivity(), newProductId));
                    dismiss();
                }
                break;
        }
    }

    public void scanResult(IntentResult scanResult) {
        if (scanResult != null) {
            productCode.setText(scanResult.getContents());
        }
    }

    public void photoResult(Bitmap data) {

        int numPhotos = photos.size();
        int remainingPhotos = MAX_PHOTOS_ALLOWED - numPhotos;

        deleteFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ".photo");

        int wc = LinearLayout.LayoutParams.WRAP_CONTENT;
        int mp = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wc, mp);
        params.setMargins(10, 0, 10, 0);

        if (remainingPhotos > 0) {

            Bitmap toyImageScaled = Bitmap.createScaledBitmap(data, 200, 200 * data.getHeight() / data.getWidth(), false);

            ImageView thumbnail = new ImageView(getActivity());
            thumbnail.setAdjustViewBounds(true);
            thumbnail.setImageBitmap(toyImageScaled);
            thumbnail.setTag(photos.size());
            thumbnail.setId(THUMBNAIL_ID);
            thumbnail.setClickable(true);
            thumbnail.setOnClickListener(this);

            productPhotoList.addView(thumbnail, params);

            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            data.compress(Bitmap.CompressFormat.JPEG, 80, out2);
            data.recycle();

            byte[] bytes = out2.toByteArray();
            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
            photos.add(encodedString);
            remainingPhotos = MAX_PHOTOS_ALLOWED - photos.size();

            if (remainingPhotos == 0)
                Toast.makeText(getActivity(), R.string.max_photos_number_reached, Toast.LENGTH_SHORT).show();

        }
    }

    public AlertDialog createPhotoDetailDialog(final ImageView clickedItem) {

        final int position = (Integer)clickedItem.getTag();

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product_add_edit_photo, null);

        ImageView previewItem = (ImageView) view.findViewById(R.id.thumbnail);
        final CheckBox mainPhotoSelector = (CheckBox) view.findViewById(R.id.main_photo_selector);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                photos.remove(position);
                productPhotoList.removeView(clickedItem);
                if (position == mainPhotoId)
                    mainPhotoId = 0;
                dialogInterface.dismiss();
            }
        });

        Drawable drawable = clickedItem.getDrawable();
        previewItem.setImageDrawable(drawable);

        mainPhotoSelector.setChecked(position == mainPhotoId);
        mainPhotoSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked)
                    mainPhotoId = position;
            }
        });

        return dialogBuilder.setView(view).create();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.ok_cancel, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.save:
                saveProduct();
                break;
            case R.id.discard:
                cancelMethod();
                break;
        }
        return false;
    }

    public int saveProduct() {
        if (validateInputs()) {

            String name, brand, code, price, cost, tax, description;
            int brand_id;

            name = productName.getText().toString();
            brand = productBrand.getText().toString().toLowerCase();
            code = productCode.getText().toString();
            price = productPrice.getText().toString();
            cost = productCost.getText().toString();
            description = productDescription.getText().toString();
            tax = productTax.getText().toString();

            if (isEditingProduct)
                Product.delete(getActivity(), productToEdit.getId());

            if (brandsAdapter.getPosition(brand) == -1) {
                brand_id = (int) ProductBrand.insert(getActivity(), brand);
                brandsAdapter.add(brand);
            }
            else
                brand_id = ProductBrand.getId(getActivity(), brand);

            //long product_id = Product.insert(getActivity(), code, name, description, Double.valueOf(price), Double.valueOf(cost), Float.valueOf(tax), 0, 0, categoryId, brand_id);
            long product_id = 0;
            for (int i = 0; i < photos.size(); i++) {
                if (i == mainPhotoId)
                    ProductPic.insert(getActivity(), photos.get(i), (int) product_id, 1);
                else
                    ProductPic.insert(getActivity(), photos.get(i), (int) product_id, 0);
            }

            LogUtil.LOGD(getActivity().getClass().getSimpleName(), "New product inserted in DB.\n" +
                    "Product ID : " + product_id + "\n" +
                    "Name: " + name + "\n" +
                    "Brand: " + brand + "\n" +
                    "Code: " + code + "\n" +
                    "Price: " + price + "\n" +
                    "Public price: " + cost + "\n" +
                    "Stock: " + tax + "\n" +
                    "Description: " + description + "\n" +
                    "Number of photos: " + photos.size());

            clearFields();
            photos.clear();
            isEditingProduct = false;
            Toast.makeText(getActivity(), R.string.product_saved, Toast.LENGTH_SHORT).show();

            return (int)product_id;
        }
        return 0;
    }

    public int deleteFile(String folderPath) {

        int numPhotos = 0;

        File photoFolder = new File(folderPath);

        if (photoFolder.exists()) {
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + ".photo/test.png");
            f.delete();

            if (photoFolder.listFiles() != null) {

                File childFile[] = photoFolder.listFiles();
                if (childFile != null) {
                    numPhotos = childFile.length;
                }
            }
        }
        return numPhotos;
    }

    public void cancelMethod(){
        try {
            String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+".photo";
            File dir = new File(folderPath);
            if (dir.exists()) {
                File childFile[] = dir.listFiles();
                if (childFile != null) {
                    for (File file : childFile) {
                        file.delete();
                    }
                }
            }else{
                Log.w("cancelMethod","File does not exist");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        getActivity().onBackPressed();
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        focusedField = view.getId();
        if (focusedField == R.id.product_brand)
            productBrand.showDropDown();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        switch (focusedField){
            case R.id.product_name:
                if (!charSequence.toString().isEmpty())
                    productName.setError(null);
                break;
            case R.id.product_brand:
                if (!charSequence.toString().isEmpty())
                    productBrand.setError(null);
                break;
            case R.id.product_code:
                if (!charSequence.toString().isEmpty())
                    productCode.setError(null);
                break;
            case R.id.product_price:
                if (!charSequence.toString().isEmpty())
                    productPrice.setError(null);
                break;
            case R.id.product_cost:
                if (!charSequence.toString().isEmpty())
                    productCost.setError(null);
                break;
            case R.id.product_tax:
                if (!charSequence.toString().isEmpty())
                    productTax.setError(null);
                break;
            case R.id.product_description:
                if (!charSequence.toString().isEmpty())
                    productDescription.setError(null);
                break;
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void clearFields(){

        Utilities.hideKeyboard(getActivity(), productDescription);
        productName.setText("");
        productBrand.setText("");
        productCode.setText("");
        productPrice.setText("");
        productCost.setText("");
        productTax.setText("");
        productDescription.setText("");
        productCategory.setText(getActivity().getString(R.string.select_category));

        View camera = productPhotoList.findViewById(R.id.camera_container);
        productPhotoList.removeAllViews();
        productPhotoList.addView(camera);
    }

    public boolean validateInputs() {

        String name, brand, code, price, cost, tax, description;
        boolean isValid = true;
        float taxNumber = 0;

        name = productName.getText().toString();
        brand = productBrand.getText().toString().toLowerCase();
        code = productCode.getText().toString();
        price = productPrice.getText().toString();
        cost = productCost.getText().toString();
        description = productDescription.getText().toString();
        tax = productTax.getText().toString();
        if (!tax.isEmpty())
            taxNumber = Float.parseFloat(tax);


        if (name.isEmpty()) {
            productName.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (brand.isEmpty()) {
            productBrand.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (code.isEmpty()) {
            productCode.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (Product.barcodeExists(getActivity(), code) && !isEditingProduct) {
            productCode.setError(getString(R.string.error_existing_barcode));
            isValid = false;
        }
        if (price.isEmpty()) {
            productPrice.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (cost.isEmpty()) {
            productCost.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (tax.isEmpty()) {
            productTax.setError(getString(R.string.error_empty_field));
            isValid = false;
        }
        if (taxNumber > 100) {
            productTax.setError(getString(R.string.error_invalid_tax_value));
            isValid = false;
        }
        if (description.isEmpty()) {
            productDescription.setError(getString(R.string.error_empty_field));
            isValid = false;
        }

        return isValid;
    }

    public void showCategoryDialog() {

        final DialogProductAddCategory dialogProductAddCategory = new DialogProductAddCategory(getActivity(), productCategory.getText().toString());
        dialogProductAddCategory.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                categoryId = dialogProductAddCategory.lastSelectedId;
                productCategory.setText(dialogProductAddCategory.result);
            }
        });
        dialogProductAddCategory.show();
    }

    private class loadProductImages extends AsyncTask<Integer, Void, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(Integer... integers) {
            int productId = integers[0];
            return ProductPic.getAllForProduct(getActivity(), productId);
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);

            for (Bitmap pic : bitmaps) {
                photoResult(pic);
            }
        }
    }
}

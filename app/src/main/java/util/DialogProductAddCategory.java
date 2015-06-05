package util;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.Arrays;

import database.models.ProductCategory;

/**
 * Created by juanc.jimenez on 16/06/14.
 */
public class DialogProductAddCategory extends Dialog implements AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, View.OnClickListener {

    LinearLayout dialogFrame, layoutAddCategory;
    Spinner parentCategory;
    AutoCompleteTextView addCategory;
    Context context;
    Button cancelButton, okButton;
    int childCounter, focusedField;
    boolean inCodeSelection = true;

    public ArrayList<String> selectedCategories;
    public int lastSelectedId;
    public String result;

    public DialogProductAddCategory(Context context, String currentCategory){

        super(context, R.style.AnimatedDialog);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_category);
        setCancelable(false);

        dialogFrame = (LinearLayout) findViewById(R.id.dialog_frame);
        layoutAddCategory = (LinearLayout) findViewById(R.id.layout_add_category);
        parentCategory = (Spinner) findViewById(R.id.parent_category);
        addCategory = (AutoCompleteTextView) findViewById(R.id.add_category);
        cancelButton = (Button) findViewById(R.id.dialog_cancel_button);
        okButton = (Button) findViewById(R.id.dialog_ok_button);

        cancelButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        okButton.setTag("OK");

        selectedCategories = new ArrayList<String>();

        parentCategory.setOnItemSelectedListener(this);
        addCategory.setOnEditorActionListener(this);
        addCategory.setOnFocusChangeListener(this);

        updateAdapter(childCounter);

        if (currentCategory.contains("/"))
            setUpForEdit(currentCategory);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.dialog_ok_button:
                String action = (String) view.getTag();
                if (action.equals("OK")){
                    result = "";
                    for (String name : selectedCategories)
                        result += name + "/";

                    dismiss();
                }else if (action.equals("ADD")){
                    String newCategory = addCategory.getText().toString();
                    addCategory.setText("");

                    saveCategory(newCategory);
                    int parentId = getParentId(focusedField);
                    if (parentId != -1)
                        updateAdapter(parentId);
                    okButton.setTag("OK");
                }

                break;
            case R.id.dialog_cancel_button:
                dismiss();
                break;
        }

    }

    public void addChild() {

        childCounter++;

        inCodeSelection = true;
        Spinner childCategory = new Spinner(context);
        childCategory.setId(childCounter);
        childCategory.setPadding(5, 5, 5, 5);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 5);
        childCategory.setLayoutParams(params);

        dialogFrame.addView(childCategory);

        int parentId = getParentId(childCounter);
        if (parentId != -1)
            updateAdapter(parentId);

        childCategory.setOnItemSelectedListener(this);
    }

    public void removeChildren(boolean isValidSelection) {

        ArrayList<String> copy = new ArrayList<String>();
        copy.addAll(selectedCategories);
        selectedCategories.clear();
        for (int i = focusedField + 1; i <= childCounter; i++) {

            dialogFrame.removeView(dialogFrame.findViewById(i));
        }
        for (int i = 0; i <= focusedField; i++)
            selectedCategories.add(copy.get(i));

        childCounter = focusedField;
        copy.clear();
        if (isValidSelection)
            addChild();
    }

    public void setUpForEdit(String toEdit) {

        String[] split = toEdit.split("/");
        selectedCategories.addAll(Arrays.asList(split));

        for (int i = 0; i < split.length; i++) {
            Spinner spinner = (Spinner) dialogFrame.getChildAt(i);
            if (spinner != null) {
                ArrayAdapter adapter = (ArrayAdapter)spinner.getAdapter();
                int spinnerPosition = adapter.getPosition(split[i]);
                spinner.setSelection(spinnerPosition, false);
                addChild();
            } else
                LogUtil.addCheckpoint("Child doesn't exists yet");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        boolean isValidSelection = true;
        Spinner spinner = (Spinner)adapterView;
        if (spinner.getId() == R.id.parent_category)
            focusedField = 0;
        else
            focusedField = spinner.getId();

        if (inCodeSelection) {
            inCodeSelection = false;
            return;
        }

        if (spinner.getItemAtPosition(i).equals(context.getString(R.string.add_category))) {
            isValidSelection = false;
            animateAddLayout(View.VISIBLE);
        }

        if (isValidSelection) {
            selectedCategories.add(focusedField, (String) adapterView.getItemAtPosition(i));
            lastSelectedId = ProductCategory.getId(context, (String) adapterView.getItemAtPosition(i));
        }

        if (focusedField < childCounter)
            removeChildren(isValidSelection);
        else
            addChild();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        if (hasFocus) {
            okButton.setTag("ADD");
            AutoCompleteTextView temp = (AutoCompleteTextView) view;
            temp.showDropDown();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if (i == EditorInfo.IME_ACTION_DONE) {
            String newCategory = textView.getText().toString();
            textView.setText("");

            saveCategory(newCategory);
            int parentId = getParentId(childCounter);
            if (parentId != -1)
                updateAdapter(parentId);
        }
        return false;
    }

    private void saveCategory(String toSave){

        int parentId = getParentId(childCounter);

        if (parentId != -1)
            ProductCategory.insert(context, toSave, parentId);

    }

    private void updateAdapter(int parentId) {

        if (parentId < 0)
            return;

        Spinner spinnerToUpdate = (Spinner) dialogFrame.getChildAt(childCounter);

        inCodeSelection = true;
        ArrayList<String> categoriesList = new ArrayList<String>();
        categoriesList.addAll(ProductCategory.getCategoriesForParent(context, parentId));
        categoriesList.add(0, context.getString(R.string.add_category));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinnerToUpdate != null) {
            spinnerToUpdate.setAdapter(adapter);
        }

        if (categoriesList.size() == 1)
            animateAddLayout(View.VISIBLE);
        else
            animateAddLayout(View.GONE);

    }

    private int getParentId(int currentSpinner) {

        Spinner parentSpinner;
        String parentCategory;
        int parentId;

        if (currentSpinner == 0) {      //isParent
            parentId = 0;
        }
        else if (currentSpinner > 0) {    //hasParent
            parentSpinner = (Spinner) dialogFrame.getChildAt(currentSpinner - 1);
            parentCategory = (String) parentSpinner.getSelectedItem();

            parentId = ProductCategory.getId(context, parentCategory);

        }
        else
            parentId = -1;

        return parentId;
    }

    private void animateAddLayout(int visibility){

        Animation animation;
        switch (visibility) {
            case View.VISIBLE:
                animation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_in);
                break;
            case View.INVISIBLE:
                animation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_out);
                break;
            default:
                animation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_out);
                break;
        }

        animation.setDuration(400);
        layoutAddCategory.setVisibility(visibility);
        layoutAddCategory.startAnimation(animation);
    }
}

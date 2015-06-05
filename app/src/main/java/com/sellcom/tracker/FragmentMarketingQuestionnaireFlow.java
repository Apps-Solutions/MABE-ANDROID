package com.sellcom.tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.Exhibition;
import database.models.MarketingQuest;
import database.models.Offers;
import database.models.Product;
import database.models.ProductShelfReview;
import database.models.Promotion;
import database.models.QualityIncidence;
import database.models.ResourceType;
import database.models.Session;
import database.models.Shelf_Review;
import database.models.User;
import util.DataCollectedReceiver;
import util.DatesHelper;
import util.SpinnerAdapter;
import util.TrackerManager;
import util.Utilities;

public class FragmentMarketingQuestionnaireFlow extends Fragment implements
                                                                            UIResponseListenerInterface,
                                                                            View.OnClickListener,
                                                                            FragmentPartialPayDialog.setSetImgPhoto,
                                                                            ConfirmationDialogListener,
                                                                            DataCollectedReceiver{

    final static public String TAG = "FRAG_MARK_QUEST_FLOW";

    public List<Map<String,String>> products;

    List<Map<String,String>>    aux_list;
    int                         aux_index   = 0;
    Map<String, String>         current_map;
    METHOD                      current_method;

    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String image_encode_op = "";
    Thread encodeImage;

    String      id_visit,id_product,username;

    private TextView   tv_customer_shelf_review;

    private Spinner    spn_sr_product_type;

    //---------- Assorment And Quality   ----------

    private EditText    edt_sr_current_existence_shelf,
                        edt_sr_current_existence_exhibit,
                        edt_sr_box_shelf,
                        edt_sr_box_exhibit,
                        edt_sr_box_total,
                        edt_sr_price_public,
                        edt_sr_expiracy_date,

                        edt_sr_front_tot,
                        edt_sr_rival_1_front_tot,
                        edt_sr_rival_2_front_tot,
                        edt_sr_front_tot_1
                                ;

    private DatePickerDialog dlg_sr_expiracy_date;
    Map<String,String> shelf_review_data;
    //---------------------------------------------

    //---------- Quality Incidences   ----------
    List<Map<String,String>>    quality_incidences_array;
    LinearLayout                qua_inc_table_container;
    ImageButton                 btn_add_quality_incidence;

    //---------- Own Offers ---------------
    Switch swt_own_prom;
    Map<String,String> offers;

    private DatePickerDialog dlg_own_offers_start;
    private DatePickerDialog dlg_own_offers_end;


    private EditText   edt_op_price_offers,
                       edt_op_start_offers,
                       edt_op_end_offers,
                       edt_op_merchandise_promotion,
                       edt_op_box_promotion;

    private Spinner    spn_op_type_promotion,
                       spn_op_type_exhibit,
                       spn_op_resourse_ragasa;

    private ImageView  img_camera_op,
                       img_photo_op;

    private RadioGroup rg_op_activation,
                       rg_op_exhibit;

    private CheckBox   chk_op_1,
                       chk_op_2,
                       chk_op_3,
                       chk_op_4,
                       chk_op_5,
                       chk_op_6;

    private Boolean camera_op = false;

    Calendar startOffer,endOffer;

    //---------- Rival Offers   ----------
    List<Map<String,String>>    rival_offers_array;
    LinearLayout                riv_off_table_container;
    ImageButton                 btn_add_rival_offer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_marketing_questionnaire_flow, container, false);

        // FRAGMENT HEADER
        tv_customer_shelf_review = (TextView) view.findViewById(R.id.tv_customer_shelf_review);
        tv_customer_shelf_review.setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv"));

        // Legacy Info
        id_visit    = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        // PRODUCT INFORMATION
        spn_sr_product_type    = (Spinner) view.findViewById(R.id.spn_sr_product_type);
        util.SpinnerAdapter spinnerAdapterProduct = new SpinnerAdapter(getActivity(),
                products,
                SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        spn_sr_product_type.setAdapter(spinnerAdapterProduct);
        id_product  = ((Map<String,String>) this.spn_sr_product_type.getSelectedItem()).get("id");

        // ASSORTMENT AND QUALITY
        edt_sr_current_existence_shelf      = (EditText) view.findViewById(R.id.edt_sr_current_existence_shelf);
        edt_sr_current_existence_exhibit    = (EditText) view.findViewById(R.id.edt_sr_current_existence_exhibit);
        edt_sr_box_shelf               = (EditText) view.findViewById(R.id.edt_sr_box_shelf);
        edt_sr_box_exhibit             = (EditText) view.findViewById(R.id.edt_sr_box_exhibit);
        edt_sr_box_total               = (EditText) view.findViewById(R.id.edt_sr_box_total);
        edt_sr_price_public            = (EditText) view.findViewById(R.id.edt_sr_price_public);
        edt_sr_expiracy_date           = (EditText) view.findViewById(R.id.edt_sr_expiracy_date);

        edt_sr_front_tot                = (EditText) view.findViewById(R.id.edt_sr_front_tot);
        edt_sr_rival_1_front_tot        = (EditText) view.findViewById(R.id.edt_sr_rival_1_front_tot);
        edt_sr_rival_2_front_tot        = (EditText) view.findViewById(R.id.edt_sr_rival_2_front_tot);
        edt_sr_front_tot_1              = (EditText) view.findViewById(R.id.edt_sr_front_tot_1);


        edt_sr_box_shelf.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                edt_sr_box_total.setText(""+(Integer.parseInt(Utilities.validateWithZeroDefault(edt_sr_box_shelf.getText().toString())) +  Integer.parseInt(Utilities.validateWithZeroDefault(edt_sr_box_exhibit.getText().toString()))));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        edt_sr_box_exhibit.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                edt_sr_box_total.setText(""+(Integer.parseInt(Utilities.validateWithZeroDefault(edt_sr_box_shelf.getText().toString())) +  Integer.parseInt(Utilities.validateWithZeroDefault(edt_sr_box_exhibit.getText().toString()))));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        edt_sr_expiracy_date.setInputType(InputType.TYPE_NULL);
        edt_sr_expiracy_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dlg_sr_expiracy_date.show();
                    edt_sr_expiracy_date.setError(null);
                }
            }
        });

        setupExpirationDateDialog();

        // QUALITY INCIDENCES
        quality_incidences_array    = new ArrayList<Map<String, String>>();
        qua_inc_table_container     = (LinearLayout)view.findViewById(R.id.qua_inc_table_container);
        btn_add_quality_incidence   = (ImageButton)view.findViewById(R.id.btn_add_quality_incidence);
        btn_add_quality_incidence.setOnClickListener(this);

        // OFFERS AND PROMOTIONS (OWN)

        swt_own_prom = (Switch) view.findViewById(R.id.swt_own_prom);
        swt_own_prom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    view.findViewById(R.id.lin_own_prom).setVisibility(View.VISIBLE);
                    edt_op_price_offers.setText(edt_sr_price_public.getText().toString());

                    edt_op_box_promotion.setText(edt_sr_current_existence_shelf.getText().toString());
                }
                else {
                    view.findViewById(R.id.lin_own_prom).setVisibility(View.GONE);
                }
            }
        });

        if(swt_own_prom.isChecked())
            view.findViewById(R.id.lin_own_prom).setVisibility(View.VISIBLE);
        else
            view.findViewById(R.id.lin_own_prom).setVisibility(View.GONE);

        edt_op_price_offers             = (EditText) view.findViewById(R.id.edt_op_price_offers);
        edt_op_price_offers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    // code to execute when EditText loses focus

                    if(!edt_sr_price_public.getText().toString().isEmpty()){
                        if(!edt_op_price_offers.getText().toString().isEmpty()){
                            if(Double.parseDouble(edt_op_price_offers.getText().toString()) > Double.parseDouble(edt_sr_price_public.getText().toString())){

                                edt_op_price_offers.setError(getString(R.string.msj_offer_price));
                                edt_op_price_offers.setFocusable(true);
                            }
                        }
                    }
                }

            }
        });




        edt_op_start_offers             = (EditText) view.findViewById(R.id.edt_op_start_offers);

        edt_op_start_offers.setInputType(InputType.TYPE_NULL);
        edt_op_start_offers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    dlg_own_offers_start.show();
            }
        });
        setupStartOfferDateDialog();

        edt_op_end_offers               = (EditText) view.findViewById(R.id.edt_op_end_offers);
        edt_op_end_offers.setInputType(InputType.TYPE_NULL);
        edt_op_end_offers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    dlg_own_offers_end.show();
            }
        });
        setupEndOfferDateDialog();

        chk_op_1    = (CheckBox)  view.findViewById(R.id.chk_op_1);
        chk_op_2    = (CheckBox)  view.findViewById(R.id.chk_op_2);
        chk_op_3    = (CheckBox)  view.findViewById(R.id.chk_op_3);
        chk_op_4    = (CheckBox)  view.findViewById(R.id.chk_op_4);
        chk_op_5    = (CheckBox)  view.findViewById(R.id.chk_op_5);
        chk_op_6    = (CheckBox)  view.findViewById(R.id.chk_op_6);

        rg_op_activation = (RadioGroup) view.findViewById(R.id.rg_op_activation);

        spn_op_type_promotion  = (Spinner) view.findViewById(R.id.spn_op_type_promotion);
        util.SpinnerAdapter spinnerAdapterProblemType = new SpinnerAdapter(getActivity(),
                Promotion.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.PROBLEM_TYPE);
        spn_op_type_promotion.setAdapter(spinnerAdapterProblemType);

        edt_op_merchandise_promotion   = (EditText) view.findViewById(R.id.edt_op_merchandise_promotion);

        spn_op_type_exhibit    = (Spinner) view.findViewById(R.id.spn_op_type_exhibit);
        SpinnerAdapter spinnerAdapterExhibit = new SpinnerAdapter(getActivity(),
                Exhibition.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_op_type_exhibit.setAdapter(spinnerAdapterExhibit);

        rg_op_exhibit    = (RadioGroup) view.findViewById(R.id.rg_op_exhibit);

        spn_op_resourse_ragasa = (Spinner) view.findViewById(R.id.spn_op_resourse_ragasa);
        SpinnerAdapter spinnerAdapterRagasa = new SpinnerAdapter(getActivity(),
                ResourceType.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_op_resourse_ragasa.setAdapter(spinnerAdapterRagasa);


        edt_op_box_promotion           = (EditText) view.findViewById(R.id.edt_op_box_promotion);

        img_camera_op = (ImageView) view.findViewById(R.id.img_camera_op);
        img_photo_op  = (ImageView) view.findViewById(R.id.img_photo_op);

        img_camera_op.setOnClickListener(this);
        img_photo_op.setOnClickListener(this);

        // OFFERS AND PROMOTIONS (RIVAL)

        rival_offers_array      = new ArrayList<Map<String, String>>();
        riv_off_table_container = (LinearLayout)view.findViewById(R.id.riv_off_table_container);
        btn_add_rival_offer     = (ImageButton)view.findViewById(R.id.btn_add_rival_offer);
        btn_add_rival_offer.setOnClickListener(this);


        spn_sr_product_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_product  = ((Map<String,String>) products.get(i)).get("id");
                setupQualityIncidencesTable();
                setupRivalOffersTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Utilities.hideKeyboard(getActivity(),edt_sr_current_existence_shelf);
                saveMeasure();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.edt_sr_expiracy_date:
                dlg_sr_expiracy_date.show();
                break;

            case R.id.btn_add_quality_incidence:
                openAddQualityIncidentFragment();
                break;

            case R.id.btn_add_rival_offer:
                openAddRivalOfferFragment();
                break;

            case R.id.img_camera_op:
                camera_op = true;
                takePhoto();
                break;

            case R.id.img_photo_op:
                if (img_photo_op.getDrawable() != null) {
                    openDialog(image);
                }
                break;
        }

    }

    public void setupExpirationDateDialog(){
        edt_sr_expiracy_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dlg_sr_expiracy_date = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_sr_expiracy_date.setText(""+dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setupStartOfferDateDialog(){
        edt_op_start_offers.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dlg_own_offers_start = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startOffer = Calendar.getInstance();
                startOffer.set(year, monthOfYear, dayOfMonth);

                if (endOffer != null){
                    if(startOffer.compareTo(endOffer) > 0) {
                        edt_op_start_offers.setText("");
                        edt_op_end_offers.setText("");
                        endOffer = startOffer = null ;
                        Toast.makeText(getActivity(),"Fecha de inicio y final no válida",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                edt_op_start_offers.setText(""+dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setupEndOfferDateDialog(){
        edt_op_end_offers.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dlg_own_offers_end = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endOffer = Calendar.getInstance();
                endOffer.set(year, monthOfYear, dayOfMonth);

                if (startOffer != null && !edt_op_start_offers.getText().toString().isEmpty()){
                    if(startOffer.compareTo(endOffer) > 0) {
                        edt_op_start_offers.setText("");
                        edt_op_end_offers.setText("");
                        endOffer = startOffer = null ;
                        Toast.makeText(getActivity(),"Fecha de inicio y final no válida",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                edt_op_end_offers.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setupQualityIncidencesTable(){

        qua_inc_table_container.removeAllViews();

        quality_incidences_array    = QualityIncidence.getAllNoSentQualityIncidenceProductInVisit(getActivity(), id_visit, id_product);

        String[] column = { "TIPO DE PROBLEMA", "OPCIONES"};

        int row_num = quality_incidences_array.size() + 1;  // +1 BECAUSE THE HEADER
        int col_num = column.length;

        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableLayout tableLayout = new TableLayout(getActivity());

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < row_num; i++) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setPadding(5,5,5,5);
            tableRow.setBackgroundResource(R.drawable.row_border);

            for (int j= 0; j < col_num; j++) {
                if (j== 1 && i!= 0){
                    Button btn  = new Button(getActivity());
                    btn.setText("X");
                    btn.setGravity(Gravity.CENTER);
                    final int id = Integer.parseInt(quality_incidences_array.get(i-1).get("id"));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            QualityIncidence.delete(getActivity(),id);
                            setupQualityIncidencesTable();
                        }
                    });

                    tableRow.addView(btn, tableRowParams);
                }
                else{
                    TextView textView = new TextView(getActivity());
                    textView.setTextColor(getActivity().getResources().getColor(R.color.black_gray));
                    textView.setGravity(Gravity.CENTER);
                    if(i==0)
                        textView.setText(column[j]);
                    else
                        textView.setText(quality_incidences_array.get(i - 1).get("name_problem_type"));

                    tableRow.addView(textView, tableRowParams);
                }
            }
            tableLayout.addView(tableRow, tableLayoutParams);
        }
        qua_inc_table_container.addView(tableLayout);
    }

    public void setupRivalOffersTable(){

        riv_off_table_container.removeAllViews();

        rival_offers_array    = Offers.getAllRivalNoSentOffersInVisitByOwnProduct(getActivity(),id_visit,id_product);

        String[] column = { "PRODUCTO", "OPCIONES"};

        int row_num = rival_offers_array.size() + 1;  // +1 BECAUSE THE HEADER
        int col_num = column.length;

        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableLayout tableLayout = new TableLayout(getActivity());

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < row_num; i++) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setPadding(5,5,5,5);
            tableRow.setBackgroundResource(R.drawable.row_border);

            for (int j= 0; j < col_num; j++) {
                if (j== 1 && i!= 0){
                    Button btn  = new Button(getActivity());
                    btn.setText("X");
                    btn.setGravity(Gravity.CENTER);
                    final int id = Integer.parseInt(rival_offers_array.get(i-1).get("id"));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Offers.delete(getActivity(),id);
                            setupRivalOffersTable();
                        }
                    });

                    tableRow.addView(btn, tableRowParams);
                }
                else{
                    TextView textView = new TextView(getActivity());
                    textView.setTextColor(getActivity().getResources().getColor(R.color.black_gray));
                    textView.setGravity(Gravity.CENTER);
                    if(i==0)
                        textView.setText(column[j]);
                    else
                        textView.setText(rival_offers_array.get(i - 1).get("name_product"));

                    tableRow.addView(textView, tableRowParams);
                }
            }
            tableLayout.addView(tableRow, tableLayoutParams);
        }
        riv_off_table_container.addView(tableLayout);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void openAddQualityIncidentFragment(){

        List <Map<String,String>> problem_type_array  =  DataBaseManager.sharedInstance().getQualityIncidencesInProductAvailableInVisit(id_product,id_visit);

        if (problem_type_array.size() == 0){
            Toast.makeText(getActivity(), "Todas las incidencias de calidad han sido registradas al producto", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment                                           = new FragmentQualityIncidence();
        ((FragmentQualityIncidence)fragment).listener               = this;
        ((FragmentQualityIncidence)fragment).problem_type_array     = problem_type_array;

        Bundle bundle = new Bundle();
        bundle.putString("product_id", ((Map<String,String>) this.spn_sr_product_type.getSelectedItem()).get("id"));
        bundle.putString("product_name", ((Map<String,String>) this.spn_sr_product_type.getSelectedItem()).get("name"));
        fragment.setArguments(bundle);

        FragmentManager fragmentManager         = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, fragment, FragmentQualityIncidence.TAG);

        fragmentTransaction.commit();
        ((MainActivity) getActivity()).depthCounter = 5;
    }

    public void openAddRivalOfferFragment(){

        Fragment fragment                               = new FragmentRivalOffer();
        List<Map<String,String>> product_array          = DataBaseManager.sharedInstance().getRivalProductsWithNoOfferInVisit(id_visit);

        if (product_array.size() == 0){
            Toast.makeText(getActivity(), "Todas las promociones de productos rivales han sido registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        ((FragmentRivalOffer)fragment).product_array    = product_array;

        Bundle bundle = new Bundle();
        bundle.putString("product_id", ((Map<String,String>) this.spn_sr_product_type.getSelectedItem()).get("id"));
        bundle.putString("product_name", ((Map<String,String>) this.spn_sr_product_type.getSelectedItem()).get("name"));
        fragment.setArguments(bundle);

        FragmentManager fragmentManager         = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, fragment, FragmentRivalOffer.TAG);

        fragmentTransaction.commit();
        ((MainActivity) getActivity()).depthCounter = 5;
    }

    public void takePhoto(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, THUMBNAIL_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK && requestCode == THUMBNAIL_ID) {
            //image = getStorageImage();
            image = data.getParcelableExtra("data");
            openDialog(image);
        }
    }

    public void openDialog(Bitmap bitmap){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPartialPayDialog dialogo = new FragmentPartialPayDialog();
        dialogo.setSetSetImgPhotoListener(this);
        dialogo.photoResult(bitmap);
        dialogo.show(fragmentManager, FragmentPartialPayDialog.TAG);
    }

    @Override
    public void setImgPhoto(Bitmap bitmap) {
        image = bitmap;
        encodeImage = new Thread(new Runnable() {
            @Override
            public void run() {
                if(camera_op == true)
                    image_encode_op = encodeTobase64(image);
            }
        });
        encodeImage.run();

        if(camera_op == true) {
            img_photo_op.setImageBitmap(image);
            camera_op = false;
        }
    }

    @Override
    public void retry() {
        takePhoto();
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public boolean validateShelfReviewData(){

        if (this.edt_sr_current_existence_shelf.getText().toString().isEmpty()) {
            edt_sr_current_existence_shelf.setError(getString(R.string.error_empty_field));
            edt_sr_current_existence_shelf.requestFocus();
            return false;
        }
        if (this.edt_sr_current_existence_exhibit.getText().toString().isEmpty()) {
            edt_sr_current_existence_exhibit.setError(getString(R.string.error_empty_field));
            edt_sr_current_existence_exhibit.requestFocus();
            return false;
        }
        if (this.edt_sr_box_shelf.getText().toString().isEmpty()) {
            edt_sr_box_shelf.setError(getString(R.string.error_empty_field));
            edt_sr_box_shelf.requestFocus();
            return false;
        }
        if (this.edt_sr_box_exhibit.getText().toString().isEmpty()) {
            edt_sr_box_exhibit.setError(getString(R.string.error_empty_field));
            edt_sr_box_exhibit.requestFocus();
            return false;
        }
        if (this.edt_sr_price_public.getText().toString().isEmpty()) {
            edt_sr_price_public.setError(getString(R.string.error_empty_field));
            edt_sr_price_public.requestFocus();
            return false;
        }
        if (this.edt_sr_expiracy_date.getText().toString().isEmpty()) {
            edt_sr_expiracy_date.setError(getString(R.string.error_empty_field));
            edt_sr_expiracy_date.requestFocus();
            return false;
        }

        if (this.edt_sr_front_tot.getText().toString().isEmpty()) {
            edt_sr_front_tot.setError(getString(R.string.error_empty_field));
            edt_sr_front_tot.requestFocus();
            return false;
        }

        if (this.edt_sr_rival_1_front_tot.getText().toString().isEmpty()) {
            edt_sr_rival_1_front_tot.setError(getString(R.string.error_empty_field));
            edt_sr_rival_1_front_tot.requestFocus();
            return false;
        }

        if (this.edt_sr_front_tot_1.getText().toString().isEmpty()) {
            edt_sr_front_tot_1.setError(getString(R.string.error_empty_field));
            edt_sr_front_tot_1.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validateOwnOffers(){

        if (this.edt_op_price_offers.getText().toString().isEmpty()) {
            edt_op_price_offers.setError(getString(R.string.error_empty_field));
            edt_op_price_offers.requestFocus();
            return false;
        }
        if (this.edt_op_end_offers.getText().toString().isEmpty()) {
            edt_op_end_offers.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.edt_op_start_offers.getText().toString().isEmpty()) {
            edt_op_start_offers.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.edt_op_merchandise_promotion.getText().toString().isEmpty()) {
            edt_op_merchandise_promotion.setError(getString(R.string.error_empty_field));
            edt_op_merchandise_promotion.requestFocus();
            return false;
        }
        if (this.edt_op_box_promotion.getText().toString().isEmpty()) {
            edt_op_box_promotion.setError(getString(R.string.error_empty_field));
            edt_op_box_promotion.requestFocus();
            return false;
        }
        return true;
    }

    public void saveMeasure() {

        if (!validateShelfReviewData())
            return;

        shelf_review_data    = new HashMap<String, String>();
        shelf_review_data.put("visit_id", id_visit);
        shelf_review_data.put("id_product", id_product);
        shelf_review_data.put("current_shelf", Utilities.validateWithZeroDefault(this.edt_sr_current_existence_shelf.getText().toString()));
        shelf_review_data.put("current_exhibition", Utilities.validateWithZeroDefault(this.edt_sr_current_existence_exhibit.getText().toString()));
        shelf_review_data.put("shelf_boxes", Utilities.validateWithZeroDefault(this.edt_sr_box_shelf.getText().toString()));
        shelf_review_data.put("exhibition_boxes", Utilities.validateWithZeroDefault(this.edt_sr_box_exhibit.getText().toString()));
        shelf_review_data.put("expiration", DatesHelper.dateFormattedWithYearMonthAndDay(this.edt_sr_expiracy_date.getText().toString()));
        shelf_review_data.put("price", Utilities.validateWithZeroDefault(this.edt_sr_price_public.getText().toString()));

        shelf_review_data.put("front", Utilities.validateWithZeroDefault(this.edt_sr_front_tot.getText().toString()));
        shelf_review_data.put("rival1", Utilities.validateWithZeroDefault(this.edt_sr_rival_1_front_tot.getText().toString()));
        shelf_review_data.put("rival2", Utilities.validateWithZeroDefault(this.edt_sr_rival_2_front_tot.getText().toString()));
        shelf_review_data.put("total", Utilities.validateWithZeroDefault(this.edt_sr_front_tot_1.getText().toString()));

        Log.d(TAG,shelf_review_data.toString());

        offers   = new HashMap<String, String>();
        // Own Offers
        if (swt_own_prom.isChecked()){  // Recover data from offers layout

            if (!validateOwnOffers())
                return;

            offers.put("id_visit",id_visit);
            offers.put("id_product",id_product);
            offers.put("promotion_price",Utilities.validateWithZeroDefault(this.edt_op_price_offers.getText().toString()));

            offers.put("promotion_start",DatesHelper.dateFormattedWithYearMonthAndDay(this.edt_op_end_offers.getText().toString()));
            offers.put("promotion_end",DatesHelper.dateFormattedWithYearMonthAndDay(this.edt_op_start_offers.getText().toString()));

            offers.put("id_graphic_material",recoverDataFromGraphicalMaterialOwnOffers());

            int checkedRadioButtonActivationOP = rg_op_activation.getCheckedRadioButtonId();
            switch (checkedRadioButtonActivationOP){
                case R.id.radio_op_activation_yes:
                    offers.put("activation","1");
                    break;
                case R.id.radio_op_activation_no:
                    offers.put("activation","0");
                    break;
            }

            offers.put("id_promotion_type",((Map<String,String>) this.spn_op_type_promotion.getSelectedItem()).get("id"));
            offers.put("mechanics",Utilities.validateWithZeroDefault(this.edt_op_merchandise_promotion.getText().toString()));

            int checkedRadioButtonExhibitOP = rg_op_exhibit.getCheckedRadioButtonId();
            switch (checkedRadioButtonExhibitOP){
                case R.id.radio_op_exhibit_yes:
                    offers.put("exhibition","1");
                    break;
                case R.id.radio_op_exhibit_no:
                    offers.put("exhibition","0");
                    break;
            }

            offers.put("id_exhibition_type",((Map<String,String>) this.spn_op_type_exhibit.getSelectedItem()).get("id"));
            offers.put("existence",Utilities.validateWithZeroDefault(this.edt_op_box_promotion.getText().toString()));
            offers.put("id_resource_type",((Map<String,String>) this.spn_op_resourse_ragasa.getSelectedItem()).get("id"));
            offers.put("evidence",image_encode_op);

            offers.put("public_price","");
            offers.put("differed_sku","");
            offers.put("sku","");
            offers.put("type","OWN");

            // OWN OFFER SAVED
            Offers.insertFromMap(getActivity(),offers);
        }
        // SHELF REVIEW SAVED
        DataBaseManager.sharedInstance().saveShelfReview(shelf_review_data);

        // Start with all the shelf reviews no sent in the visit
        aux_index   = 0;
        current_method  = METHOD.SET_SHELF_REVISION;

        aux_list = new ArrayList<Map<String, String>>();

        Map<String,String> review = ProductShelfReview.getProductShelfReviewNoSentWithProductInVisit(getActivity(), id_product, id_visit);

        if (review != null)
            aux_list.add(review);

        if (!aux_list.isEmpty())
            prepareRequest();
        else
            prepareOffersRequest();


        Log.d(TAG,"Shelf Revision: "+(ProductShelfReview.getProductShelfReviewNoSentWithProductInVisit(getActivity(), id_product, id_visit)));
        List<Map<String,String>> offers = Offers.getAllNoSentOffersInVisit(getActivity(),id_visit);

        for (Map<String,String> offer :offers) {
            Log.d(TAG,"Offer: "+offer.toString());
        }

        List<Map<String,String>> incidences = QualityIncidence.getAllNoSentQualityIncidencesInVisit(getActivity(), id_visit);
        for (Map<String,String> incidence :incidences) {
            Log.d(TAG,"Incidence: "+incidence.toString());
        }
    }

    void prepareRequest(){
        current_map = aux_list.get(aux_index);

        JSONArray   products = new JSONArray();
        products.put(Utilities.mapToJson(current_map));

        String array_name = "products";
        if (current_method == METHOD.SET_PROMOTION)
            array_name = "promotions";

        JSONObject requestData = Utilities.getJSONWithCredentials(getActivity());
        try {
            requestData.put("id_visit",id_visit);
            requestData.put(array_name, products.toString());
            requestData.put("request",current_method);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,current_method);
    }

    void prepareOffersRequest(){
        aux_index       = 0;
        aux_list        = Offers.getAllNoSentOffersInVisit(getActivity(), id_visit);
        current_method  = METHOD.SET_PROMOTION;
        if (!aux_list.isEmpty())
            prepareRequest();
        else
            prepareIncidencesRequest();
    }

    void prepareIncidencesRequest(){
        aux_index       = 0;
        aux_list        = QualityIncidence.getAllNoSentQualityIncidencesInVisit(getActivity(),id_visit);
        current_method  = METHOD.SET_QUALITY_INCIDENCE;
        if (!aux_list.isEmpty())
            prepareRequest();
        else
            endMarketingQuestionnaire();
    }


    String recoverDataFromGraphicalMaterialOwnOffers(){
        String grafic_material_op="";
        if(this.chk_op_1.isChecked()){
            grafic_material_op = "cenefa";
        }
        if(this.chk_op_2.isChecked()){
            if(grafic_material_op.equals("")){
                grafic_material_op += "collarin";
            }else{
                grafic_material_op += ";collarin";
            }
        }
        if(this.chk_op_3.isChecked()){
            if(grafic_material_op.equals("")){
                grafic_material_op += "copete";
            }else{
                grafic_material_op += ";copete";
            }
        }
        if(this.chk_op_4.isChecked()){
            if(grafic_material_op.equals("")){
                grafic_material_op += "folleto";
            }else{
                grafic_material_op += ";folleto";
            }
        }
        if(this.chk_op_5.isChecked()){
            if(grafic_material_op.equals("")){
                grafic_material_op += "periodico";
            }else{
                grafic_material_op += ";periodico";
            }
        }
        if(this.chk_op_6.isChecked()){
            if(grafic_material_op.equals("")){
                grafic_material_op += "anuncio";
            }else{
                grafic_material_op += ";anuncio";
            }
        }

        return grafic_material_op;
    }

    public String getDate(float año,float mes,float dia){
        String date="",day="",month="";

        if(dia<10) {
            day = "" + dia;
        }else{
            day = ""+dia;
            day = day.substring(0,day.length()-1);
        }
        if(mes<10) {
            month = "" + mes;
        }else{
            month = "" + mes;
            month = month.substring(0, month.length()-1);
        }

        date = año+month+day;
        date = date.substring(0,date.length()-1);
        date = date.replace(".","");

        return date;
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {

        JSONObject resp = null;
        try {
            resp = new JSONObject(stringResponse);

            if (resp.getString("error").isEmpty()){
                if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SHELF_REVISION.toString())) {
                    ProductShelfReview.updateProductShelfReviewToSent(getActivity(),current_map.get("id_product"),current_map.get("id_visit"));
                    aux_index++;
                    if (aux_index < aux_list.size())
                        prepareRequest();
                    else
                        prepareOffersRequest();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_PROMOTION.toString())) {
                    Offers.updateOfferToSent(getActivity(),current_map.get("id_product"),current_map.get("id_visit"));
                    aux_index++;
                    if (aux_index < aux_list.size())
                        prepareRequest();
                    else
                        prepareIncidencesRequest();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_QUALITY_INCIDENCE.toString())) {
                    QualityIncidence.updateQualityIncidenceToSent(getActivity(),current_map.get("id"));
                    aux_index++;
                    if (aux_index < aux_list.size())
                        prepareRequest();
                    else
                        endMarketingQuestionnaire();
                }
            }
            else
                endMarketingQuestionnaire();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void endMarketingQuestionnaire(){
        Toast.makeText(getActivity(),"Cuestionario de mercadeo enviado exitosamente",Toast.LENGTH_SHORT).show();
        (getActivity()).onBackPressed();
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        ((MainActivity)getActivity()).onBackPressed();
    }

    @Override
    public void collectDataFromQualityIncidence(Map<String, String> data) {
        setupQualityIncidencesTable();
    }

}

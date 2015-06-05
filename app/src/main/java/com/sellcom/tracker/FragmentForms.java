package com.sellcom.tracker;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.ExtraTasks;
import database.models.Form;
import database.models.QuestionsSectionForm;
import database.models.SectionsForm;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.TrackerManager;


/**
 * Created by hugo.figueroa on 29/04/15.
 */
public class FragmentForms extends Fragment implements UIResponseListenerInterface,
                                                        FragmentDialogEvidenceExtraTask.setSetImgPhoto,
                                                        View.OnClickListener,
                                                        ConfirmationDialogListener {

    final static public String              TAG                 = "forms";
    private Context                         context;
    private Fragment                        fragment;
    private FragmentManager                 fragmentManager;

    private LayoutInflater                  layoutInflater;
    private View[]                          view,
                                            viewSections;
    private RadioGroup[]                    rg;
    private TextView[]                      textView,
                                            textViewTitle,
                                            textViewDescription;
    private EditText[]                      editText;
    private Spinner[]                       spinner;
    private CheckBox[]                      checkBox;

    private LinearLayout                    linearContainer,
                                            linear_comment;
    private LinearLayout[]                  containerChecks;

    private String                          id_visit,
                                            id_generic_task,
                                            id_form,
                                            answerOption;

    private TextView                        txv_form_name,
                                            txv_form_description;


    private List<Map<String,String>>        dataForm,
                                            dataSectionForm,
                                            dataQuestionSectionForm;

    private int                             sizeArrayQuestions = 0,
                                            sizeSections,
                                            sizeQuestions;
    private String[]                        arrayMultipleOptions,
                                            arrayChecks;
    private Map<String,String>              responseForm,
                                            responseTask;
    private EditText                        edt_observation_extra_task,
                                            edt_comment_form;

    private ImageView                       imv_take_photo,
                                            imv_evidence_extra_task;
    private Bitmap                          image;
    private String                          imageEncode = "";
    private Uri                             mImageUri;
    private Thread                          encodeImage;



    private static final    int THUMBNAIL_ID = 1;

    public static final String TYPE_BINARY      = "Binaria";
    public static final String TYPE_INT         = "Entero";
    public static final String TYPE_DECIMAL     = "Decimal";
    public static final String TYPE_TEXT        = "Texto";
    public static final String TYPE_OPC_MULTI   = "Opcion Multiple";

    public static final String TYPE_CHECK       = "Seleccion Multiple";
    public static final String TYPE_IMAGE       = "Imagen";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();
        layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forms, container, false);

        linear_comment                  = (LinearLayout) view.findViewById(R.id.linear_comment);
        linear_comment.setVisibility(View.VISIBLE);
        edt_observation_extra_task      = (EditText)view.findViewById(R.id.edt_observation_extra_task);
        imv_take_photo                  = (ImageView)view.findViewById(R.id.imv_take_photo);
        imv_take_photo.setOnClickListener(this);
        imv_evidence_extra_task         = (ImageView)view.findViewById(R.id.imv_evidence_extra_task);
        imv_evidence_extra_task.setOnClickListener(this);


        id_visit                = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");
        id_generic_task         = getArguments().getString("id_generic_task");
        id_form                 = getArguments().getString("id_form");


        dataForm                = new ArrayList<Map<String, String>>();
        dataSectionForm         = new ArrayList<Map<String, String>>();
        dataQuestionSectionForm = new ArrayList<Map<String, String>>();

        Log.e("formularios","numero de forms: "+ Form.getAllByIDFormandIDTask(context,id_generic_task,id_form).size());
        dataForm.add(Form.getAllByIDFormandIDTask(context,id_generic_task,id_form).get(0));

        if(dataForm.get(0) != null){


            sizeSections = SectionsForm.getCountSectionsByIDFormandIDTask(context,id_generic_task,id_form);


            if(sizeSections != 0){

                dataSectionForm = SectionsForm.getAllByIDFormandIDTask(context, id_generic_task,id_form);
                for(int i = 0; i < sizeSections; i++){

                    sizeQuestions = QuestionsSectionForm.getCountQuestionByIDSection(context,dataSectionForm.get(i).get("id_section"));
                    for(int j=0; j < sizeQuestions; j++){
                        sizeArrayQuestions +=2; //Se le suman 2 para dejar un espacio extra para que alcancen los TextView ya que se ocupan en secciones
                    }
                    sizeArrayQuestions +=1;
                }

                txv_form_name           = (TextView)view.findViewById(R.id.txv_form_name);
                txv_form_name           = (TextView)view.findViewById(R.id.txv_form_name);
                txv_form_name.setText(dataForm.get(0).get("form").toString());

                txv_form_description    = (TextView)view.findViewById(R.id.txv_form_description);
                txv_form_description.setText(dataForm.get(0).get("description").toString());

                edt_comment_form        = (EditText)view.findViewById(R.id.edt_comment_form);

                linearContainer = (LinearLayout)view.findViewById(R.id.linearContainer);

                OnInit();

            }
        }else{
            linear_comment.setVisibility(View.GONE);
        }



        return view;
    }


    public void OnInit(){

        view                    = new View[sizeArrayQuestions];
        viewSections            = new View[sizeArrayQuestions];
        rg                      = new RadioGroup[sizeArrayQuestions];
        textView                = new TextView[sizeArrayQuestions];
        textViewTitle           = new TextView[sizeArrayQuestions];
        textViewDescription     = new TextView[sizeArrayQuestions];
        editText                = new EditText[sizeArrayQuestions];
        spinner                 = new Spinner[sizeArrayQuestions];
        containerChecks         = new LinearLayout[sizeArrayQuestions];
        int cont = 0;


        for (int i = 0; i < dataSectionForm.size(); i++) {

            viewSections[i]     = layoutInflater.inflate(R.layout.item_title_section,null);
            linearContainer.addView(viewSections[i]);

            textViewTitle[i] = (TextView) viewSections[i].findViewById(R.id.txv_title_section);
            textViewTitle[i].setText(dataSectionForm.get(i).get("title"));

            textViewDescription[i] = (TextView) viewSections[i].findViewById(R.id.txv_description_section);

            if(!dataSectionForm.get(i).get("description").isEmpty())
                textViewDescription[i].setText(dataSectionForm.get(i).get("description"));

            //sizeQuestions = QuestionsSectionForm.getCountQuestionByIDSection(context,dataSectionForm.get(i).get("id_section"));
            dataQuestionSectionForm = QuestionsSectionForm.getAllByIDSectionandIDTask(context, dataSectionForm.get(i).get("id_section") , id_generic_task);
            sizeQuestions = dataQuestionSectionForm.size();

            for(int j=0; j < sizeQuestions; j++){

                if(dataQuestionSectionForm.get(j).get("question_type").toString().equals(TYPE_BINARY)){


                    view[cont]     = layoutInflater.inflate(R.layout.item_form_question_binary,null);
                    linearContainer.addView(view[cont]);
                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_binary);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    rg[cont]       = (RadioGroup) view[cont].findViewById(R.id.rgp_binary);
                    cont++;

                }else if(dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_INT)){


                    view[cont]     = layoutInflater.inflate(R.layout.item_form_question_number,null);
                    linearContainer.addView(view[cont]);

                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_number);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    editText[cont] = (EditText) view[cont].findViewById(R.id.edt_answer_number);
                    cont++;

                }else if(dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_DECIMAL)){


                    view[cont] = layoutInflater.inflate(R.layout.item_form_question_float, null);
                    linearContainer.addView(view[cont]);

                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_float);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    editText[cont] = (EditText) view[cont].findViewById(R.id.edt_answer_float);
                    cont++;

                }else if(dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_TEXT)){


                    view[cont]     = layoutInflater.inflate(R.layout.item_form_question_text,null);
                    linearContainer.addView(view[cont]);

                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_text);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    editText[cont] = (EditText) view[cont].findViewById(R.id.edt_answer_text);
                    cont++;

                }else if( dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_OPC_MULTI)){



                    view[cont]     = layoutInflater.inflate(R.layout.item_form_question_radio,null);
                    linearContainer.addView(view[cont]);

                    String options = dataQuestionSectionForm.get(j).get("options").toString();
                    arrayMultipleOptions = options.split(",");

                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayMultipleOptions);

                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_radio);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    spinner[cont]  = (Spinner) view[cont].findViewById(R.id.spn_answer_radio);
                    spinner[cont].setAdapter(spinnerArrayAdapter);
                    cont++;

                }else if(dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_CHECK)){



                    String options = dataQuestionSectionForm.get(j).get("options").toString();
                    arrayChecks = options.split(",");
                    checkBox = new CheckBox[arrayChecks.length];

                    view[cont] = layoutInflater.inflate(R.layout.item_form_question_check,null);
                    linearContainer.addView(view[cont]);

                    textView[cont] = (TextView) view[cont].findViewById(R.id.txt_question_check);
                    textView[cont].setText(dataQuestionSectionForm.get(j).get("question").toString());

                    containerChecks[cont] = (LinearLayout) view[cont].findViewById(R.id.containerChecks);

                    for (int k = 0; k < checkBox.length; k++) {
                        checkBox[k] = new CheckBox(context);
                        checkBox[k].setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        checkBox[k].setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                        checkBox[k].setText(arrayChecks[k]);
                        containerChecks[cont].addView(checkBox[k]);
                    }
                    cont++;

                }else if(dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_IMAGE)){
                    //cont++;

                }

            }


        }



    }


    public void sendDataForm(){

        JSONObject jsonObjectAnswers = null;
        JSONArray jsonArrayAnswers = new JSONArray();

        if (this.edt_comment_form.getText().toString().isEmpty()) {
            edt_comment_form.setError(getString(R.string.error_empty_field));
            edt_comment_form.requestFocus();
        }else if(this.edt_observation_extra_task.getText().toString().isEmpty()) {
            edt_observation_extra_task.setError(getString(R.string.error_empty_field));
            edt_observation_extra_task.requestFocus();
        }else
        {

            if (dataForm.get(0) != null) {

                try {

                    int cont = 0;

                    for (int i = 0; i < dataSectionForm.size(); i++) {

                        dataQuestionSectionForm = QuestionsSectionForm.getAllByIDSectionandIDTask(context, dataSectionForm.get(i).get("id_section"), id_generic_task);
                        sizeQuestions = dataQuestionSectionForm.size();

                        for (int j = 0; j < sizeQuestions; j++) {


                            jsonObjectAnswers = new JSONObject();


                            if (dataQuestionSectionForm.get(j).get("question_type").toString().equals(TYPE_BINARY)) {

                                int selectedId = rg[cont].getCheckedRadioButtonId();

                                if (selectedId == R.id.rbtn_binary1) {
                                    answerOption = "1";
                                } else if (selectedId == R.id.rbtn_binary2) {
                                    answerOption = "0";
                                }


                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answerOption);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answerOption);

                                jsonArrayAnswers.put(jsonObjectAnswers);

                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_INT)) {

                                answerOption = editText[cont].getText().toString();
                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answerOption);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answerOption);

                                jsonArrayAnswers.put(jsonObjectAnswers);

                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_DECIMAL)) {

                                answerOption = editText[cont].getText().toString();
                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answerOption);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answerOption);

                                jsonArrayAnswers.put(jsonObjectAnswers);


                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_TEXT)) {

                                answerOption = editText[cont].getText().toString();
                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answerOption);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answerOption);

                                jsonArrayAnswers.put(jsonObjectAnswers);


                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_OPC_MULTI)) {

                                answerOption = spinner[cont].getSelectedItem().toString();
                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answerOption);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answerOption);

                                jsonArrayAnswers.put(jsonObjectAnswers);

                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_CHECK)) {

                                String answer = "";
                                int contador = 0;
                                for (int k = 0; k < checkBox.length; k++) {
                                    if (checkBox[k].isChecked()) {
                                        answer = answer + checkBox[k].getText().toString();

                                        if (contador >= 0 && k != checkBox.length - 1) {
                                            answer = answer + ",";
                                        }
                                        contador++;
                                    }
                                }

                                jsonObjectAnswers.put("id_question", dataQuestionSectionForm.get(j).get("id_question").toString());
                                jsonObjectAnswers.put("answer", answer);

                                QuestionsSectionForm.updateAnswer(context, dataQuestionSectionForm.get(j).get("id_question").toString(), answer);

                                jsonArrayAnswers.put(jsonObjectAnswers);


                                cont++;
                            } else if (dataQuestionSectionForm.get(j).get("question_type").equals(TYPE_IMAGE)) {
                                //cont++;

                            }


                        }


                    }


                    ExtraTasks.updateObservationByIDTask(context, id_generic_task, edt_observation_extra_task.getText().toString());

                    if (!imageEncode.equals("")) {
                        ExtraTasks.updateEvidenceByIDTask(context, id_generic_task, imageEncode);
                        Log.e("Evidencia:", "ev:  " + imageEncode);

                    }

                    Form.updateAnswersTimestampCommentToIDForm(context, id_generic_task, id_form, jsonArrayAnswers.toString(),
                            DatesHelper.getStringDate(new Date()), edt_comment_form.getText().toString()); //Guardamos el arreglo de respuestas en la BD,la fecha y el comentario en la BD

/*
                JSONObject requestData = getJSONWithCredentials();
                try {
                    requestData.put("form_id", id_form);
                    requestData.put("visit_id", id_visit);
                    requestData.put("date_time", DatesHelper.getStringDate(new Date()));
                    requestData.put("comment", txv_comment_form.getText().toString());
                    requestData.put("answers", jsonArrayAnswers.toString());
                    requestData.put("request", METHOD.SET_FORM_TEST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestManager.sharedInstance().setListener(this);
                RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData, METHOD.SET_FORM_TEST);

*/


                    responseForm = new HashMap<String, String>();
                    responseForm.put("form_id", id_form);
                    responseForm.put("visit_id", id_visit);
                    responseForm.put("date_time", DatesHelper.getStringDate(new Date()));
                    responseForm.put("comment", edt_comment_form.getText().toString());
                    responseForm.put("answers", jsonArrayAnswers.toString());


                    prepareRequest(METHOD.SET_FORM_TEST, responseForm);

                    Log.d("Respuestas Totales:", "answers:  " + jsonArrayAnswers.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {//Si no hay formulario solo hay que enviar las observaciones y la evidencia

                ExtraTasks.updateObservationByIDTask(context, id_generic_task, edt_observation_extra_task.getText().toString());

                if (imageEncode != null)
                    ExtraTasks.updateEvidenceByIDTask(context, id_generic_task, imageEncode);

            }

        }


    }

    public JSONObject   getJSONWithCredentials(){
        String token                = Session.getSessionActive(context).getToken();
        String username             = User.getUser(context, Session.getSessionActive(context).getUser_id()).getEmail();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token",token);
            jsonObject.put("user",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                //Toast.makeText(context,"Enviando...",Toast.LENGTH_SHORT).show();
                sendDataForm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
        /**** Request manager stub
         * 0. Recover data from UI
         * 1. Add credentials information
         * 2. Set the RequestManager listener to 'this'
         * 3. Send the request (Via RequestManager)
         * 4. Wait for it
         */

        // 1
        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        //2
        RequestManager.sharedInstance().setListener(this);

        //3
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {

        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_FORM_TEST.toString())) {

                if(resp.getString("success").equalsIgnoreCase("true")) {
                    //if (resp.getString("error").isEmpty())

                    responseTask            = new HashMap<String, String>();
                    responseTask.put("id_visit",id_visit);
                    responseTask.put("id_generic_task",id_generic_task);
                    responseTask.put("observations",edt_observation_extra_task.getText().toString());
                    if (!imageEncode.equals("")){
                        responseTask.put("evidence",imageEncode);
                    }else{
                        responseTask.put("evidence","");
                    }


                     prepareRequest(METHOD.SET_GENERIC_TASK_RESULT, responseTask);

                }else{
                    RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_extra_task_sent), getActivity(), this);
                }
            }else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_GENERIC_TASK_RESULT.toString())) {
                if(resp.getString("success").equalsIgnoreCase("true")) {

                    ExtraTasks.updateExtraTasksToStatusSent(context,id_generic_task);
                    RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_extra_task_sent), getActivity(), this);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void okFromConfirmationDialog(String message) {
        ((MainActivity)getActivity()).onBackPressed();
    }

    @Override
    public void setImgPhoto(Bitmap bitmap) {
        image = bitmap;
        encodeImage = new Thread(new Runnable() {
            @Override
            public void run() {
                imageEncode = encodeTobase64(image);
            }
        });
        encodeImage.run();
        Log.d(TAG,"Encoded: "+imageEncode);
        imv_evidence_extra_task.setImageBitmap(image);

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

    @Override
    public void retry() {
        takePhoto();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.imv_take_photo:
                takePhoto();
                break;
            case R.id.imv_evidence_extra_task:
                if (imv_evidence_extra_task.getDrawable() != null){
                    openDialog(image);
                }
                break;
        }

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
        FragmentDialogEvidenceExtraTask dialogo = new FragmentDialogEvidenceExtraTask();
        dialogo.setSetSetImgPhotoListener(this);
        dialogo.photoResult(bitmap);
        dialogo.show(fragmentManager, FragmentDialogEvidenceExtraTask.TAG);
    }



}

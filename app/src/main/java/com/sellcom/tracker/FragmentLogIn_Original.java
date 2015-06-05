package com.sellcom.tracker;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.Permission;
import database.models.Profile;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.Utilities;

public class FragmentLogIn_Original extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener, UIResponseListenerInterface, DataBaseManager.InitialInformationDownloadInterface {

    public static boolean TEST  = true;

    public  final   String LOG_LOGIN_FRAGMENT    = "login_fragment";

    EditText                usuario;
    EditText                pass;
    Button                  ingresar;
    TextView                versionText;

    String                  textEmail;
    String                  textPassword;

    JSONObject              resp;
    ProgressDialog          dialog;
    Context                 context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login,container,false);

        RequestManager.sharedInstance().setActivity(this.getActivity());

        if (view != null) {

            context     = getActivity();
            usuario     = (EditText) view.findViewById(R.id.emailUser);
            pass        = (EditText) view.findViewById(R.id.passwordUser);
            ingresar    = (Button) view.findViewById(R.id.sign_in_button);
            versionText = (TextView) view.findViewById(R.id.version_text);

            if (TEST){
                usuario.setText("DFLC02@correo.com");
                pass.setText("demo");
            }

            ingresar.setOnClickListener(this);

            pass.setOnEditorActionListener(this);

            try {
                PackageManager manager = getActivity().getPackageManager();
                PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                versionText.setText(info.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            ingresar.performClick();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.sign_in_button:
                textEmail       = usuario.getText().toString();
                textPassword    = pass.getText().toString();

                if (textEmail.isEmpty()) {
                    usuario.setError(getString(R.string.error_empty_field));
                    usuario.requestFocus();
                    return;
                }
                else if (textPassword.isEmpty()){
                    pass.setError(getString(R.string.error_empty_field));
                    pass.requestFocus();
                    return;
                }

                Utilities.hideKeyboard(context, pass);

                boolean test_mode = false;

                if (test_mode){

                    startMainActivity();
                }else {

                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("request", METHOD.LOGIN.toString());
                    params.put("user", textEmail);
                    params.put("password", textPassword);

                    RequestManager.sharedInstance().setListener(this);
                    RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, METHOD.LOGIN);
                    break;
                }
        }
    }

    public void requestInitialInformation() {
        DataBaseManager.sharedInstance().startInitialInformationDownload(getActivity(),this,true);
    }

    public void startMainActivity(){
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
    }

    @Override
    public void decodeResponse(String stringResponse) {
        JSONObject  resp;
        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.LOGIN.toString())){


                    if (resp.getBoolean("success")) {
                        //usuario.setText("");
                        //pass.setText("");

                        String textToken = resp.getString("token");

                        RequestManager.sharedInstance().saveInPreferencesKeyAndValue("user_email", textEmail);
                        RequestManager.sharedInstance().saveInPreferencesKeyAndValue("password", textPassword);
                        RequestManager.sharedInstance().saveInPreferencesKeyAndValue("token", textToken);

                        RequestManager.sharedInstance().user_name = textEmail;

                        // Force to salesman profile
                        int profileId = 2;
                        String profileName = "Level 1";

                        Cursor user = User.getUserForEmail(context, textEmail);
                        if (user != null && user.getCount() > 0) {
                            int user_id = user.getInt(user.getColumnIndex(User.ID));

                            Session.closeSession(context);
                            Session.updateToken(context, 1, "fecha", textToken, textToken, user_id);
                        } else {
                            long userId = User.insert(getActivity(), textEmail, textPassword, profileId, 0);
                            Session.closeSession(context);
                            Session.insert(context, 1, "date", textToken, "001", (int) userId);

                            if (Profile.getProfile(context, profileName) == null) {
                                Profile.insert(context, profileId, profileName);

                                if (profileId == 1)
                                    Permission.setFullPermission(context, profileName);
                                else
                                    Permission.setBasicPermission(context, profileName);
                            }
                        }
                    } else {

                        RequestManager.sharedInstance().showErrorDialog(getString(R.string.req_man_error_contacting_service), getActivity());
                        return;
                    /*
                    String username = RequestManager.sharedInstance().getPreferencesValueForKey("user_email");
                    String password = RequestManager.sharedInstance().getPreferencesValueForKey("password");

                    if (textEmail.equalsIgnoreCase(username) && textPassword.equalsIgnoreCase(password)){
                        String message    = "Error en la red, entrada a la aplicación modo offline";
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                    }
                    else{
                        RequestManager.sharedInstance().showErrorDialog(getString(R.string.req_man_login_info_error),getActivity());
                        return;
                    }
                    */
                    }


                    String str_prod_timestamp = RequestManager.sharedInstance().getPreferencesValueForKey("initial_info_timestamp");
                    Log.d(LOG_LOGIN_FRAGMENT, "Stored: " + str_prod_timestamp);
                    if (str_prod_timestamp.equalsIgnoreCase("CLEAR"))  // First login ever
                        requestInitialInformation();
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date last_update = dateFormat.parse(str_prod_timestamp);
                        if (DatesHelper.sharedInstance().daysFromLastUpdate(last_update) >= 1) { // Last update was more than a day ago
                            requestInitialInformation();
                        } else {
                            Log.d(LOG_LOGIN_FRAGMENT, "Start main activity");
                            startMainActivity();
                        }
                    }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialInformationDownloadFinished() {
        startMainActivity();
    }
}
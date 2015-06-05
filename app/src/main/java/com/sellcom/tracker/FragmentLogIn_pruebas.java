package com.sellcom.tracker;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import database.models.Permission;
import database.models.Profile;
import database.models.Session;
import database.models.User;
import util.LogUtil;
import util.Utilities;
import volleyhandler.ApplicationController;
import volleyhandler.VolleyErrorHelper;

/**
 * Created by dmolinero on 22/05/14.
 */
public class FragmentLogIn_pruebas extends Fragment implements TextView.OnEditorActionListener {

    EditText usuario;
    EditText pass;
    Button ingresar;
    TextView versionText;

    String textEmail;
    String textPassword;

    JSONObject resp;
    ProgressDialog dialog;
    Context context;
    boolean isFirst;
    int user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        if (view != null) {

            context = getActivity();
            usuario = (EditText) view.findViewById(R.id.emailUser);
            pass = (EditText) view.findViewById(R.id.passwordUser);
            ingresar = (Button) view.findViewById(R.id.sign_in_button);
            versionText = (TextView) view.findViewById(R.id.version_text);

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
        textEmail = usuario.getText().toString();
        textPassword = pass.getText().toString();

        Utilities.hideKeyboard(context, pass);

        final Map<String, String> params = new HashMap<String, String>();

        params.put("request", "login");
        params.put("user", textEmail);
        params.put("password", textPassword);

        boolean success = false;
        String textRespuesta = "";
        String textToken = "";

        success = true;
        textRespuesta = "Usuario valido";
        textToken = "1";
        textEmail = "vf_plaza";


        if (!success) {
            Toast.makeText(context, textRespuesta, Toast.LENGTH_SHORT).show();

        } else {
            usuario.setText("");
            pass.setText("");
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.getDeviceId();

            Cursor user = User.getUserForEmail(context, textEmail);
            if (user != null && user.getCount() > 0) {
                user_id = user.getInt(user.getColumnIndex(User.ID));

                Session.closeSession(context);
                Session.updateToken(context, 1, "fecha", textToken, android.os.Build.MODEL + " - " + telephonyManager.getDeviceId(), user_id);
//                        if (unlock_code == 1111)
                isFirst = false;

                LogUtil.addCheckpoint("User token updated on DB");
            } else {
                int contPeople = 0;
                Cursor userAux = User.getAll(context);

                if (userAux != null)
                    contPeople = userAux.getCount();

                int profileId;
                String profileName;
                if (textEmail.equals("vf_plaza")) {
                    profileId = 1;
                    profileName = "Level 1";
                } else {
                    profileId = 2;
                    profileName = "Level 2";
                }

                long userId = User.insert(getActivity(), textEmail, textPassword, profileId, contPeople);
                Session.closeSession(context);
                Session.insert(context, 1, "date", textToken, android.os.Build.MODEL + " - " + telephonyManager.getDeviceId(), (int) userId);

                Profile.insert(context, profileId, profileName);

                if (profileId == 1)
                    Permission.setFullPermission(context, profileName);
                else
                    Permission.setBasicPermission(context, profileName);

                isFirst = true;
                LogUtil.addCheckpoint("New user inserted on DB");
            }

            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            Session sessionActive = Session.getSessionActive(context);

            Log.w("DATOS de SESSION active", "id: " + sessionActive.getId());
            Log.w("DATOS de SESSION active", "status: " + sessionActive.getStatus());
            Log.w("DATOS de SESSION active", "timestamp: " + sessionActive.getTimestamp());
            Log.w("DATOS de SESSION active", "token: " + sessionActive.getToken());
            Log.w("DATOS de SESSION active", "device: " + sessionActive.getDevice());
            Log.w("DATOS de SESSION active", "user id: " + sessionActive.getUser_id());
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

}

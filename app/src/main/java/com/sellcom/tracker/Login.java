package com.sellcom.tracker;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import async_request.RequestManager;

public class Login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle extras = getIntent().getExtras();
        boolean isFromLogout    = false;
        if (extras!=null)
            isFromLogout = extras.getBoolean("isFromLogout");

        if (isFromLogout){
            RequestManager.sharedInstance().endSession();
        }

        FragmentLogIn_Original frag = new FragmentLogIn_Original();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.layoutLogin,frag,"LogIn");
        ft.commit();
    }
}




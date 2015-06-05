package util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sellcom.tracker.R;

/**
 * Created by Raiseralex21 on 29/01/15.
 */
public class FragmentAlertGeneric extends DialogFragment implements View.OnClickListener{

    public final static String TAG = "dialog_generic";

    public final static int ALERT_ERROR = 1;
    ImageView alertIcon;

    TextView  alertMsn;

    Button    buttonOk,
              buttonCancel,
              buttonRetry;

    public FragmentAlertGeneric() {
        super();
    }

    public void onCreate(Bundle sacedInstanceState){
        super.onCreate(sacedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_alert_generic, container, false);

        if (view != null){

            alertIcon    = (ImageView) view.findViewById(R.id.imv_alertG_icon);
            alertMsn     = (TextView)  view.findViewById(R.id.txt_alertG_textMessage);

            buttonCancel = (Button)    view.findViewById(R.id.btn_alertG_cancel);
            buttonOk     = (Button)    view.findViewById(R.id.btn_alertG_ok);
            buttonRetry  = (Button)    view.findViewById(R.id.btn_alertG_retry);

            buttonCancel.setOnClickListener(this);
            buttonOk.setOnClickListener(this);
            buttonRetry.setOnClickListener(this);

        }

        return view;

    }

    public void typeMessage(int type, String message){

        if(type == 1){

        }else{

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_alertG_cancel:
                Toast.makeText(getActivity(),"CANCEL",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_alertG_ok:
                Toast.makeText(getActivity(),"OK",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_alertG_retry:
                Toast.makeText(getActivity(),"RETRY",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}

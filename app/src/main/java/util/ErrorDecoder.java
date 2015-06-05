package util;

import android.content.Context;

import com.sellcom.tracker.R;

/**
 * Created by raymundo.piedra on 29/01/15.
 */
public class ErrorDecoder {

    public static String decodeErrorMessage(String error, Context context){
        String errorMessage = context.getString(R.string.req_man_login);
        if (error.toLowerCase().contains("has already started")){
            return context.getString(R.string.req_man_error_visit_already_starter_1)+error.split("[\\(\\)]")[1]
                    + context.getString(R.string.req_man_error_visit_already_starter_2)
                    + " ("+error.split("[\\(\\)]")[3]+")";
        }
        else if (error.toLowerCase().contains("error_cerrada")){
            return context.getString(R.string.req_man_error_visit_already_starter_1)+error.split("[\\(\\)]")[1]
                    + ") " +context.getString(R.string.req_man_error_visit_already_ended_2)
                    + " ("+error.split("[\\(\\)]")[3]+")";
        }
        else if (error.toLowerCase().contains("incomplete_data_new_client")){
            return context.getString(R.string.req_man_error_incomplete_data_new_client);
        }
            return error;
    }
}

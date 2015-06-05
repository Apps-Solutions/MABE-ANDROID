package util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import database.models.Session;
import database.models.User;

/**
 * Created by juanc.jimenez on 22/05/14.
 */
public class Utilities {

    public static boolean isHandset(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void hideKeyboard(Context context, View editField) {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }

    public static Bitmap stringToBitmap(String encodedString) {

        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static String setReceiptMoneyNumberFormat (double amount, int strLength){
        DecimalFormat form  = new DecimalFormat("0.00");
        String strAmount    = form.format(amount);
        while (strAmount.length()<strLength){
            strAmount = " "+strAmount;
        }
        strAmount = "$"+strAmount;

        return strAmount;
    }

    public static String formatProductName(String prod_name){
        String[] result = prod_name.split("\\s");
        String ret = "";
        for (int x=1; x<result.length; x++)
            ret = ret + result[x];

        return ret.substring(0,Math.min(14,ret.length()-1));
    }

    public static boolean emailValidator(String strMail){
        if (strMail.matches("[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+") && strMail.length() > 0)
            return true;
        else
            return false;
    }

    public static boolean CURPValidator(String strCURP)
    {
        if (strCURP.isEmpty())
            return false;

        String regex =  "[A-Z]{1}" +
                        "[AEIOU]{1}" +
                        "[A-Z]{2}" +
                        "[0-9]{2}" +
                        "(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])" +
                        "[HM]{1}" +
                        "(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)" +
                        "[B-DF-HJ-NP-TV-Z]{3}" +
                        "[0-9A-Z]{1}[0-9]{1}$";

        Pattern patron = Pattern.compile(regex);

        if(!patron.matcher(strCURP).matches())
            return false;
        else
            return true;
    }

    public static boolean RFCValidator(String strRFC)
    {
        if (strRFC.isEmpty())
            return false;

        return (strRFC.toUpperCase().matches("[A-Z]{4}[0-9]{6}[A-Z0-9]{3}") ||
                strRFC.toUpperCase().matches("[A-Z]{3}[0-9]{6}[A-Z0-9]{3}"));
    }

    public static String formatNumberWithLength(String string, int length){
        if (string.equalsIgnoreCase("null")){
            string  = "";
        }

        StringBuilder sb = new StringBuilder();

        for (int toPrepend=length-string.length(); toPrepend>0; toPrepend--) {
            sb.append('0');
        }
        sb.append(string);
        return sb.toString();
    }

    public static JSONObject mapToJson(Map<String, String> data){
        JSONObject object = new JSONObject();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            String key = entry.getKey();
            try
            {
                if (key.equalsIgnoreCase("evidence")) {
                    String str_1 = entry.getValue().replaceAll("[\\t\\n\\r]", "");
                    object.put(key, str_1.replaceAll("\\\\\\\\",""));
                }
                else
                    object.put(key,entry.getValue());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return object;
    }

    public static String validateWithZeroDefault(String str){
        if (str.trim().isEmpty())
            return "0";
        else
            return str;
    }

    public static String validateWithBlankSpaceDefault(String str){
        if (str.trim().isEmpty())
            return "";
        else
            return str;
    }

    public static String validateDateWithDateTimeDefault(String str){
        if (str.trim().isEmpty())
            return DatesHelper.getStringDate(new Date());
        else
            return str;
    }

    public static JSONObject   getJSONWithCredentials(Context context){
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
}

package com.sellcom.tracker;

import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import location.GPSTracker;

/**
 * Created by juanc.jimenez on 12/05/14.
 */
public class FragmentWeather extends Fragment implements View.OnClickListener{

    TextView cityText, tempText, conditionText, maxTempText, minTempText;
    ImageButton refreshButton;
    ImageView maxTempIcon, minTempIcon, weatherIcon;
    LinearLayout weatherErrorLayout, weatherLayout;
    ProgressBar weatherProgress;
    String city, temperature, condition, maxTemperature, minTemperature, WOEID;
    Location location;
    GPSTracker gpsTracker;
    RequestQueue requestQueue;

    private static final String TAG = "Weather - ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gpsTracker = new GPSTracker(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        if (view != null) {

            cityText = (TextView) view.findViewById(R.id.city_text);
            tempText = (TextView) view.findViewById(R.id.temp_text);
            conditionText = (TextView) view.findViewById(R.id.condition_text);
            maxTempText = (TextView) view.findViewById(R.id.max_temp_text);
            minTempText = (TextView) view.findViewById(R.id.min_temp_text);
            weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
            refreshButton = (ImageButton) view.findViewById(R.id.refresh_button);
            weatherErrorLayout = (LinearLayout) view.findViewById(R.id.weather_error_layout);
            weatherLayout = (LinearLayout) view.findViewById(R.id.weather_layout);
            weatherProgress = (ProgressBar) view.findViewById(R.id.weather_progress);
            maxTempIcon = (ImageView) view.findViewById(R.id.max_temp_icon);
            minTempIcon = (ImageView) view.findViewById(R.id.min_temp_icon);

            weatherErrorLayout.setVisibility(View.GONE);
            weatherLayout.setOnClickListener(this);
            refreshButton.setOnClickListener(this);
        }

        return view;

    }

    @Override
    public void onClick(View view) {
        refreshWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshWeather();
    }

    private void getWOEID(Location position){
        String woeid_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text%3D%22"+ position.getLatitude()  +"%2C"+ position.getLongitude() +"%22%20and%20gflags%3D%22R%22&format=json";
        Log.d(TAG + "URL for WOEID", woeid_URL);
        StringRequest myReq = new StringRequest(Request.Method.GET,
                woeid_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject response = new JSONObject(s);

                            WOEID = response.getJSONObject("query").getJSONObject("results").getJSONObject("Result").getString("woeid");
                            Log.i(TAG , "City code: " + WOEID);
                            getWeather(WOEID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                showErrorLayout();
            }
        }
        );
        requestQueue.add(myReq);
        showLoadingLayout();
    }

    private void getWeather(String WOEID) {

        String weather_URL = "http://weather.yahooapis.com/forecastrss?w=" + WOEID + "&u=c";
        Log.d(TAG + "URL for weather", weather_URL);
        StringRequest myReq = new StringRequest(Request.Method.GET,
                weather_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        showWeatherLayout();
                        if (isAdded())
                            parseResponse(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                showErrorLayout();
            }
        }
        );
        requestQueue.add(myReq);
    }

    private void parseResponse(String response){

        String qResult = "";

        try {
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());

            Reader in = new InputStreamReader(inputStream);
            BufferedReader bufferedreader = new BufferedReader(in);
            StringBuilder stringBuilder = new StringBuilder();
            String stringReadLine;
            while ((stringReadLine = bufferedreader.readLine()) != null) {
                stringBuilder.append(stringReadLine + "\n");
            }
            qResult = stringBuilder.toString();

            bufferedreader.close();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorLayout();
        }

        Document dest = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser;
        try {
            parser = dbFactory.newDocumentBuilder();
            dest = parser.parse(new ByteArrayInputStream(qResult.getBytes()));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            showErrorLayout();
        } catch (SAXException e) {
            e.printStackTrace();
            showErrorLayout();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorLayout();
        }

        if (dest != null) {

            try {
                Node locationNode = dest.getElementsByTagName("yweather:location").item(0);
                city = locationNode.getAttributes().getNamedItem("city").getNodeValue();
                city = city + ", " + locationNode.getAttributes().getNamedItem("country").getNodeValue();

                Node temperatureNode = dest.getElementsByTagName("yweather:condition").item(0);
                temperature = temperatureNode.getAttributes().getNamedItem("temp").getNodeValue() + getString(R.string.units_celsius);

                Node conditionNode = dest.getElementsByTagName("yweather:condition").item(0);
                int condition_code = Integer.valueOf(conditionNode.getAttributes().getNamedItem("code").getNodeValue());

                if (condition_code != 3200) {
                    condition = getResources().getStringArray(R.array.weather_conditions)[condition_code];
                    setIcon(condition_code);
                } else
                    condition = getString(R.string.weather_unavailable);

                Node forecastNode = dest.getElementsByTagName("yweather:forecast").item(0);
                maxTemperature = forecastNode.getAttributes().getNamedItem("high").getNodeValue() + getString(R.string.units_celsius);
                minTemperature = forecastNode.getAttributes().getNamedItem("low").getNodeValue() + getString(R.string.units_celsius);

                cityText.setText(city);
                tempText.setText(temperature);
                maxTempText.setText(maxTemperature);
                minTempText.setText(minTemperature);
                conditionText.setText(condition);
                minTempIcon.setImageResource(android.R.drawable.stat_sys_download);
                maxTempIcon.setImageResource(android.R.drawable.stat_sys_upload);
            } catch (Exception e) {
                e.printStackTrace();
                showErrorLayout();
            }
        }
        else
            showErrorLayout();

    }

    private void refreshWeather() {

        showLoadingLayout();
        location = gpsTracker.getCurrentLocation();
        if (location != null)
            getWOEID(location);
        else {
            Toast.makeText(getActivity(), getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show();
        }
    }

    public void showErrorLayout() {

        hideLayouts();
        if (weatherErrorLayout != null)
            weatherErrorLayout.setVisibility(View.VISIBLE);
    }

    public void showLoadingLayout() {
        hideLayouts();
        if (weatherProgress != null)
            weatherProgress.setVisibility(View.VISIBLE);
    }

    public void showWeatherLayout() {
        hideLayouts();
        if (weatherLayout != null)
            weatherLayout.setVisibility(View.VISIBLE);
    }

    public void hideLayouts() {
        if (weatherErrorLayout.getVisibility() != View.GONE)
            weatherErrorLayout.setVisibility(View.GONE);
        if (weatherProgress.getVisibility() != View.GONE)
            weatherProgress.setVisibility(View.GONE);
        if (weatherLayout.getVisibility() != View.GONE)
            weatherLayout.setVisibility(View.GONE);
    }

    public void setIcon(int condition_code) {

        TypedArray typedArray = getResources().obtainTypedArray(R.array.weather_icons);
        int resource_id = typedArray.getResourceId(condition_code, 0);
        weatherIcon.setImageResource(resource_id);
    }
}

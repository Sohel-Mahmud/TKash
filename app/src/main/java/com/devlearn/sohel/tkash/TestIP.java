package com.devlearn.sohel.tkash;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devlearn.sohel.tkash.IPClass.IPCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

public class TestIP extends AppCompatActivity {
    public  IPCheck ipCheck;
    TextView txtIP, txtipDetails;
    String errorMessage;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ip);

        txtIP = findViewById(R.id.txtIP);
        txtipDetails = findViewById(R.id.ipDetails);

        ipCheck = new IPCheck();
        RequestQueue queue = Volley.newRequestQueue(this);

        String urlip = "http://checkip.amazonaws.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                url ="http://ip-api.com/json/" +response;
                ipCheck.setIp(response);
                txtIP.setText(response);
                getIPDetails(url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtIP.setText("didnt work");
            }
        });

        queue.add(stringRequest);



    }

    private void getIPDetails(String url) {
        RequestQueue queue2 = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ipCheck.setCountry(response.getString("country"));
                    ipCheck.setIsp(response.getString("isp"));
                    ipCheck.setCity(response.getString("city"));
                    ipCheck.setTimezone(response.getString("timezone"));

                    txtipDetails.setText(ipCheck.getCountry()+" "+ipCheck.getCity()+" "+ipCheck.getIsp()+" "+ipCheck.getIp());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtipDetails.setText("Error "+error.getMessage());
            }
        });

        queue2.add(jsonObjectRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}

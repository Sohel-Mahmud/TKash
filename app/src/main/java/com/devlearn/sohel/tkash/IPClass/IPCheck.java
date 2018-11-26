package com.devlearn.sohel.tkash.IPClass;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPCheck{


    private String country;
    private String city;
    private String countryCode;
    private double latitude;
    private double longtidue;
    private String region;
    private String timezone;
    private String isp;
    private String ip;
    private Context context;
    private String error1;


    private String error2;

    private RequestQueue queue;

    public IPCheck(Context context){
        this.context=context;
    }

    public IPCheck(String country, String city, String countryCode, double latitude, double longtidue, String region, String timezone, String isp,String ip, String error1,String error2) {
        this.country = country;
        this.city = city;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longtidue = longtidue;
        this.region = region;
        this.timezone = timezone;
        this.isp = isp;
        this.ip =  ip;
        this.error1 = error1;
        this.error2 = error2;
    }

    public IPCheck() {

    }

    public void GetIp() {

        RequestQueue queue = Volley.newRequestQueue(context);

        String urlip = "http://checkip.amazonaws.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String newResponse="";
                for(int i=0; i<response.length(); i++){
                    if(response.charAt(i)==','){
                        break;
                    }
                    newResponse = newResponse+response.charAt(i);
                }
                String url = "http://ip-api.com/json/" +newResponse;
//                    Log.d("actuallink","link: "+url);
                setIp(newResponse);
                getIPDetails(url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setError1("Loading..");
            }
        });

        queue.add(stringRequest);

    }
    private void getIPDetails(String url) {
        RequestQueue queue2 = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String cntry = response.getString("country");
                    setCountry(cntry);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setError2("Error "+error.getMessage());
            }
        });

        queue2.add(jsonObjectRequest);
    }

/*    public void GetIPAddress(RequestQueue queue, final RequestQueue queue2){
        String urlip = "http://checkip.amazonaws.com/";
        // alternative https://ipinfo.io/ip

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String url ="http://ip-api.com/json/" +response;
                setIp(response);
                getIPDetails(url, queue2);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setError1("didnt work");
            }
        });

        queue.add(stringRequest);
    }

    private void getIPDetails(String url, RequestQueue queue2) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    setCountry(response.getString("country"));

                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setError2("Error "+error.getMessage());
            }
        });

        queue2.add(jsonObjectRequest);
    }*/

    public String getError1() {
        return error1;
    }

    public void setError1(String error1) {
        this.error1 = error1;
    }

    public String getError2() {
        return error2;
    }

    public void setError2(String error2) {
        this.error2 = error2;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtidue() {
        return longtidue;
    }

    public void setLongtidue(double longtidue) {
        this.longtidue = longtidue;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}

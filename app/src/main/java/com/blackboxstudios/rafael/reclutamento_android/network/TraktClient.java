package com.blackboxstudios.rafael.reclutamento_android.network;

import android.provider.SyncStateContract;
import android.util.Log;

import com.blackboxstudios.rafael.reclutamento_android.utils.Constants;
import com.blackboxstudios.rafael.reclutamento_android.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rafael on 30/09/2015.
 */
public class TraktClient {


    private static final String TAG = "FETCHING_INFO";
    private static String rating;

    public static JSONArray getEpisodes() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        JSONArray jsonArray = null;
        String jsonString = "";

        try {
            //Final URL for request
            URL url = new URL(Constants.episodesUrl);

            //Creation for the request of movies data
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty(Constants.content_type, Constants.content_typeValue);
            urlConnection.addRequestProperty(Constants.apiKey, Constants.apiKeyValue);
            urlConnection.addRequestProperty(Constants.apiVersion, Constants.apiVersionValue);
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }



            jsonString = buffer.toString();
            Log.w(TAG,jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }


        // try parse the string to a JSON object
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
        return jsonArray;
    }

    public static String getRating() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        JSONObject jsonObject = null;
        String jsonString = "";


        try {
            //Final URL for request
            URL url = new URL(Constants.ratingsUrl);

            //Creation for the request of movies data
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty(Constants.content_type, Constants.content_typeValue);
            urlConnection.addRequestProperty(Constants.apiKey, Constants.apiKeyValue);
            urlConnection.addRequestProperty(Constants.apiVersion, Constants.apiVersionValue);
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }



            jsonString = buffer.toString();
            Log.w(TAG,jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(jsonString);
            rating = jsonObject.get("rating").toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return Utils.trimString(rating);
    }
}

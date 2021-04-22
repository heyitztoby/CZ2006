package com.example.parkez;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.parkez.Model.CarparkAvailability;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetCarparkData extends AsyncTask<Void, Void, String> {

    private Callback obj;

    public GetCarparkData(Callback callback) {
        this.obj = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String urlString = "https://api.data.gov.sg/v1/transport/carpark-availability";
        try {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            is.close();
            return stringBuilder.toString();
        } catch (SocketTimeoutException e) {
            Log.e("GetCarpark", "An error occurred getting carpark data");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Gson gson = new Gson();
        CarparkAvailability cp = null;
        if (s !=  null) {
            try {
                cp = gson.fromJson(s, CarparkAvailability.class);
            } catch (JsonSyntaxException e) {
                Log.e("GetCarpark", "Invalid Json String");
            }
        }

        obj.onCallback(cp);
    }

    interface Callback {
        void onCallback(@Nullable CarparkAvailability availability);
    }
}

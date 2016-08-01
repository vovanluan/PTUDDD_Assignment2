package com.yuyo.hikaru.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.DataHolder;
import entity.Local;
import entity.User;
import support.Preference;
import support.Support;

public class SplashActivity extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    SharedPreferences pref;
    Local local;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.spash_screen);

        pref = getSharedPreferences(Preference.PREF_NAME, MODE_PRIVATE);
        String EMAIL = pref.getString(Preference.PREF_EMAIL, null);
        String PASSWORD = pref.getString(Preference.PREF_PASSWORD, null);

        if (!(EMAIL == null && PASSWORD == null)) {
            String url = Support.HOST + "login";
            local = new Local();
            local.setEmail(EMAIL);
            local.setPassword(PASSWORD);

            new LoginRequest().execute(url);
        } else {
            /* New Handler to start the Menu-Activity
            * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);

        }


    }

    // This is from Login Activity
    // if change, remember to synchronize in LoginActivity.java
    private class LoginRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Step 1 : Create a HttpURLConnection object send REQUEST to server
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                Type type = new TypeToken<Local>() {
                }.getType();
                String json = gson.toJson(local, type);

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                // Step 2: wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                // Step 3: Arriving JSON fragments are concatenate into a StringBuilder
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("User", jsonResponse);
                return urlConnection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.toString());
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<User>() {
                    }.getType();
                    //user = gson.fromJson(jsonResponse, type);
                    DataHolder.getInstance().setUser((User) gson.fromJson(jsonResponse, type));
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    //intent.putExtra("User", jsonResponse);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Some thing went wrong, cannot Log In", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    //intent.putExtra("User", jsonResponse);
                    startActivity(intent);
                }
            } catch (Exception e) {

            }
        }
    }

}
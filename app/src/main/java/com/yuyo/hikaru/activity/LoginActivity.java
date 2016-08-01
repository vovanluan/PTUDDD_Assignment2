package com.yuyo.hikaru.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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
import mehdi.sakout.fancybuttons.FancyButton;
import services.NotificationService;
import support.Preference;
import support.Support;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    FancyButton login, signup;
    FancyButton startService, stopService;
    User user;
    Local local;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.login);
        signup = (FancyButton) findViewById(R.id.signup);
        // New Code
//        startService = (FancyButton) findViewById(R.id.btnStartService);
//        stopService = (FancyButton) findViewById(R.id.btnStopService);

        final SharedPreferences pref = getSharedPreferences(Preference.PREF_NAME, MODE_PRIVATE);
        String EMAIL = pref.getString(Preference.PREF_EMAIL, null);
        String PASSWORD = pref.getString(Preference.PREF_PASSWORD, null);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Support support = new Support();
                if (!support.isValidEmail(email.getText().toString())) {
                    showError(email);
                    email.setError("Please enter your email");
                } else if (password.getText().toString().isEmpty() || password.getText().toString().contains(" ")) {
                    showError(password);
                    password.setError("Please enter your password");
                } else {
                    String url = Support.HOST + "login";
                    local = new Local();
                    local.setEmail(email.getText().toString());
                    local.setPassword(password.getText().toString());

                    // Store username and password for constant log in
                    getSharedPreferences(Preference.PREF_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(Preference.PREF_EMAIL, email.getText().toString())
                            .putString(Preference.PREF_PASSWORD, password.getText().toString())
                            .apply();

                    new LoginRequest().execute(url);
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        editText.startAnimation(shake);
    }

    private class LoginRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Log in...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

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
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<User>() {
                    }.getType();
                    //user = gson.fromJson(jsonResponse, type);
                    DataHolder.getInstance().setUser((User) gson.fromJson(jsonResponse, type));
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    //intent.putExtra("User", jsonResponse);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
                    password.setText("");
                }
            } catch (Exception e) {

            }
        }
    }

}


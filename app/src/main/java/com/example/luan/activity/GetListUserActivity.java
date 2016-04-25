package com.example.luan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.UserAdapter;
import entity.User;
import support.Support;

public class GetListUserActivity extends AppCompatActivity {
    ListView myListView;
    ArrayList<User> userList;
    UserAdapter adapter;
    User user;
    String jsonUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getuserlist);
        myListView = (ListView) findViewById(R.id.user_list);
        jsonUser =  getIntent().getStringExtra("User");
        Gson gson = new Gson();
        Type  type = new TypeToken<User>(){}.getType();
        user = gson.fromJson(jsonUser, type);
        // Get data (user list) from server
        String url = Support.HOST + "api/users/all";
        Log.e("URL", url);
        new GetListUserReQuest().execute(url);

        // Handle click event
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(GetListUserActivity.this, InformationActivity.class);
                User u = userList.get(position);
                String userString = new Gson().toJson(u);
                intent.putExtra("User", userString);
                startActivity(intent);
            }
        });
    }


    private class GetListUserReQuest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(GetListUserActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Load list user...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // Step 2: wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                // Step 3: Arriving JSON fragments are concatenate into a StringBuilder
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null){
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Json", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
                return urlConnection.getResponseCode();

            }
            catch (Exception e) {

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
                    Type  type = new TypeToken<ArrayList<User>>(){}.getType();
                    userList = gson.fromJson(jsonResponse, type);
                    Log.e("Size", String.valueOf(userList.size()));
                    adapter = new UserAdapter(getApplicationContext(), R.layout.short_info, userList);
                    //Attach the adapter to a ListView
                    myListView.setAdapter(adapter);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error while getting user list", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }

    }
}
package com.yuyo.hikaru.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;

import adapter.ParticipantAdapter;
import entity.DataHolder;
import entity.IdList;
import entity.User;
import support.Support;

public class ParticipantActivity extends AppCompatActivity {

    private TextView txtView;
    private Toolbar toolbar;
    private IdList participantIdList;
    private ArrayList<User> participantList;
    private ListView listView;


    String URL = Support.HOST + "users/userlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        initInstance();
    }

    private void initInstance() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Going List");

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        listView = (ListView) findViewById(R.id.myListView);

        Intent intent = getIntent();
        participantIdList = new IdList( intent.getStringArrayListExtra("Going List"));

        new GetUserListRequest().execute(URL);


    }

    private void bindToListView() {

        ParticipantAdapter adapter = new ParticipantAdapter(this, participantList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = participantList.get(position);

                // start new intent
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                // Send user information
                Gson gson = new Gson();
                Type type = new TypeToken<User>() {
                }.getType();
                String jsonUser = gson.toJson(selectedUser, type);
                i.putExtra("User", jsonUser);
                startActivity(i);
            }
        });
    }

    private class GetUserListRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(ParticipantActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // convert idlist to JSON
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<IdList>() {
                }.getType();
                String json = gson.toJson(participantIdList, type);

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
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
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<User>>() {
                    }.getType();
                    participantList = gson.fromJson(jsonResponse, type);
                    //Log.e("Size User", String.valueOf(DataHolder.getInstance().getUserList().size()));

                    bindToListView();
                }
/*                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("GOT_USER_LIST");
                sendBroadcast(broadcastIntent);*/
            } catch (Exception e) {

            }
        }
    }




}

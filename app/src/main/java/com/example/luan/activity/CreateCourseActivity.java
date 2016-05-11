package com.example.luan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import entity.Course;
import entity.DataHolder;
import entity.Local;
import support.Support;

/**
 * Created by Admin on 5/11/2016.
 */
public class CreateCourseActivity extends AppCompatActivity {
    private EditText title, description, place;
    private TextView creator;
    private Button create;
    private SearchableSpinner searchableSpinner;
    private ArrayList<String> languages;
    private Course course;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        title = (EditText) findViewById(R.id.title);
        creator = (TextView) findViewById(R.id.creator);
        place = (EditText) findViewById(R.id.place);
        description = (EditText) findViewById(R.id.description);
        create = (Button) findViewById(R.id.createBtn);
        searchableSpinner = (SearchableSpinner) findViewById(R.id.languageSpinner);

        creator.setText(DataHolder.getInstance().getData().getBio().getFirstName());
        searchableSpinner.setTitle("Select Item");
        searchableSpinner.setPositiveButton("OK");
        languages = new ArrayList<>();
        languages.add("English");
        languages.add("Vietnamese");
        languages.add("French");
        languages.add("Japanese");
        languages.add("Spanish");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.language_spinner, languages) {

            @Override
            public boolean isEnabled(int position) {
                return position != 1;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.dropdown_spinner, null);
                }

                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setText(languages.get(position));

                switch (position) {
                    case 0:  tv.setTextColor(Color.RED);
                        break;
                    case 1:  tv.setTextColor(Color.BLUE);
                        break;
                    default:  tv.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };

        searchableSpinner.setAdapter(spinnerAdapter);

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                course = new Course();
                course.setCreate_by(DataHolder.getInstance().getData());
                course.setTitle(title.getText().toString());
                course.setDescription(description.getText().toString());

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                course.setCreatedAt(ts);

                course.setPlace(place.getText().toString());
                String postCourseURL = Support.HOST + "cards";
                new PostCardRequest().execute(postCourseURL);
            }
        });

    }

    private class PostCardRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(CreateCourseActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // Convert course object to json string
                Gson gson = new Gson();
                Type type = new TypeToken<Course>() {
                }.getType();
                String json = gson.toJson(course, type);

                Log.e("PostJSON", json);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

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
                Log.e("CODE", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<Course>(){}.getType();
                    course = gson.fromJson(jsonResponse, type);
                    DataHolder.getInstance().getData().getCourses().add(course);
                    Log.e("Course:", jsonResponse);
                    Toast.makeText(CreateCourseActivity.this, "Creating course successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(CreateCourseActivity.this, "Error while creating new course", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

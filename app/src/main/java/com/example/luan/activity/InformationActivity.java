package com.example.luan.activity;

/**
 * Created by Luan on 3/30/2016.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

import entity.DataHolder;
import entity.User;
import support.Support;
public class InformationActivity extends AppCompatActivity {
    EditText firstName, lastName, phoneNumber, age, university;
    TextView email;
    Button update;
    User user;
    String jsonUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        email = (TextView) findViewById(R.id.email);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
        phoneNumber = (EditText) findViewById(R.id.phone);
        age = (EditText) findViewById(R.id.age);
        university = (EditText) findViewById(R.id.university);
        update = (Button) findViewById(R.id.update);

        email.setText(DataHolder.getInstance().getData().getLocal().getEmail());
        firstName.setText(DataHolder.getInstance().getData().getBio().getFirstName());
        lastName.setText(DataHolder.getInstance().getData().getBio().getLastName());
        phoneNumber.setText(DataHolder.getInstance().getData().getBio().getPhoneNumber());
        age.setText(String.valueOf(DataHolder.getInstance().getData().getBio().getAge()));
        university.setText(DataHolder.getInstance().getData().getBio().getUniversity());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Support support = new Support();

                user = new User();
                user.getLocal().setEmail(email.getText().toString());
                user.getBio().setFirstName(firstName.getText().toString());
                user.getBio().setLastName(lastName.getText().toString());
                user.getBio().setUniversity(university.getText().toString());
                // validate age
                try {
                    int num = Integer.parseInt(age.getText().toString());
                    if (num <= 0) throw new NumberFormatException();
                    user.getBio().setAge(num);
                } catch (NumberFormatException e) {
                    showError(age);
                    age.setError("Please enter your age");
                    return;
                }

                // validate phoneNumber
                if(TextUtils.isEmpty(phoneNumber.getText().toString()) || support.isValidPhoneNumber(phoneNumber.getText().toString())){
                    user.getBio().setPhoneNumber(phoneNumber.getText().toString());
                }
                else {
                    showError(phoneNumber);
                    age.setError("Please enter your phone number");
                    return;
                }
                String url = Support.HOST + "mobile/users/" + DataHolder.getInstance().getData().get_id() + "/update";
                Log.e("URL", url);
                UpdateRequest updateRequest = new UpdateRequest();
                updateRequest.execute(url);
            }
        });
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        editText.startAnimation(shake);
    }

    private class UpdateRequest extends AsyncTask<String, Void, Integer> {

        public User u;
        private final ProgressDialog dialog = new ProgressDialog(InformationActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Updating...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected Integer doInBackground(String... urls) {
            try {
                Log.e("URL", urls[0]);
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                String json = gson.toJson(user, type);
                Log.e("Json", json);

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
                while ((line = responseBuffer.readLine()) != null){
                    stringBuilder.append(line);
                }
                String jsonResponse = stringBuilder.toString();

                Log.e("JSON response", jsonResponse);
                Log.e("Response Message", urlConnection.getResponseMessage());
                return urlConnection.getResponseCode();

            }
            catch (Exception e) {

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(InformationActivity.this, "Update successfully!", Toast.LENGTH_LONG).show();
                DataHolder.getInstance().setData(user);
            }
            else {
                Toast.makeText(InformationActivity.this, "Error when update information", Toast.LENGTH_LONG).show();
            }

        }

    }
}

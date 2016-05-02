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

import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import entity.User;
import support.Support;
public class InformationActivity extends AppCompatActivity {
    EditText firstName, lastName, password, confirmPassword, phoneNumber, age;
    TextView email;
    Button update;
    User user;
    String jsonUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        jsonUser =  getIntent().getStringExtra("User");
        Gson gson = new Gson();
        Type  type = new TypeToken<User>(){}.getType();
        user = gson.fromJson(jsonUser, type);

        email = (TextView) findViewById(R.id.email);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
/*        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);*/
        phoneNumber = (EditText) findViewById(R.id.phone);
        age = (EditText) findViewById(R.id.age);
        update = (Button) findViewById(R.id.update);

        email.setText(user.getLocal().getEmail());
        firstName.setText(user.getBio().getFirstName());
        lastName.setText(user.getBio().getLastName());
        phoneNumber.setText(user.getBio().getPhoneNumber());
        age.setText(String.valueOf(user.getBio().getAge()));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a user object
                Support support = new Support();
                User u = new User();

                u.getLocal().setEmail(email.getText().toString());
                u.getLocal().setPassword(u.getLocal().getPassword());
                u.getBio().setFirstName(firstName.getText().toString());
                u.getBio().setLastName(lastName.getText().toString());
                // validate age
                try {
                    int num = Integer.parseInt(age.getText().toString());
                    if (num <= 0) throw new NumberFormatException();
                    u.getBio().setAge(num);
                } catch (NumberFormatException e) {
                    showError(age);
                    age.setError("Please enter your age");
                    return;
                }

                // validate phoneNumber
                if(TextUtils.isEmpty(phoneNumber.getText().toString()) || support.isValidPhoneNumber(phoneNumber.getText().toString())){
                    u.getBio().setPhoneNumber(phoneNumber.getText().toString());
                }
                else {
                    showError(phoneNumber);
                    age.setError("Please enter your phone number");
                    return;
                }
                String url = Support.HOST + "mobile/user";
                UpdateRequest updateRequest = new UpdateRequest();
                updateRequest.u = u;
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
                String json = gson.toJson(u, type);
                Log.e("Json", json);

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();
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
            }
            else {
                Toast.makeText(InformationActivity.this, "Error when update information", Toast.LENGTH_LONG).show();
            }

        }

    }
}

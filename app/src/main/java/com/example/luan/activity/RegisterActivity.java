package com.example.luan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.DataHolder;
import entity.Local;
import entity.User;
import support.Support;

public class RegisterActivity extends AppCompatActivity {
    EditText password, confirmPassword, email, firstName, lastName;
    Button signup;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        email = (EditText) findViewById(R.id.email);
        signup = (Button) findViewById(R.id.signup);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check empty field or blank space
                Support support = new Support();
                if(!support.isValidEmail(email.getText().toString())){
                    showError(email);
                    email.setError("Please enter your email");
                    return;
                }
                else if(password.getText().toString().isEmpty() || password.getText().toString().contains(" ")){
                    showError(password);
                    password.setError("Please enter your password");
                    return;
                }
                else if(confirmPassword.getText().toString().isEmpty() || confirmPassword.getText().toString().contains(" ")){
                    showError(confirmPassword);
                    confirmPassword.setError("Please confirm your password");
                    return;
                }                

                // Check password and confirm password fields have the same value
                if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password not match");
                    return;
                }


                // Create a user object
                user = new User();
                user.getLocal().setPassword(password.getText().toString());
                user.getLocal().setEmail(email.getText().toString());

                String url = Support.HOST + "signup";
                new RegisterRequest().execute(url);
            }
        });
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        editText.startAnimation(shake);
    }
    
    private class RegisterRequest extends AsyncTask<String, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Sign up...");
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
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                Type type = new TypeToken<Local>(){}.getType();
                String json = gson.toJson(user.getLocal(), type);
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
                DataHolder.getInstance().setData(user);
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Email already in use. Please use another email!", Toast.LENGTH_LONG).show();
            }

        }
    }
}

package fragment;

/**
 * Created by Admin on 5/10/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuyo.hikaru.activity.R;
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


public class ProfileFragment extends Fragment {
    EditText firstName, lastName, phoneNumber, age, university;
    Button update;
    User userFromActivity;
    User user;
    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String jsonUser = getArguments().getString("User");
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        userFromActivity = gson.fromJson(jsonUser, type);

        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        firstName = (EditText) view.findViewById(R.id.firstname);
        lastName = (EditText) view.findViewById(R.id.lastname);
        phoneNumber = (EditText) view.findViewById(R.id.phone);
        age = (EditText) view.findViewById(R.id.age);
        university = (EditText) view.findViewById(R.id.university);
        update = (Button) view.findViewById(R.id.update);


        //Check if user view another user's profile
        if(!userFromActivity.get_id().equals(DataHolder.getInstance().getUser().get_id())) {
            update.setVisibility(View.GONE);
            firstName.setEnabled(false);
            lastName.setEnabled(false);
            phoneNumber.setEnabled(false);
            age.setEnabled(false);
            university.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstName.setText(userFromActivity.getBio().getFirstName());
        lastName.setText(userFromActivity.getBio().getLastName());
        phoneNumber.setText(userFromActivity.getBio().getPhoneNumber());
        age.setText(String.valueOf(userFromActivity.getBio().getAge()));
        university.setText(userFromActivity.getBio().getUniversity());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Support support = new Support();

                user = new User();
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
                if (TextUtils.isEmpty(phoneNumber.getText().toString()) || support.isValidPhoneNumber(phoneNumber.getText().toString())) {
                    user.getBio().setPhoneNumber(phoneNumber.getText().toString());
                } else {
                    showError(phoneNumber);
                    age.setError("Please enter your phone number");
                    return;
                }
                String url = Support.HOST + "users/" + userFromActivity.get_id() + "/update";
                Log.e("URL", url);
                UpdateRequest updateRequest = new UpdateRequest();
                updateRequest.execute(url);
            }
        });
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        editText.startAnimation(shake);
    }

    private class UpdateRequest extends AsyncTask<String, Void, Integer> {

        public User u;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

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
                Type type = new TypeToken<User>() {
                }.getType();
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
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String jsonResponse = stringBuilder.toString();

                Log.e("JSON response", jsonResponse);
                Log.e("Response Message", urlConnection.getResponseMessage());
                String jsonUser = getArguments().getString("User");
                Gson gsonResponse = new Gson();
                Type typeResponse = new TypeToken<User>() {
                }.getType();
                user = gson.fromJson(jsonResponse, type);
                return urlConnection.getResponseCode();

            } catch (Exception e) {

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getActivity(), "Update successfully!", Toast.LENGTH_LONG).show();
                DataHolder.getInstance().setUser(user);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("USER_CHANGE");
                getActivity().sendBroadcast(broadcastIntent);
            } else {
                Toast.makeText(getActivity(), "Error when update information", Toast.LENGTH_LONG).show();
            }

        }

    }

}

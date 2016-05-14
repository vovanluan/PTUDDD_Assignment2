package com.example.luan.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.DataHolder;
import entity.Follow;
import entity.User;
import fragment.ContactFragment;
import fragment.UserCourseFragment;
import fragment.ProfileFragment;
import adapter.ViewPagerAdapter;
import support.Support;

/**
 * Created by Admin on 5/10/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ProfileFragment profileFragment;
    private ContactFragment contactFragment;
    private UserCourseFragment userCourseFragment;
    private TextView name, followers, cards;
    private ImageView followBtn;
    private User user;
    private Follow follow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.name);
        followers = (TextView) findViewById(R.id.followers);
        cards = (TextView) findViewById(R.id.cards);
        followBtn = (ImageView) findViewById(R.id.followBtn);
        // transfer user information to fragment
        String jsonUser = getIntent().getStringExtra("User");
        Bundle bundle = new Bundle();
        bundle.putString("User", jsonUser);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<User>() {
        }.getType();
        user = gson.fromJson(jsonUser, type);
        Log.e("USER", jsonUser);

        name.setText(user.getBio().getFirstName());
        followers.setText(String.valueOf(user.getFollowers().size()));
        cards.setText(String.valueOf(user.getCards().size()));
        // create navigation tab and viewpager
        profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        contactFragment = new ContactFragment();
        contactFragment.setArguments(bundle);
        userCourseFragment = new UserCourseFragment();
        userCourseFragment.setArguments(bundle);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(profileFragment, "Profile");
        adapter.addFragment(contactFragment, "Contact");
        adapter.addFragment(userCourseFragment, "Course");
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // initialize following button: check if user followed this opened profile
        if(DataHolder.getInstance().getUser().getFollowing().contains(user.get_id())) {
            followBtn.setImageResource(R.drawable.ic_star_followed);
        }
        else {
            followBtn.setImageResource(R.drawable.ic_star_border);
        }
        followBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!DataHolder.getInstance().getUser().getFollowing().contains(user.get_id())) {
                    follow = new Follow();
                    follow.setStudent(DataHolder.getInstance().getUser().get_id());
                    follow.setTeacher(user.get_id());
                    String followingRequestURL = Support.HOST + "users/addFollower";
                    new FollowingRequest().execute(followingRequestURL);
                }
                else {
                    follow = new Follow();
                    follow.setStudent(DataHolder.getInstance().getUser().get_id());
                    follow.setTeacher(user.get_id());
                    String followingRequestURL = Support.HOST + "users/removeFollower";
                    new RemoveFollowingRequest().execute(followingRequestURL);
                }
            }
        });
    }

    private class FollowingRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Following...");
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
                urlConnection.setRequestMethod("PUT");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Follow>() {
                }.getType();
                String json = gson.toJson(follow, type);
                Log.e("Follow", json);

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
                Log.e("Message", jsonResponse);
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
                    followBtn.setImageResource(R.drawable.ic_star_followed);
                    DataHolder.getInstance().getUser().getFollowing().add(user.get_id());
                    Toast.makeText(ProfileActivity.this, "Followed " + user.getBio().getFirstName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error while following", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
    private class RemoveFollowingRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Remove follow...");
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
                urlConnection.setRequestMethod("PUT");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Follow>() {
                }.getType();
                String json = gson.toJson(follow, type);
                Log.e("Follow", json);

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
                Log.e("Message", jsonResponse);
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
                    followBtn.setImageResource(R.drawable.ic_star_border);
                    DataHolder.getInstance().getUser().getFollowing().remove(user.get_id());
                    Toast.makeText(ProfileActivity.this, "Remove follow " + user.getBio().getFirstName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error while remove follow", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
}

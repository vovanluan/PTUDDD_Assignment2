package com.example.luan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Random;

import entity.Course;
import entity.DataHolder;
import entity.Local;
import entity.Notification;
import entity.User;
import fragment.ReviewDialogFragment;
import support.Support;

/**
 * Created by Admin on 5/11/2016.
 */
public class CourseActivity extends AppCompatActivity{
    private Course course;
    public TextView star;
    private TextView title, creator, people, language, location, time, description, upvote;
    private Button pairUpBtn, reviewBtn, upVoteBtn;
    private ImageView courseImage, upvoteIcon, starIcon, peopleIcon;
    private Notification notification;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        final String jsonCourse = getIntent().getStringExtra("course");
        Log.e("Course:", jsonCourse);
        Gson gson = new Gson();
        Type type = new TypeToken<Course>() {
        }.getType();
        course = gson.fromJson(jsonCourse, type);

        courseImage = (ImageView) findViewById(R.id.courseImage);
        title = (TextView) findViewById(R.id.title);
        creator = (TextView) findViewById(R.id.creator);
        description = (TextView) findViewById(R.id.description);
        time = (TextView) findViewById(R.id.time);
        star = (TextView) findViewById(R.id.star);
        upvote = (TextView) findViewById(R.id.upvote);
        pairUpBtn = (Button)  findViewById(R.id.pairUpBtn);
        reviewBtn = (Button)  findViewById(R.id.reviewBtn);
        upVoteBtn = (Button)  findViewById(R.id.upvoteBtn);
        upvoteIcon = (ImageView) findViewById(R.id.upvoteIcon);
        starIcon = (ImageView) findViewById(R.id.starIcon);
        people = (TextView) findViewById(R.id.people);
        peopleIcon = (ImageView) findViewById(R.id.peopleIcon);

        //initialize upvote icon and button
        if(DataHolder.getInstance().getUser().getUpvoted().contains(course.get_id())) {
            upVoteBtn.setText("Remove vote");
            upvoteIcon.setImageResource(R.drawable.ic_action_liked);
        }
        else {
            upVoteBtn.setText("Upvote");
            upvoteIcon.setImageResource(R.drawable.ic_action_like);
        }

        if(course.getStudents().contains(DataHolder.getInstance().getUser().get_id())) {
            peopleIcon.setImageResource(R.drawable.ic_big_group);
        }
        else {
            peopleIcon.setImageResource(R.drawable.ic_group_add);
        }
/*        if(DataHolder.getInstance().getUser().getReviews().contains(course.get_id())) {
            starIcon.setImageResource(R.drawable.ic_action_reviewed);
        }
        else {
            starIcon.setImageResource(R.drawable.ic_big_star);
        }*/

        title.setText(course.getTitle());
        creator.setText(DataHolder.getInstance().getUserById(course.getCreated_by()).getBio().getFirstName());
        description.setText(course.getDescription());
        star.setText(String.valueOf(course.getRating()));
        time.setText(course.getTime());
        upvote.setText(String.valueOf(course.getUpvotes()));
        people.setText(String.valueOf(course.getStudents().size()));

        Random generator = new Random();
        int random = generator.nextInt(9);
        String color = Support.COLOR[random];
        courseImage.setBackgroundColor(Color.parseColor(color));

        creator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(CourseActivity.this, ProfileActivity.class);
                User user = DataHolder.getInstance().getUserById(course.getCreated_by());
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                String jsonUser = gson.toJson(user, type);
                i.putExtra("User", jsonUser);
                startActivity(i);
            }
        });
        View.OnClickListener pairUpListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user joined this course
                if(course.getStudents().contains(DataHolder.getInstance().getUser().get_id())) {
                    Toast.makeText(CourseActivity.this, "You joined this course", Toast.LENGTH_SHORT).show();
                    return;
                }
                User teacher = DataHolder.getInstance().getUserById(course.getCreated_by());
                String studentName = DataHolder.getInstance().getUser().getBio().getFirstName() + " " + DataHolder.getInstance().getUser().getBio().getLastName();
                String teacherName = teacher.getBio().getFirstName() + " " + teacher.getBio().getLastName();
                notification = new Notification();
                notification.setCreated_by(DataHolder.getInstance().getUser().get_id());
                notification.setFor_card(course.get_id());
                notification.setTo(course.getCreated_by());
                notification.setStudentName(studentName);
                notification.setTeacherName(teacherName);
                notification.setDescription(studentName + " wants to join your " + course.getTitle() + " class");
                notification.setCardName(course.getTitle());

                new PairUpRequest().execute(Support.HOST + "users/pairup");
            }
        };

        pairUpBtn.setOnClickListener(pairUpListener);
        peopleIcon.setOnClickListener(pairUpListener);

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.getInstance();
                reviewDialogFragment.course = CourseActivity.this.course;
                reviewDialogFragment.show(getSupportFragmentManager(), "ReviewDialog");
            }
        });
        View.OnClickListener mUpvoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DataHolder.getInstance().getUser().getUpvoted().contains(course.get_id())) {
                    String upVoteURL = Support.HOST + "cards/" + course.get_id() + "/" + DataHolder.getInstance().getUser().get_id() + "/upvote";
                    Log.e("UpvoteURL", upVoteURL);
                    new UpvoteRequest().execute(upVoteURL);
                }
                else {
                    String removeVoteURL = Support.HOST + "cards/" + course.get_id() + "/" + DataHolder.getInstance().getUser().get_id() + "/removeupvote";
                    Log.e("removeVoteURL", removeVoteURL);
                    new RemoveVoteRequest().execute(removeVoteURL);
                }
            }
        };
        upvoteIcon.setOnClickListener(mUpvoteClickListener);

        upVoteBtn.setOnClickListener(mUpvoteClickListener);
    }

    private class UpvoteRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(CourseActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Upvoting...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("PUT");
                urlConnection.connect();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Upvote", jsonResponse);
                Log.e("CODE", String.valueOf(urlConnection.getResponseCode()));
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
                    Type type = new TypeToken<Course>() {
                    }.getType();
                    course = gson.fromJson(jsonResponse, type);
                    upvote.setText(String.valueOf(course.getUpvotes()));
                    //Remove old course from course list
                    DataHolder.getInstance().removeCourse(course.get_id());
                    DataHolder.getInstance().getUser().getUpvoted().add(course.get_id());
                    DataHolder.getInstance().getUserById(DataHolder.getInstance().getUser().get_id()).getUpvoted().add(course.get_id());
                    //Update new course
                    DataHolder.getInstance().getCourseList().add(course);
                    Toast.makeText(CourseActivity.this, "Upvoted", Toast.LENGTH_SHORT).show();

                    //Broadcast update card list
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("UPDATE_CARD_LIST");
                    sendBroadcast(broadcastIntent);

                    upVoteBtn.setText("Remove vote");
                    upvoteIcon.setImageResource(R.drawable.ic_action_liked);
                }
                else if (responseCode == 400){

                }
                else {
                    Toast.makeText(CourseActivity.this, "Error while upvote", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }

    private class RemoveVoteRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(CourseActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Upvoting...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("PUT");
                urlConnection.connect();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("RemoveVote", jsonResponse);
                Log.e("CODE", String.valueOf(urlConnection.getResponseCode()));
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
                    Type type = new TypeToken<Course>() {
                    }.getType();
                    course = gson.fromJson(jsonResponse, type);
                    upvote.setText(String.valueOf(course.getUpvotes()));
                    //Remove old course from course list
                    DataHolder.getInstance().removeCourse(course.get_id());
                    DataHolder.getInstance().getUser().getUpvoted().remove(course.get_id());
                    DataHolder.getInstance().getUserById(DataHolder.getInstance().getUser().get_id()).getUpvoted().remove(course.get_id());
                    //Update new course
                    DataHolder.getInstance().getCourseList().add(course);

                    //Broadcast update course
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("UPDATE_CARD_LIST");
                    sendBroadcast(broadcastIntent);

                    Toast.makeText(CourseActivity.this, "Remove vote", Toast.LENGTH_SHORT).show();
                    upVoteBtn.setText("Upvote");
                    upvoteIcon.setImageResource(R.drawable.ic_action_like);
                }
                else if (responseCode == 400){

                }
                else {
                    Toast.makeText(CourseActivity.this, "Error while remove vote", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
    private class PairUpRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(CourseActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Pairing up...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                Type type = new TypeToken<Notification>() {
                }.getType();
                String json = gson.toJson(notification, type);
                Log.e("Notification", json);

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

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
                    DataHolder.getInstance().getCourseById(course.get_id()).getStudents().add(DataHolder.getInstance().getUser().get_id());
                    DataHolder.getInstance().getUser().getTeachers().add(course.getCreated_by());
                    DataHolder.getInstance().getUserById(DataHolder.getInstance().getUser().get_id()).getTeachers().add(course.getCreated_by());
                    course.getStudents().add(DataHolder.getInstance().getUser().get_id());
                    people.setText(String.valueOf(course.getStudents().size()));
                    peopleIcon.setImageResource(R.drawable.ic_big_group);
                    Toast.makeText(CourseActivity.this, "Awesome!!! You've joined this course!", Toast.LENGTH_SHORT).show();

                    //Broadcast update course
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("UPDATE_CARD_LIST");
                    sendBroadcast(broadcastIntent);
                }
                else if(responseCode == 400){
                    Toast.makeText(CourseActivity.this, "You joined this course already", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CourseActivity.this, "Error while pair up", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
}

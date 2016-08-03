package com.yuyo.hikaru.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.internal.widget.ActivityChooserModel;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.Course;
import entity.DataHolder;
import entity.Notification;
import entity.User;
import fragment.ReviewDialogFragment;
import mehdi.sakout.fancybuttons.FancyButton;
import support.Support;

public class CourseActivity extends AppCompatActivity {

    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView imgHeader;
    private Course course;

    private ImageView iconLanguage, iconTime, iconLocation, iconFlag;

    //private SimpleRatingBar ratingBar;

    public TextView star;
    private TextView title, creator, people, language, location, time, description, upvote;
    private Button pairUpBtn, reviewBtn, upVoteBtn;
    private ImageView upvoteIcon, starIcon, peopleIcon;
    private Notification notification;


    private Palette palette;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        initInstances();
    }

    private void initInstances() {

        getCourseIntent();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(course.getTitle());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        iconLanguage = (ImageView) findViewById(R.id.languageIcon);
        iconTime = (ImageView) findViewById(R.id.timeIcon);
        iconLocation = (ImageView) findViewById(R.id.locationIcon);
        iconFlag = (ImageView) findViewById(R.id.languageFlag);

        // previous code
        title = (TextView) findViewById(R.id.title);
        creator = (TextView) findViewById(R.id.creator);
        description = (TextView) findViewById(R.id.description);
        time = (TextView) findViewById(R.id.time);
        star = (TextView) findViewById(R.id.star);
        upvote = (TextView) findViewById(R.id.upvote);
        language = (TextView) findViewById(R.id.language);
        pairUpBtn = (Button)  findViewById(R.id.pairUpBtn);
        reviewBtn = (Button)  findViewById(R.id.reviewBtn);
        upVoteBtn = (Button)  findViewById(R.id.upvoteBtn);
        upvoteIcon = (ImageView) findViewById(R.id.upvoteIcon);
        starIcon = (ImageView) findViewById(R.id.starIcon);
        people = (TextView) findViewById(R.id.people);
        peopleIcon = (ImageView) findViewById(R.id.peopleIcon);
        location = (TextView) findViewById(R.id.location);

        //ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);

        reviewBtn = (Button) findViewById(R.id.reviewBtn);

        setupHeaderImg();

        bindColor();

        bindDataFromCourse();

        addListennerEvent();


    }

    private void addListennerEvent() {
        // check if this user is watching his course
        // hide all button: pair up, review, upvote
        if (course.getCreated_by().equals(DataHolder.getInstance().getUser().get_id())) {
            pairUpBtn.setVisibility(View.INVISIBLE);
            reviewBtn.setVisibility(View.INVISIBLE);
            upVoteBtn.setVisibility(View.INVISIBLE);
            upvoteIcon.setOnClickListener(null);
        }
        else {
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
        //initialize upvote icon and button\
        if(DataHolder.getInstance().getUser().getUpvoted().contains(course.get_id())) {
            upVoteBtn.setText("Remove vote");
            upvoteIcon.setImageResource(R.drawable.ic_action_liked);
        }
        else {
            upVoteBtn.setText("Upvote");
            upvoteIcon.setImageResource(R.drawable.ic_action_like);
        }

        /*if(course.getStudents().contains(DataHolder.getInstance().getUser().get_id())) {
            peopleIcon.setImageResource(R.drawable.ic_big_group);
        }
        else {
            peopleIcon.setImageResource(R.drawable.ic_group_add);
        }*/

        creator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                creatorBtnHandler();
            }
        });

        View.OnClickListener pairUpListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairupBtnHandler();

            }
        };
        pairUpBtn.setOnClickListener(pairUpListener);
        peopleIcon.setOnClickListener(pairUpListener);

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewBtnHandler();
            }
        });
    }

    private void reviewBtnHandler() {
        ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.getInstance();
        reviewDialogFragment.course = CourseActivity.this.course;
        reviewDialogFragment.show(getSupportFragmentManager(), "ReviewDialog");
    }

    private void pairupBtnHandler() {
        // Check if user joined this course
        if(course.getStudents().contains(DataHolder.getInstance().getUser().get_id())) {
            Toast.makeText(CourseActivity.this, "You already joined this course", Toast.LENGTH_SHORT).show();
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

    private void creatorBtnHandler() {
        Intent i = new Intent(CourseActivity.this, ProfileActivity.class);
        User user = DataHolder.getInstance().getUserById(course.getCreated_by());
        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        String jsonUser = gson.toJson(user, type);
        i.putExtra("User", jsonUser);
        startActivity(i);
    }

    private void bindDataFromCourse() {
        title.setText(course.getTitle());
        creator.setText(DataHolder.getInstance().getUserById(course.getCreated_by()).getBio().getFirstName() + " " +
                DataHolder.getInstance().getUserById(course.getCreated_by()).getBio().getLastName());
        description.setText(course.getDescription());
        star.setText(String.valueOf(course.getRating()));
        location.setText(course.getPlace());

        DateTime timeCourseStart = new DateTime(course.getTime());
        time.setText(timeCourseStart.getDayOfMonth() + "/" + timeCourseStart.getMonthOfYear() + "/" + timeCourseStart.getYear() + " at " + timeCourseStart.getHourOfDay() + ":" + timeCourseStart.getMinuteOfHour());
        upvote.setText(String.valueOf(course.getUpvotes()));
        people.setText(String.valueOf(course.getStudents().size()));
        language.setText(course.getCategory());
//        ratingBar.setRating(course.getRating());

        // Set language flag
        int flag = Support.getCategoryFlag(course.getCategory());
        iconFlag.setImageResource(flag);
    }

    private void setupHeaderImg() {
        imgHeader = (ImageView) findViewById(R.id.imgHeader);
        int headerPic = Support.getHeaderPic(course.getCategory());
        imgHeader.setImageResource(headerPic);

        // Extract color from header
        Bitmap iconBitmap = BitmapFactory.decodeResource(this.getResources(),
                headerPic);
        if (iconBitmap != null && !iconBitmap.isRecycled()) {
            palette = Palette.generate (iconBitmap);
        }
    }

    private void bindColor() {
        int defaultColor = 0x000000;
        int myColor = palette.getVibrantColor(defaultColor);
        int myColorDark = palette.getDarkVibrantColor(defaultColor);
        iconLanguage.setColorFilter(myColor);
        iconTime.setColorFilter(myColor);
        iconLocation.setColorFilter(myColor);
        title.setTextColor(myColor);

        pairUpBtn.setBackgroundColor(Color.parseColor("#2CC88F"));
        upVoteBtn.setBackgroundColor(Color.parseColor("#2196F3"));
    }

    private void getCourseIntent() {
        final String jsonCourse = getIntent().getStringExtra("course");
        Log.e("Course:", jsonCourse);
        Gson gson = new Gson();
        Type type = new TypeToken<Course>() {
        }.getType();
        course = gson.fromJson(jsonCourse, type);
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

package com.example.luan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.Random;

import entity.Course;
import entity.User;
import support.Support;

/**
 * Created by Admin on 5/11/2016.
 */
public class CourseActivity extends AppCompatActivity{
    private Course course;
    private TextView title, creator, star, people, language, location, time, description;
    private ImageView courseImage;
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
        star = (TextView) findViewById(R.id.star);
        //TextView vote = (TextView) findViewById(R.id.vote);

        title.setText(course.getTitle());
        creator.setText(course.getCreate_by().getBio().getFirstName());
        description.setText(course.getDescription());
        star.setText(String.valueOf(course.getRating()));
        //vote.setText(String.valueOf(course.getUpvotes()));

        Random generator = new Random();
        int random = generator.nextInt(9);
        String color = Support.COLOR[random];
        courseImage.setBackgroundColor(Color.parseColor(color));

        creator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*                Intent i = new Intent(CourseActivity.this, ProfileActivity.class);
                User user = course.getCreate_by();
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                String jsonUser = gson.toJson(user, type);
                i.putExtra("User", jsonUser);
                startActivity(i);*/
            }
        });
    }
}

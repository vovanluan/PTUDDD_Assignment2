package com.example.luan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import Fragment.ContactFragment;
import Fragment.CourseFragment;
import Fragment.ProfileFragment;
import adapter.ViewPagerAdapter;

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
    private CourseFragment courseFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // create navigation tab and viewpager
        profileFragment = new ProfileFragment();
        contactFragment = new ContactFragment();
        courseFragment = new CourseFragment();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(profileFragment, "Profile");
        adapter.addFragment(contactFragment, "Contact");
        adapter.addFragment(courseFragment, "Course");
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*viewPager.setOnTouchListener(new View.OnTouchListener() {

            int dragthreshold = 30;
            int downX;
            int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int distanceX = Math.abs((int) event.getRawX() - downX);
                        int distanceY = Math.abs((int) event.getRawY() - downY);

                        if (distanceY > distanceX && distanceY > dragthreshold) {
                            viewPager.getParent().requestDisallowInterceptTouchEvent(false);
                            scrollView.getParent().requestDisallowInterceptTouchEvent(true);
                        } else if (distanceX > distanceY && distanceX > dragthreshold) {
                            viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                            scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                        viewPager.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });*/
    }
}

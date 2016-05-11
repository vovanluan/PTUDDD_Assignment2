package com.example.luan.activity;

import android.os.Bundle;
import android.renderscript.Type;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import entity.User;
import fragment.ContactFragment;
import fragment.UserCourseFragment;
import fragment.ProfileFragment;
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
    private UserCourseFragment userCourseFragment;
    private TextView name;
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.name);
        // transfer user information to fragment
        String jsonUser = getIntent().getStringExtra("User");
        Bundle bundle = new Bundle();
        bundle.putString("User", jsonUser);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<User>() {
        }.getType();
        user = gson.fromJson(jsonUser, type);

        name.setText(user.getBio().getFirstName());
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

    }
}

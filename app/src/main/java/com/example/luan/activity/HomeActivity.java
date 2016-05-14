package com.example.luan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import entity.Course;
import entity.Notification;
import entity.User;
import fragment.CourseFragment;
import fragment.UserFragment;
import fragment.NotificationFragment;
import fragment.FeedbackDialogFragment;
import adapter.ViewPagerAdapter;
import entity.DataHolder;
import support.BackgroundRequest;
import support.Support;

public class HomeActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private BroadcastReceiver logOutBroadcastReceiver, userChangeBroadcastReceiver, gotUserListBroadCastReceiver, gotCardListBroadCastReceiver, updateCardListBroadcastReceiver;
    private IntentFilter gotUserListIntentFilter, gotCardListIntentFilter, logOutIntentFilter, updateCardListIntentFilter, userChangeIntentFilter;
    private AlertDialog.Builder logoutDialog;
    private CourseFragment courseFragment;
    private UserFragment userFragment;
    private NotificationFragment notificationFragment;
    private FloatingActionButton floatingActionButton;
    private BackgroundRequest backgroundRequest;
    private int[] tabIcons = {
            R.drawable.ic_class,
            R.drawable.ic_user_list,
            R.drawable.ic_notifications
    };

    @Override
    protected void onDestroy() {
        try {
            if (gotUserListBroadCastReceiver != null) {
                unregisterReceiver(gotUserListBroadCastReceiver);
                gotUserListBroadCastReceiver = null;
            }
            if (gotCardListBroadCastReceiver != null) {
                unregisterReceiver(gotCardListBroadCastReceiver);
                gotCardListBroadCastReceiver = null;
            }
            if (userChangeBroadcastReceiver != null) {
                unregisterReceiver(userChangeBroadcastReceiver);
                userChangeBroadcastReceiver = null;
            }
            if (logOutBroadcastReceiver != null) {
                unregisterReceiver(logOutBroadcastReceiver);
                logOutBroadcastReceiver = null;
            }
            if (updateCardListBroadcastReceiver != null) {
                unregisterReceiver(updateCardListBroadcastReceiver);
                updateCardListBroadcastReceiver = null;
            }
        }
        catch (Exception e) {
            throw e;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        PreferenceManager.setDefaultValues(this, R.xml.preferences_fragment, false);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateCourseActivity.class);
                startActivity(intent);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backgroundRequest = new BackgroundRequest(this);
        backgroundRequest.getUserListRequest();

        // get broadcast that get user list finished
        gotUserListBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                backgroundRequest.getCardListRequest();
                userFragment.adapter.setListUser(DataHolder.getInstance().getUserList());
            }
        };
        gotUserListIntentFilter = new IntentFilter();
        gotUserListIntentFilter.addAction("GOT_USER_LIST");
        registerReceiver(gotUserListBroadCastReceiver, gotUserListIntentFilter);

        // get broadcast that get card list finished
        gotCardListBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                courseFragment.adapter.setListCard(DataHolder.getInstance().getCourseList());
            }
        };

        gotCardListIntentFilter = new IntentFilter();
        gotCardListIntentFilter.addAction("GOT_CARD_LIST");
        registerReceiver(gotCardListBroadCastReceiver, gotCardListIntentFilter);
        // create navigation tab and viewpager
        courseFragment = new CourseFragment();
        userFragment = new UserFragment();
        notificationFragment = new NotificationFragment();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(courseFragment, "");
        adapter.addFragment(userFragment, "");
        adapter.addFragment(notificationFragment, "");
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // setup tab icons
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        final TextView name = (TextView) findViewById(R.id.username);
        final TextView email = (TextView) findViewById(R.id.email);
        logoutDialog = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Support.HOST + "logout";
                        new LogoutRequest().execute(url);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        // parse data to header
        name.setText(DataHolder.getInstance().getUser().getBio().getFirstName() + " " + DataHolder.getInstance().getUser().getBio().getLastName());
        email.setText(DataHolder.getInstance().getUser().getLocal().getEmail());

        // receive broadcast on secured activity
        logOutBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //start login activity
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                DataHolder.getInstance().setUser(new User());
                DataHolder.getInstance().setUserList(new ArrayList<User>());
                DataHolder.getInstance().setCourseList(new ArrayList<Course>());
                DataHolder.getInstance().setNewNotifications(new ArrayList<Notification>());
                DataHolder.getInstance().setOldNotifications(new ArrayList<Notification>());
                finish();
            }
        };
        logOutIntentFilter = new IntentFilter();
        logOutIntentFilter.addAction("ACTION_LOGOUT");
        registerReceiver(logOutBroadcastReceiver, logOutIntentFilter);

        updateCardListBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HomeActivity.this.courseFragment.adapter.setListCard(DataHolder.getInstance().getCourseList());
            }
        };
        updateCardListIntentFilter = new IntentFilter();
        updateCardListIntentFilter.addAction("UPDATE_CARD_LIST");
        registerReceiver(updateCardListBroadcastReceiver, updateCardListIntentFilter);

        // receive broadcast on user change
        userChangeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update text view
                Log.e("Update", DataHolder.getInstance().getUser().getBio().getFirstName());
                name.setText(DataHolder.getInstance().getUser().getBio().getFirstName() + " " + DataHolder.getInstance().getUser().getBio().getLastName());
                email.setText(DataHolder.getInstance().getUser().getLocal().getEmail());
            }
        };
        userChangeIntentFilter = new IntentFilter();
        userChangeIntentFilter.addAction("USER_CHANGE");
        registerReceiver(userChangeBroadcastReceiver, userChangeIntentFilter);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                        // Send user information
                        Gson gson = new Gson();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        String jsonUser = gson.toJson(DataHolder.getInstance().getUser(), type);
                        i.putExtra("User", jsonUser);
                        startActivity(i);
                        return true;
                    case R.id.settings:
                        Intent settingIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(settingIntent);
                        return true;
                    case R.id.feedback:
                        FeedbackDialogFragment feedbackDialogFragment = FeedbackDialogFragment.getInstance();
                        feedbackDialogFragment.show(getSupportFragmentManager(), "feedback");
                        return true;
                    case R.id.aboutUs:
                        Intent aboutUsIntent = new Intent(HomeActivity.this, AboutUsActivity.class);
                        startActivity(aboutUsIntent);
                        return true;
                    case R.id.logOut:
                        logoutDialog.show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    @Override
    public void onBackPressed() {
        // Minimize app after pressing back button
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        //logoutDialog.show();
    }

    private class LogoutRequest extends AsyncTask<String, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Log out...");
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
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                Log.e("ResponseCode", String.valueOf(responseCode));
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Send broadcast to every activity
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("ACTION_LOGOUT");
                    sendBroadcast(broadcastIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Can not log out!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }

}

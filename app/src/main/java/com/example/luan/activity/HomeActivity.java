package com.example.luan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Fragment.CardFragment;
import Fragment.StudentFragment;
import adapter.UserAdapter;
import adapter.ViewPagerAdapter;
import entity.Card;
import entity.DataHolder;
import entity.User;
import support.Support;

public class HomeActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    TextView name, email;
    User user;
    String jsonUser;
    ArrayList<Card> cardList;
    String[] SPINNERLIST = {"A student", "A teacher"};
    private BroadcastReceiver logOutBroadcastReceiver, userChangeBroadcastReceiver;
    private AlertDialog.Builder logoutDialog;
    private CardFragment cardFragment;
    private StudentFragment studentFragment;
    private int[] tabIcons = {
            R.drawable.card,
            R.drawable.student,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get card list from server
        String getCardListURL = Support.HOST +"mobile/cards";
        new GetCardRequest().execute(getCardListURL);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create navigation tab and viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        cardFragment = new CardFragment();
        studentFragment = new StudentFragment();
        adapter.addFragment(cardFragment, "Card");
        adapter.addFragment(studentFragment, "Student");
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        final TextView name = (TextView) findViewById(R.id.username);
        final TextView email = (TextView) findViewById(R.id.email);
        logoutDialog = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Support.HOST + "mobile/logout";
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
        name.setText(DataHolder.getInstance().getData().getBio().getFirstName() + " " + DataHolder.getInstance().getData().getBio().getLastName());
        email.setText(DataHolder.getInstance().getData().getLocal().getEmail());

        // receive broadcast on secured activity
        logOutBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //start login activity
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ACTION_LOGOUT");
        registerReceiver(logOutBroadcastReceiver, intentFilter);

        // receive broadcast on user change
        userChangeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update text view
                Log.e("Update", DataHolder.getInstance().getData().getBio().getFirstName());
                name.setText(DataHolder.getInstance().getData().getBio().getFirstName() + " " + DataHolder.getInstance().getData().getBio().getLastName());
                email.setText(DataHolder.getInstance().getData().getLocal().getEmail());
            }
        };
        IntentFilter userChangeIntentFilter = new IntentFilter();
        userChangeIntentFilter.addAction("USER_CHANGE");
        registerReceiver(userChangeBroadcastReceiver, userChangeIntentFilter);

/*        // create spinner for choosing user type

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.user_type_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);*/

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.teacher:
                        Toast.makeText(getApplicationContext(),"Teacher Selected",Toast.LENGTH_SHORT).show();
/*                        ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();*/
                        return true;
                    case R.id.student:
                        Toast.makeText(getApplicationContext(),"Student Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.profile:
                        Intent i = new Intent(HomeActivity.this, InformationActivity.class);
                        //i.putExtra("User", jsonUser);
                        startActivity(i);
                        return true;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(),"Setttings Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.aboutUs:
                        Toast.makeText(getApplicationContext(),"About us Selected",Toast.LENGTH_SHORT).show();
                        Log.e("USER: ", DataHolder.getInstance().getData().getBio().getLastName());
                        return true;
                    case R.id.logOut:
                        logoutDialog.show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CardFragment(), "Card");
        adapter.addFragment(new StudentFragment(), "Student");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("CARD");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_card, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("STUDENT");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_student, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
    @Override
    protected void onStop()
    {
        //unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        private String jsonResponse;
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
                }
                else {
                    Toast.makeText(getApplicationContext(),"Can not log out!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
    private class GetCardRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Load cards...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // Step 2: wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                // Step 3: Arriving JSON fragments are concatenate into a StringBuilder
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null){
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Json", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
                return urlConnection.getResponseCode();

            }
            catch (Exception e) {

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
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type  type = new TypeToken<ArrayList<Card>>(){}.getType();
                    cardList = gson.fromJson(jsonResponse, type);
                    Log.e("Size", String.valueOf(cardList.size()));
                    Log.e("Card list:", jsonResponse);
                    Bundle cardsBundle = new Bundle();
                    cardsBundle.putString("cards", jsonResponse);
                    cardFragment.setArguments(cardsBundle);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error while getting card list", Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {

            }
        }
    }
}

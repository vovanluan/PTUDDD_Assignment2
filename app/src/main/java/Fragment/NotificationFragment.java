package fragment;

/**
 * Created by Luan on 5/2/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.luan.activity.ProfileActivity;
import com.example.luan.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.PreferenceChangeListener;

import adapter.NotificationAdapter;
import adapter.UserAdapter;
import entity.DataHolder;
import entity.Notification;
import entity.User;
import support.Support;

public class NotificationFragment extends Fragment implements AdapterView.OnItemClickListener{
    ListView myListView;
    private int INTERVAL = 10 * 1000;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener = null;
    public NotificationAdapter adapter;

    public NotificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize adapter
        adapter = new NotificationAdapter(getActivity(), R.layout.user_fragment, DataHolder.getInstance().getNewNotifications());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Log.e("Get notification list", "10s");
                new GetNotificationListRequest().execute(Support.HOST + "users/" + DataHolder.getInstance().getUser().get_id() + "/notification");
            }

        }, 0, INTERVAL);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // listener implementation
                if (key.equals("notification")) {
                    boolean turnOn = prefs.getBoolean("notification", true);
                    //TODO: turn on/off notification
                    if (turnOn) {
                        Log.e("TURN ON", "TEST");
                    } else {
                        Log.e("TURN OFF", "TEST");
                    }
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.user_fragment, container, false);
    }

    @Override
    public void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
        super.onDestroy();
    }

/*    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Log.e("Register", "WTF");
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        Log.e("Unregister", "WTF");
        super.onPause();
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize listView
        myListView = (ListView) view.findViewById(R.id.listView);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Handle click an card in fragment
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // retrieve the ListView item
        Notification notification = adapter.getNotificationList().get(position);
        //TODO: handle when user click into a notification
        DataHolder.getInstance().getNewNotifications().remove(notification);
        DataHolder.getInstance().getOldNotifications().add(notification);
        adapter.setNotificationList(DataHolder.getInstance().getNewNotifications());
    }

    private class GetNotificationListRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
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

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Json Notification", jsonResponse);
                Log.e("message Notification", urlConnection.getResponseMessage());
                return urlConnection.getResponseCode();

            } catch (Exception e) {

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Notification>>() {
                    }.getType();
                    ArrayList<Notification> notificationList =  gson.fromJson(jsonResponse, type);
                    for (Notification notification: notificationList) {
                        Log.e("Old contains", String.valueOf(DataHolder.getInstance().getOldNotifications().contains(notification)));
                        if(!DataHolder.getInstance().getOldNotifications().contains(notification)) {
                            DataHolder.getInstance().getNewNotifications().add(notification);
                        }
                    }
                    Log.e("New size", String.valueOf(DataHolder.getInstance().getNewNotifications().size()));
                    Log.e("Old size", String.valueOf(DataHolder.getInstance().getOldNotifications().size()));
                } else {
                }
                String notifications = "[{\"_id\":\"57360ab1411b131100c4e513\",\"updatedAt\":\"2016-05-13T17:21:13.926Z\",\"createdAt\":\"2016-05-13T17:11:13.933Z\",\"cardName\":\"English class\",\"created_by\":\"57360aae411b131100c4e512\",\"description\":\"some first name some last name wants to join class: English class taught by some first name some last name\",\"for_card\":\"57360a94411b131100c4e511\",\"studentName\":\"some first name some last name\",\"teacherName\":\"some first name some last name\",\"to\":\"57360a7b411b131100c4e510\",\"__v\":0,\"status\":0}]";
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Notification>>() {
                }.getType();
                ArrayList<Notification> notificationList =  gson.fromJson(notifications, type);
                Log.e("Notifications size", String.valueOf(notificationList.size()));
                adapter.setNotificationList(notificationList);
            } catch (Exception e) {

            }
        }
    }


}

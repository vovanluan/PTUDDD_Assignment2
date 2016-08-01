package fragment;

/**
 * Created by Luan on 5/2/2016.
 */

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.yuyo.hikaru.activity.ProfileActivity;
import com.yuyo.hikaru.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import adapter.UserAdapter;
import entity.DataHolder;
import entity.User;
import support.BackgroundRequest;
import support.Support;

public class UserFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    public ListView myListView;
    public UserAdapter adapter;
    private int INTERVAL = 10 * 1000;
    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize adapter

        adapter = new UserAdapter(getActivity(), R.layout.user_fragment, DataHolder.getInstance().getUserList());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Log.e("Get notification list", "10s");
                new GetUserListRequest().execute(Support.HOST + "users");
            }

        }, 0, INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_fragment, container, false);
    }

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
        //adapter.setListUser(DataHolder.getInstance().getUserList());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "");
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Show all users after collapsing
                adapter.setListUser(DataHolder.getInstance().getUserList());
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;  // Return true to expand action view
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // Hande event submit query on search
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Handle search card
        ArrayList<User> users = new ArrayList<>();
        for (User user : DataHolder.getInstance().getUserList()) {
            // search by first name
            if (user.getBio().getFirstName().contains(query)) {
                users.add(user);
            }
        }
        if (users.isEmpty()) {
            Toast.makeText(getActivity(), "Can't find this user", Toast.LENGTH_LONG).show();
        } else {
            adapter.setListUser(users);
        }
        return true;
    }


    // Handle event query change on search
    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<User> users = new ArrayList<>();
        for (User user : DataHolder.getInstance().getUserList()) {
            // search by first name
            if (user.getBio().getFirstName().contains(newText)) {
                users.add(user);
            }
        }
        adapter.setListUser(users);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                // handle click Search button
                Toast.makeText(getActivity(), "Action search", Toast.LENGTH_LONG).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getActivity(), "Something else", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle click an card in fragment
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // retrieve the ListView item
        User user = DataHolder.getInstance().getUserList().get(position);

        // start new intent
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        // Send user information
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        String jsonUser = gson.toJson(user, type);
        i.putExtra("User", jsonUser);
        startActivity(i);
    }
    private class GetUserListRequest extends AsyncTask<String, Void, Integer> {

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
                Log.e("Json User", jsonResponse);
                Log.e("message USer", urlConnection.getResponseMessage());
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
                    Type type = new TypeToken<ArrayList<User>>() {
                    }.getType();
                    DataHolder.getInstance().setUserList((ArrayList<User>) gson.fromJson(jsonResponse, type));
                    Log.e("Size User", String.valueOf(DataHolder.getInstance().getUserList().size()));
                }
                adapter.setListUser(DataHolder.getInstance().getUserList());
/*                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("GOT_USER_LIST");
                sendBroadcast(broadcastIntent);*/
            } catch (Exception e) {

            }
        }
    }
}

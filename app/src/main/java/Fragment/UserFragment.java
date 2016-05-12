package fragment;

/**
 * Created by Luan on 5/2/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import adapter.UserAdapter;
import entity.DataHolder;
import entity.User;
import support.Support;

public class UserFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    ListView myListView;
    public UserAdapter adapter;

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize adapter
        DataHolder.getInstance().setUserList(new ArrayList<User>());
        adapter = new UserAdapter(getActivity(), R.layout.user_fragment, DataHolder.getInstance().getUserList());

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

    private class GetListUserReQuest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Load list user...");
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
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<User>>() {
                    }.getType();
                    //jsonResponse = "[{\"_id\":\"56ffa1817cd3b998110c6ba6\",\"__v\":2,\"following\":[],\"followers\":[\"56ffc829e7fb7511006fddea\"],\"cards\":[],\"image\":[\"http://i.imgur.com/gfJTLEs.jpg\"],\"bio\":{\"phoneNumber\":\"02851858102\",\"university\":\"To change account information\",\"age\":22,\"lastName\":\"Your Email on Top\",\"firstName\":\">> Click\"},\"local\":{\"password\":\"$2a$09$jyHO2tczZBD1c4mb9s0X9uP9TxLI7rS6lOgUS0.EgIJ/jUoBLdLZO\",\"email\":\"testmail3@gmail.com\"}},{\"_id\":\"56ffbc7c740a253017e53658\",\"__v\":2,\"following\":[],\"followers\":[\"56ffc829e7fb7511006fddea\"],\"cards\":[],\"image\":[\"http://i.imgur.com/pz5Q8HB.jpg\"],\"bio\":{\"phoneNumber\":\"090111111\",\"university\":\"some University\",\"age\":10,\"lastName\":\"some last name\",\"firstName\":\"some first name\"},\"local\":{\"password\":\"$2a$09$eDsUAVkUiWWEjyTBuGl4jO/3HNE0JBytT0Wv1T.qvcc.Soiu9nPn6\",\"email\":\"testads@gmail.com\"}}]";
                    DataHolder.getInstance().setUserList((ArrayList<User>) gson.fromJson(jsonResponse, type));
                    Log.e("Size User", String.valueOf(DataHolder.getInstance().getUserList().size()));
                } else {
                    Toast.makeText(getActivity(), "Error while getting user list", Toast.LENGTH_LONG).show();
                }
                adapter.setListUser(DataHolder.getInstance().getUserList());
            } catch (Exception e) {

            }
        }
    }


}

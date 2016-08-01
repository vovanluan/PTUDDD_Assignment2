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
import android.widget.GridView;
import android.widget.Toast;

import com.yuyo.hikaru.activity.CourseActivity;
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

import adapter.CardAdapter;
import entity.Course;
import entity.DataHolder;
import support.BackgroundRequest;
import support.Support;

public class CourseFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{
    public CardAdapter adapter;
    public GridView gridView;
    private View view;
    private int INTERVAL = 10 * 1000;
    public CourseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize adapter
        adapter = new CardAdapter(getActivity());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Log.e("Get notification list", "10s");
                new GetCardListRequest().execute(Support.HOST + "cards");
            }

        }, 0, INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.course_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize gridview
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        //adapter.setListCard(DataHolder.getInstance().getCourseList());
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
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
                // Show all cards after collapsing
                adapter.setListCard(DataHolder.getInstance().getCourseList());
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
        ArrayList<Course> resultCourses = new ArrayList<>();
        for (Course course : DataHolder.getInstance().getCourseList()) {
            // search by title, creator or description
            if(course.getTitle().contains(query) || DataHolder.getInstance().getUserById(course.getCreated_by()).getBio().getFirstName().contains(query)
                    || course.getDescription().contains(query)) {
                resultCourses.add(course);
            }
        }
        if(resultCourses.isEmpty()) {
            Toast.makeText(getActivity(),"Can't find this course", Toast.LENGTH_LONG).show();
        }
        else {
            adapter.setListCard(resultCourses);
        }
        return true;
    }


    // Handle event query change on search
    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Course> resultCourses = new ArrayList<>();
        for (Course course : DataHolder.getInstance().getCourseList()) {
            // search by title, creator or description
            if(course.getTitle().contains(newText) || DataHolder.getInstance().getUserById(course.getCreated_by()).getBio().getFirstName().contains(newText)
                    || course.getDescription().contains(newText)) {
                resultCourses.add(course);
            }
        }
        adapter.setListCard(resultCourses);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_search:
                // handle click Search button
                Toast.makeText(getActivity(),"Action search", Toast.LENGTH_LONG).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getActivity(),"Something else", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle click an card in fragment
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // retrieve the GridView item
        Course course = DataHolder.getInstance().getCourseList().get(position);

        // do something
        Intent intent = new Intent(getActivity(), CourseActivity.class);
        Gson gson = new Gson();
        Type type = new TypeToken<Course>() {
        }.getType();
        String jsonCourse = gson.toJson(course, type);
        intent.putExtra("course", jsonCourse);
        startActivity(intent);
    }
    private class GetCardRequest extends AsyncTask<String, Void, Integer> {
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
                Log.e("CourseList", jsonResponse);
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
                Log.e("CODE", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Course>>(){}.getType();
                    DataHolder.getInstance().setCourseList((ArrayList<Course>) gson.fromJson(jsonResponse, type));
                }
                else {
                    Toast.makeText(getActivity(), "Error while getting card list", Toast.LENGTH_LONG).show();
                }
                adapter.setListCard(DataHolder.getInstance().getCourseList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class GetCardListRequest extends AsyncTask<String, Void, Integer> {

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
                Log.e("CourseList", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
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
                    Type type = new TypeToken<ArrayList<Course>>() {
                    }.getType();
                    DataHolder.getInstance().setCourseList((ArrayList<Course>) gson.fromJson(jsonResponse, type));
                    Log.e("Size course", String.valueOf(DataHolder.getInstance().getCourseList().size()));
                }
                adapter.setListCard(DataHolder.getInstance().getCourseList());
/*                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("GOT_CARD_LIST");
                sendBroadcast(broadcastIntent);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

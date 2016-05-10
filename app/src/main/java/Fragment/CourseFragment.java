package fragment;

/**
 * Created by Luan on 5/2/2016.
 */
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.Toast;

import com.example.luan.activity.CourseActivity;
import com.example.luan.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.CardAdapter;
import entity.Course;
import support.Support;

public class CourseFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{
    private ArrayList<Course> courseList;
    private CardAdapter adapter;
    private GridView gridView;
    private View view;
    public CourseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize card list
        courseList = new ArrayList<>();
        String getCardListURL = Support.HOST +"cards";
        new GetCardRequest().execute(getCardListURL);

        // initialize adapter
        adapter = new CardAdapter(getActivity());
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
                adapter.setListCard(courseList);
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
        for (Course course : courseList) {
            // search by title, creator or description
            if(course.getTitle().contains(query) || course.getCreate_by().getBio().getFirstName().contains(query)
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
        for (Course course : courseList) {
            // search by title, creator or description
            if(course.getTitle().contains(newText) || course.getCreate_by().getBio().getFirstName().contains(newText)
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
        Course course = courseList.get(position);

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
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                Log.e("CODE", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Course>>(){}.getType();
                    courseList = gson.fromJson(jsonResponse, type);
                    Log.e("Size", String.valueOf(courseList.size()));
                    Log.e("Course list:", jsonResponse);
                }
                else {
                    Toast.makeText(getActivity(), "Error while getting card list", Toast.LENGTH_LONG).show();
                }
                adapter.setListCard(courseList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

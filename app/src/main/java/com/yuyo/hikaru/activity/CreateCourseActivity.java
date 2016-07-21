package com.yuyo.hikaru.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import entity.Course;
import entity.DataHolder;
import support.Support;

/**
 * Created by Admin on 5/11/2016.
 */
public class CreateCourseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText title, description, place;
    private TextView creator;
    private Button create;
    private TextView fromTime, date;
    private SearchableSpinner searchableSpinner;
    private ArrayList<String> languages;
    private Course course;
    private int  year,  monthOfYear,  dayOfMonth,hourOfDay,  minute,  second;
    private DateTime timeCourseStart;
    private TimePickerDialog.OnTimeSetListener fromTimeListener, toTimeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_create_course);

        title = (EditText) findViewById(R.id.title);
        creator = (TextView) findViewById(R.id.creator);
        place = (EditText) findViewById(R.id.place);
        description = (EditText) findViewById(R.id.description);
        create = (Button) findViewById(R.id.createBtn);
        fromTime = (TextView) findViewById(R.id.fromTime);
        //toTime = (Button) findViewById(R.id.toTime);
        date = (TextView) findViewById(R.id.date);
        timeCourseStart = new DateTime();
        searchableSpinner = (SearchableSpinner) findViewById(R.id.languageSpinner);

        // set date value to current day
        year = DateTime.now().getYear();
        monthOfYear = DateTime.now().getMonthOfYear();
        dayOfMonth = DateTime.now().getDayOfMonth();

        creator.setText(DataHolder.getInstance().getUser().getBio().getFirstName());
        fromTime.setText(DateTime.now().getHourOfDay() + ":" + DateTime.now().getMinuteOfHour());
        date.setText(DateTime.now().getDayOfMonth() + "/" + DateTime.now().getMonthOfYear() + "/" + DateTime.now().getYear());
        create.setBackgroundColor(Color.parseColor("#2CC88F"));

        searchableSpinner.setTitle("Select Item");
        searchableSpinner.setPositiveButton("OK");
        languages = new ArrayList<>();
        languages.add("English");
        languages.add("Vietnamese");
        languages.add("French");
        languages.add("Japanese");
        languages.add("Spanish");
        languages.add("Chinese");
        languages.add("Finland");
        languages.add("German");
        languages.add("Korean");
        languages.add("Russian");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.language_spinner, languages) {

            @Override
            public boolean isEnabled(int position) {
                return position != 1;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.dropdown_spinner, null);
                }

                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setText(languages.get(position));

                switch (position) {
                    case 0:
                        tv.setTextColor(Color.RED);
                        break;
                    case 1:
                        tv.setTextColor(Color.BLUE);
                        break;
                    default:
                        tv.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };

        searchableSpinner.setAdapter(spinnerAdapter);

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Validation input
                if(title.getText().toString().isEmpty()){
                    showError(title);
                    return;
                }

                if(searchableSpinner.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(CreateCourseActivity.this, "Please choose a language", Toast.LENGTH_SHORT).show();
                }

                if(place.getText().toString().isEmpty()){
                    Toast.makeText(CreateCourseActivity.this, "Set the course's location", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(fromTime.getText().toString().equals("From")){
                    Toast.makeText(CreateCourseActivity.this, "You have to set the time when the course starts", Toast.LENGTH_SHORT).show();
                    return;
                }
/*                if(toTime.getText().toString().equals("To")){
                    Toast.makeText(CreateCourseActivity.this, "You have to set the time when the course finishes", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if(date.getText().toString().equals("Date")){
                    Toast.makeText(CreateCourseActivity.this, "You have to set the day when the course starts", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.getText().toString().isEmpty()) {
                    showError(description);
                    return;
                }

                // Create course data
                course = new Course();
                course.setCreated_by(DataHolder.getInstance().getUser().get_id());
                course.setCategory(searchableSpinner.getSelectedItem().toString());
                course.setTitle(title.getText().toString());
                course.setDescription(description.getText().toString());
                timeCourseStart = new DateTime(CreateCourseActivity.this.year, CreateCourseActivity.this.monthOfYear, CreateCourseActivity.this.dayOfMonth, CreateCourseActivity.this.hourOfDay, CreateCourseActivity.this.minute);
                course.setTime(timeCourseStart.toString());

                course.setPlace(place.getText().toString());
                String postCourseURL = Support.HOST + "cards";
                Log.e("URL", postCourseURL);
                new PostCardRequest().execute(postCourseURL);
            }
        });

/*        fromTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

            }
        };
        toTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                String time = hourOfDay + "h" + minute;
                toTime.setText(time);
            }
        };*/
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(CreateCourseActivity.this, R.anim.shake);
        editText.startAnimation(shake);
    }

    public void showDatePicker(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                CreateCourseActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setTitle("Choose a day");
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    public void showFromTimePicker(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false);
        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    public void showToTimePicker(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this.toTimeListener,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false);
        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.date.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + "h" + minute;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
        fromTime.setText(time);
    }

    private class PostCardRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(CreateCourseActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                // Convert course object to json string
                Gson gson = new Gson();
                Type type = new TypeToken<Course>() {
                }.getType();
                String json = gson.toJson(course, type);

                Log.e("PostJSON", json);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Json", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
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
                Log.e("CODE", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Course>() {
                    }.getType();
                    course = gson.fromJson(jsonResponse, type);
                    DataHolder.getInstance().getUser().getCards().add(course.get_id());
                    DataHolder.getInstance().getCourseList().add(course);
                    DataHolder.getInstance().getUserById(course.getCreated_by()).getCards().add(course.get_id());
                    Toast.makeText(CreateCourseActivity.this, "Creating course successfully", Toast.LENGTH_LONG).show();
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("UPDATE_CARD_LIST");
                    sendBroadcast(broadcastIntent);
                    finish();
                } else {
                    Toast.makeText(CreateCourseActivity.this, "Error while creating new course", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.example.luan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import entity.Course;
import entity.DataHolder;
import support.Support;

/**
 * Created by Admin on 5/11/2016.
 */
public class CreateCourseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText title, description, place;
    private TextView creator;
    private Button create, fromTime, date;
    private SearchableSpinner searchableSpinner;
    private ArrayList<String> languages;
    private Course course;
    private StringBuilder timeStart;
    private Calendar timeCourseStart;
    private TimePickerDialog.OnTimeSetListener fromTimeListener, toTimeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        title = (EditText) findViewById(R.id.title);
        creator = (TextView) findViewById(R.id.creator);
        place = (EditText) findViewById(R.id.place);
        description = (EditText) findViewById(R.id.description);
        create = (Button) findViewById(R.id.createBtn);
        fromTime = (Button) findViewById(R.id.fromTime);
        //toTime = (Button) findViewById(R.id.toTime);
        date = (Button) findViewById(R.id.date);
        searchableSpinner = (SearchableSpinner) findViewById(R.id.languageSpinner);
        timeStart = new StringBuilder();
        timeCourseStart = new GregorianCalendar();

        creator.setText(DataHolder.getInstance().getUser().getBio().getFirstName());
        searchableSpinner.setTitle("Select Item");
        searchableSpinner.setPositiveButton("OK");
        languages = new ArrayList<>();
        languages.add("English");
        languages.add("Vietnamese");
        languages.add("French");
        languages.add("Japanese");
        languages.add("Spanish");
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

                Log.e("TIME",timeCourseStart.toString());
                course.setTime(timeCourseStart.toString());

                course.setPlace(place.getText().toString());
                String postCourseURL = Support.HOST + "cards";
                Log.e("URL", postCourseURL);
                new PostCardRequest().execute(postCourseURL);
                //TODO: Response Internal Server Error after sending post card request
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
/*        timeStart.append(year);
        timeStart.append("-");
        timeStart.append(monthOfYear + 1);
        timeStart.append("-");
        timeStart.append(dayOfMonth);
        timeStart.append("T");*/
        timeCourseStart.set(Calendar.YEAR, year);
        timeCourseStart.set(Calendar.MONTH, monthOfYear);
        timeCourseStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.date.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + "h" + minute;
        timeCourseStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
        timeCourseStart.set(Calendar.MINUTE, minute);
        timeCourseStart.set(Calendar.SECOND, second);
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
                    Log.e("Course:", jsonResponse);
                    Toast.makeText(CreateCourseActivity.this, "Creating course successfully", Toast.LENGTH_LONG).show();
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

package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luan.activity.R;

import java.util.ArrayList;
import java.util.Random;

import entity.Course;
import support.Support;

/**
 * Created by Luan on 5/2/2016.
 */
public class CardAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Course> courses;

    public CardAdapter(Context context) {
        this.context = context;
        this.courses = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Course getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.card_item, parent, false);
        }

        // Generate random color
/*        Random generator = new Random();
        int random = generator.nextInt(9);
        String color = Support.COLOR[random];

        convertView.setBackgroundColor(Color.parseColor(color));*/
        ImageView courseImage = (ImageView) convertView.findViewById(R.id.courseImage);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView creator = (TextView) convertView.findViewById(R.id.creator);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView star = (TextView) convertView.findViewById(R.id.star);
        //TextView vote = (TextView) convertView.findViewById(R.id.vote);
        Course course = courses.get(position);

        title.setText(course.getTitle());
        creator.setText(course.getCreate_by().getBio().getFirstName());
        description.setText(course.getDescription());
        star.setText(String.valueOf(course.getRating()));
        //vote.setText(String.valueOf(course.getUpvotes()));

        Random generator = new Random();
        int random = generator.nextInt(9);
        String color = Support.COLOR[random];
        courseImage.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }

    // Update new data in adapter
    public void setListCard(ArrayList<Course> courses) {
        this.courses.clear();
        this.courses.addAll(courses);
        // Update adapter's data
        notifyDataSetChanged();
    }
}

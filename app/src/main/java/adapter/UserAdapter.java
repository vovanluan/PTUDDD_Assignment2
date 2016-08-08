package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuyo.hikaru.activity.R;

import java.util.ArrayList;

import entity.User;

/**
 * Created by Luan on 3/30/2016.
 */
public class UserAdapter extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private Context context;

    public UserAdapter(Context context, int layoutResourceId, ArrayList<User> users) {
        super(context, layoutResourceId, users);
        this.context = context;
        this.userList = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.short_info, parent, false);
        }

        // Get the data item for this position
        User user = userList.get(position);

        if (user != null) {
            TextView firstName = (TextView) convertView.findViewById(R.id.firstname);
            TextView age = (TextView) convertView.findViewById(R.id.age);

            String fullName = user.getBio().getFirstName() + " " + user.getBio().getLastName();
            firstName.setText(fullName);
            age.setText(String.valueOf(user.getBio().getAge()));
        }
        return convertView;
    }

    // update list user
    public void setListUser(ArrayList<User> users) {
        this.userList.clear();
        this.userList.addAll(users);
        notifyDataSetChanged();
    }
}

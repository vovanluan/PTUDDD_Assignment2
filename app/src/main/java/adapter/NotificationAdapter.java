package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.luan.activity.R;

import java.util.ArrayList;

import entity.Course;
import entity.DataHolder;
import entity.Notification;
import entity.User;

/**
 * Created by Luan on 3/30/2016.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notificationList;
    private Context context;

    public NotificationAdapter(Context context, int layoutResourceId, ArrayList<Notification> notifications) {
        super(context, layoutResourceId, notifications);
        this.context = context;
        this.notificationList = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.notification_item, parent, false);
        }

        // Get the data item for this position
        Notification notification = notificationList.get(position);

        if (notification != null) {
            TextView notificationTextView = (TextView) convertView.findViewById(R.id.notification);
            Course course = DataHolder.getInstance().getCourseById(notification.getFor_card());
            notificationTextView.setText(notification.getDescription());
        }
        return convertView;
    }

    // update list user
    public void setNotificationList(ArrayList<Notification> notifications) {
        Log.e("NOTI SIZE", String.valueOf(notifications.size()));
        this.notificationList = new ArrayList<>();
        Log.e("NOTI SIZE", String.valueOf(notifications.size()));
        this.notificationList.addAll(notifications);
        Log.e("NOTI SIZE", String.valueOf(notifications.size()));
        notifyDataSetChanged();
    }

    public ArrayList<Notification> getNotificationList(){
        return notificationList;
    }
}

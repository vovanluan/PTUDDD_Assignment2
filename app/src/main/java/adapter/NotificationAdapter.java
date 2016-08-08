package adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuyo.hikaru.activity.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import entity.Notification;
import support.Support;

/**
 * Created by Luan on 3/30/2016.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notificationList;
    private Context context;
    private CircleImageView iconNoti;

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
            setDescriptionStyle(convertView, notification);

            setNotificationIcon(convertView, notification);
        }
        return convertView;
    }

    private void setNotificationIcon(View convertView, Notification notification) {
        iconNoti = (CircleImageView) convertView.findViewById(R.id.imgNoti);
        if (notification.getType() == Support.NOTI_TYPE_NEWCOURSE) {
            iconNoti.setImageResource(R.drawable.ic_fiber_new);
        } else if (notification.getType() == Support.NOTI_TYPE_NEWUPVOTE) {
            iconNoti.setImageResource(R.drawable.ic_thumb_up);
        }
    }

    private void setDescriptionStyle(View convertView, Notification notification) {
        TextView notificationTextView = (TextView) convertView.findViewById(R.id.notification);

        SpannableString notiDescription = new SpannableString( notification.getDescription() + ".");

        int maxLength = 0;
        if (notification.getType() == Support.NOTI_TYPE_PAIRUP) {
            maxLength = notification.getStudentName().length();
        } else if (notification.getType() == Support.NOTI_TYPE_NEWCOURSE) {
            maxLength = notification.getTeacherName().length();
        } else if (notification.getType() == Support.NOTI_TYPE_NEWUPVOTE) {
            maxLength = notification.getStudentName().length();
        }

        notiDescription.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                maxLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        notificationTextView.setText(notiDescription );
    }

    // update list user
    public void setNotificationList(ArrayList<Notification> notifications) {
        this.notificationList = new ArrayList<>();
        this.notificationList.addAll(notifications);
        notifyDataSetChanged();
    }

    public ArrayList<Notification> getNotificationList(){
        return notificationList;
    }
}

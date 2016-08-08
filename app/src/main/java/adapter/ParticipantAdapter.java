package adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyo.hikaru.activity.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import entity.User;

/**
 * Created by Bach Do on 8/8/2016.
 */
public class ParticipantAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mDataSource;

    public ParticipantAdapter(Context context, ArrayList<User> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public User getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.participant_item, parent, false);

        TextView userName = (TextView) rowView.findViewById(R.id.participantName);
        TextView userAge = (TextView) rowView.findViewById(R.id.participantAge);
        CircleImageView userImage = (CircleImageView) rowView.findViewById(R.id.icon);

        User user = getItem(position);

        String fullName = user.getBio().getFirstName() + " " + user.getBio().getLastName();
        userName.setText(fullName);
        String description = "Age: " + user.getBio().getAge() + ", Uni/Job: " + user.getBio().getUniversity();
        userAge.setText(description);
        userImage.setImageResource(R.drawable.user_ava_1);


        return rowView;

    }
}

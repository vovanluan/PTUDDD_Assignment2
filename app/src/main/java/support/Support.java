package support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.example.luan.activity.R;

import java.util.ArrayList;

import entity.DataHolder;

/**
 * Created by Luan on 29/03/2016.
 */
public class Support {
    public static final String HOST = "https://yuyo-app.herokuapp.com/mobile/";
    public static final String[] COLOR = {
        "#43A047",
        "#689F38",
        "#EEFF41",
        "#FB8C00",
            "#039BE5",
            "#F44336",
            "#EC407A",
            "#AB47BC",
            "#3F51B5"
    };
    public boolean isValidEmail(CharSequence email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static final int getCategoryFlag(String category){
        switch(category) {
            case "English":
                return R.drawable.usa;
            case "Vietnamese":
                return R.drawable.vietnam;
            case "French":
                return R.drawable.france;
            case "Japanese":
                return R.drawable.japan;
            case "Spanish":
                return R.drawable.spain;
            default:
                return R.drawable.usa;
        }
    }

}

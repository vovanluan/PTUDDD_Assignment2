package support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;

import entity.DataHolder;

/**
 * Created by Luan on 29/03/2016.
 */
public class Support {
    public static final String HOST = "https://yuyo-beta.herokuapp.com/mobile/";
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

}

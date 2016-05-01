package support;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.example.luan.activity.R;

/**
 * Created by Luan on 29/03/2016.
 */
public class Support {
    public static final String HOST = "https://yuyo-beta.herokuapp.com/";
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

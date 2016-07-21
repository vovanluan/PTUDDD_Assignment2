package support;

import android.text.TextUtils;
import android.util.Patterns;

import com.yuyo.hikaru.activity.R;

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
                return R.drawable.ic_flag_usa;
            case "Vietnamese":
                return R.drawable.ic_flag_vietnam;
            case "French":
                return R.drawable.ic_flag_france;
            case "Japanese":
                return R.drawable.ic_flag_japan;
            case "Spanish":
                return R.drawable.ic_flag_spain;
            case "Chinese":
                return R.drawable.ic_flag_chinese;
            case "Finland":
                return R.drawable.ic_flag_finland;
            case "German":
                return R.drawable.ic_flag_german;
            case "Korean":
                return R.drawable.ic_flag_korean;
            case "Russian":
                return R.drawable.ic_flag_russian;
            default:
                return R.drawable.ic_flag_usa;
        }
    }

}

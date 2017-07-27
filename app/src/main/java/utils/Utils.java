package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/27.
 */

public class Utils {

    public static void toast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }
}

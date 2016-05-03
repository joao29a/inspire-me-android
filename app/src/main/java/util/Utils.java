package util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    public static void hideKeyboard(Activity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

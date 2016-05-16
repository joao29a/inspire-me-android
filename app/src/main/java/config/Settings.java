package config;

import android.content.Context;
import android.content.SharedPreferences;

import listener.StatusListener;
import model.Quote;

public class Settings {
    public static final String ADDRESS = "http://52.67.31.25:5050";

    public static final String PREFERENCES_FILENAME = "settings";

    public static final boolean DEBUG_MODE = true;

    public static final String STATUS_PARAM = "status";

    public static void checkStatus(Integer status, StatusListener listener) {
        switch (status) {
            case 0: //success
                listener.onSuccess();
                break;
            case 1: //failure
                listener.onFailure();
                break;
        }
    }

    public static void saveAuthor(Context context, String author) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Quote.AUTHOR_PARAM, author);
        editor.commit();
    }

    public static String getSavedAuthor(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILENAME, 0);
        return sp.getString(Quote.AUTHOR_PARAM, "");
    }

    public static final String BACKGROUND_IMAGE_NAME = "background_";
    public static final Integer BACKGROUND_IMAGE_SIZE = 21;

    public static final String[] FONTS = {"ChopinScript.ttf", "GreatVibes-Regular.ttf"};
}

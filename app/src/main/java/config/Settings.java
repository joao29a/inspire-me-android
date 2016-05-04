package config;

import listener.StatusListener;

public class Settings {
    public static final String ADDRESS = "http://54.233.77.68:5050";

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

    public static final String BACKGROUND_IMAGE_NAME = "background_";
    public static final Integer BACKGROUND_IMAGE_SIZE = 10;

    public static final String[] FONTS = {"ChopinScript.ttf", "GreatVibes-Regular.ttf"};
}

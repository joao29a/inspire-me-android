package http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestProcess {
    private static RequestProcess mInstance;
    private RequestQueue mRequestQueue;
    private static Context ctx;

    private RequestProcess(Context context) {
        this.ctx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestProcess getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestProcess(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }
}
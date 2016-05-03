package request;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import config.Settings;
import http.JsonObjectRequest;
import http.RequestProcess;
import model.Quote;

public class QuoteRequest {

    /*
     *  POST REQUEST - /quotes/save
     *  Save a quote on the server
     */
    public static void saveQuote(Context context, Quote quote,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        String url = Settings.ADDRESS + "/quotes/save";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                quote.getParams(), null, listener, errorListener);
        RequestProcess.getInstance(context).addToRequestQueue(request);
    }

    /*
     * GET REQUEST - /quotes
     * Get a quote from the server
     */
    public static void getRandomQuote(Context context, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        String url = Settings.ADDRESS + "/quotes";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, null, listener, errorListener);
        RequestProcess.getInstance(context).addToRequestQueue(request);
    }
}

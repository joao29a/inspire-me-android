package activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.joao29a.quote.R;

import org.json.JSONObject;

import config.Settings;
import listener.StatusListener;
import model.Quote;
import request.QuoteRequest;
import util.Utils;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuote;
    private TextView textViewAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertQuote();
            }
        });
    }

    private void insertQuote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View layout = getLayoutInflater().inflate(R.layout.alert_insert_quote, null);

        textViewQuote  = (TextView) layout.findViewById(R.id.textViewQuote);
        textViewAuthor = (TextView) layout.findViewById(R.id.textViewAuthor);

        builder.setView(layout);

        builder.setTitle(R.string.sendQuote);

        builder.setPositiveButton(R.string.send, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = (Button) dialog.getButton(DialogInterface.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer maxQuoteLength = getResources()
                                .getInteger(R.integer.maxQuoteLength);
                        Integer maxAuthorLength = getResources()
                                .getInteger(R.integer.maxAuthorLength);
                        if (isTextViewValid(textViewQuote, maxQuoteLength)
                                && isTextViewValid(textViewAuthor, maxAuthorLength)) {
                            Quote quote = new Quote(textViewQuote.getText().toString(),
                                    textViewAuthor.getText().toString());
                            saveQuote(quote);
                            Utils.hideKeyboard(MainActivity.this);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    public boolean isTextViewValid(TextView textView, Integer maxLength) {
        String text = textView.getText().toString();
        if (text.isEmpty()) {
            String error = getResources().getString(R.string.emptyField);
            textView.setError(error);
            return false;
        } else if (text.length() > maxLength) {
            String error = getResources().getString(R.string.overflowMaxLength);
            textView.setError(error);
            return false;
        }
        return true;
    }

    private void saveQuote(Quote quote) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                getString(R.string.sending), true);

        QuoteRequest.saveQuote(this, quote, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                checkSaveQuoteResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (Settings.DEBUG_MODE) {
                    Log.e("VolleyError", "Error", error);
                }
                Snackbar.make(findViewById(android.R.id.content), R.string.errorQuoteSent,
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void checkSaveQuoteResponse(JSONObject response) {
        try {
            Integer status = response.getInt(Settings.STATUS_PARAM);
            Settings.checkStatus(status, new StatusListener() {
                @Override
                public void onSuccess() {
                    Snackbar.make(findViewById(android.R.id.content), R.string.quoteSent,
                            Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    Snackbar.make(findViewById(android.R.id.content), R.string.quoteNotSent,
                            Snackbar.LENGTH_LONG).show();
                }
            });

        } catch (Exception ex) {
            Snackbar.make(findViewById(android.R.id.content), R.string.errorQuoteSent,
                    Snackbar.LENGTH_LONG).show();
        }
    }
}

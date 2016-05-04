package activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joao29a.quote.R;

import org.json.JSONObject;

import java.util.Random;

import config.Settings;
import listener.StatusListener;
import model.Quote;
import request.QuoteRequest;
import util.Utils;

public class MainActivity extends AppCompatActivity {

    private EditText editTextQuote;
    private EditText editTextAuthor;
    private FrameLayout frameLayout;
    private ImageView imageViewBackground;
    private TextView textQuote;
    private TextView authorQuote;
    private FloatingActionButton fab;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertQuote();
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuote();
            }
        });

        imageViewBackground = (ImageView) findViewById(R.id.imageBackground);

        textQuote   = (TextView) findViewById(R.id.textQuote);
        authorQuote = (TextView) findViewById(R.id.authorQuote);

        logoAnimation();
    }

    private void logoAnimation() {
        final ImageView image = (ImageView) findViewById(R.id.imageLogo);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                showAds();
                getQuote();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        image.startAnimation(anim);
    }

    private void showAds() {
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void insertQuote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View layout = getLayoutInflater().inflate(R.layout.alert_insert_quote, null);

        editTextQuote  = (EditText) layout.findViewById(R.id.textViewQuote);
        editTextAuthor = (EditText) layout.findViewById(R.id.textViewAuthor);

        editTextAuthor.setText(Settings.getSavedAuthor(this));

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
                        if (isTextViewValid(editTextQuote, maxQuoteLength)
                                && isTextViewValid(editTextAuthor, maxAuthorLength)) {
                            Quote quote = new Quote(editTextQuote.getText().toString(),
                                    editTextAuthor.getText().toString());
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

        Settings.saveAuthor(this, quote.getAuthor());

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

    private void checkQuote(final JSONObject response) {
        try {
            Integer status = response.getInt(Settings.STATUS_PARAM);
            Settings.checkStatus(status, new StatusListener() {
                @Override
                public void onSuccess() {
                    try {
                        showQuote(Quote.getQuote(response));
                    } catch (Exception ex) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.errorGetQuote,
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure() {
                    Snackbar.make(findViewById(android.R.id.content), R.string.errorGetQuote,
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Snackbar.make(findViewById(android.R.id.content), R.string.errorGetQuote,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private Drawable getRandomBackground() {
        Random random = new Random();
        Integer val = random.nextInt(Settings.BACKGROUND_IMAGE_SIZE);
        String file = String.format("@drawable/%s%d", Settings.BACKGROUND_IMAGE_NAME, val);
        int imageResource = getResources().getIdentifier(file, null, getPackageName());
        return ResourcesCompat.getDrawable(getResources(), imageResource, null);
    }

    private Typeface getRandomTypeface() {
        Random random = new Random();
        Integer val = random.nextInt(Settings.FONTS.length);
        return Typeface.createFromAsset(getAssets(), String.format("fonts/%s",
                Settings.FONTS[val]));
    }

    private void showQuote(Quote quote) {
        imageViewBackground.setImageDrawable(getRandomBackground());
        //Typeface tf = getRandomTypeface();
        //textQuote.setTypeface(tf);
        textQuote.setText(String.format("\"%s\"", quote.getText()));

        //authorQuote.setTypeface(tf);
        authorQuote.setText(quote.getAuthor());
    }

    private void getQuote() {
        QuoteRequest.getRandomQuote(this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                checkQuote(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(android.R.id.content), R.string.errorGetQuote,
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

package chuckles.jonathanfries.com.chucklesandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.contentful.java.cda.CDASpace;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Main class of a simple example of an Android App using Contentful.
 * <p>
 * This sample code shows you how to do the bare minimum of things needed to connect your Android
 * app to Contentful.
 * It'll create a client to communicate with Contentful, fetches information about the space it is
 * connected to, and shows how to retrieve all entries of this space and lastly how to filter
 * search your space for specific criteria.
 * </p>
 * <h1>Compilation</h1>
 * {@code ./gradlew build}
 * <h1>Running</h1>
 * {@code ./gradlew run}
 *
 * @see <a href="file:///app/src/main/AndroidManifest">AndroidManifest</a> for permissions needed.
 * @see <a href="file:///app/build.gradle">build.gradle</a> for dependencies.
 * @see <a href="http://contentful.com/">Contentful Main Website</a>
 * @see <a href="http://app.contentful.com/">Contentful Management WebApp</a>
 * @see <a href="http://doc.contentful.com/java/hello-world">Contentful documentation</a>
 */
public class AboutActivity extends AppCompatActivity {

    //URL for retrieving the intro text.
    private static final String introUrl = "https://cdn.contentful.com/spaces/jk4dnk7cynhq/entries/4jkedoxufKUyoC4mQyqSCY?access_token=445cd1547e7b23312b00e9bb242a77da85d545cff70b211d05374084e09dbe2f";

    /*
     * This variable will store the view to put the result messages into.
     */
    private WebView messageView;
    private StringBuilder messageBuilder;

    //the loading Dialog
    ProgressDialog pDialog;

    /*
     * This private variable will be used for formatting the output. It will be set to
     * an empty string to annotate, that it should not have a topmost border. As soon
     * as something gets outputted, this limiter will be set to a border.
     */


    /**
     * Creates this activity.
     * <p>
     * Since the activity is not shown yet, we ignore this method.
     *
     * @param savedInstanceState arguments saved from the last time this activity opened, not used here.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        messageBuilder = new StringBuilder();
        messageView = (WebView) findViewById(R.id.mainWebView);
        messageView.loadData(messageBuilder.toString(), "text/html; charset=utf-8", "UTF-8");
    }

    public void shutIt(View view)
    {
          this.finish();
    }

    /**
     * Resume state of activity.
     * <p>
     * This method gets called once the activity should be active, or displayed, so it is the perfect
     * time for us to start talking to contentful.
     */
    @Override protected void onResume() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Hold your horses, Jeffrey.");
        pDialog.setCancelable(false);

        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_LONG).show();
        } else {

            pDialog.show();

            // make HTTP request to retrieve the weather
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    introUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Parsing json object response
                        // response will be a json object

                        // display weather description into the "description textview"

                        messageBuilder.append(response.getJSONObject("fields").getString("introText"));
                        messageView.loadData(messageBuilder.toString(), "text/html; charset=utf-8", "UTF-8");

                        // hide the loading Dialog
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }


                }


            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                    pDialog.dismiss();
                }
            });

            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }

    /*
     * And we are done. Please feel free to stick around, change some code to see how it works, or
     * continue reading more in depth guides at {@see <a href="docs.contentful.com/java"> our
     * documentation</a>}
     */
        super.onResume();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}

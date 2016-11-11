package in.zollet.abhilash.dictionary;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.zollet.abhilash.dictionary.data.DictionaryColumns;
import in.zollet.abhilash.dictionary.data.DictionaryProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.zollet.abhilash.dictionary.DownloadManager.getInstance;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Dictionary";
    public static volatile long ExecutionTime = 0;
    public static volatile long totalExecutionTime = 0;
    public static volatile int count = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    TextView execution_time,total_executionTime;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        execution_time = (TextView) findViewById(R.id.executionTime);
        total_executionTime =(TextView) findViewById(R.id.total_executionTime);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        startDownload();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            ArrayList<ContentProviderOperation> dictonary = new ArrayList<ContentProviderOperation>();
            dictonary.add(ContentProviderOperation.newDelete(DictionaryProvider.Dictionary.CONTENT_URI).build());
            try {
                getApplicationContext().getContentResolver().
                        applyBatch(DictionaryProvider.AUTHORITY, dictonary);
                Toast.makeText(this, "Database Cleared", Toast.LENGTH_SHORT).show();
            } catch (RemoteException | OperationApplicationException e) {
                Toast.makeText(this, "OOPS! Something went wrong", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public synchronized void displayTImetaken(long executionTime, String alphabet) {

        count++;
        totalExecutionTime += executionTime;
        if(ExecutionTime > executionTime || ExecutionTime == 0){
            ExecutionTime = executionTime;
            displayExecutionTime(ExecutionTime,alphabet);

        }
        if(count == 26)
        downloadCompleteAction();

        Log.v(TAG, "count : "+ String.valueOf(count));
        Log.v(TAG, "executin time : "+ String.valueOf(ExecutionTime));
        Log.v(TAG, "total executin time : "+ String.valueOf (totalExecutionTime));
    }

    private synchronized void displayExecutionTime(final long executionTime, final String alphabet ) {

        runOnUiThread(new Runnable() {
            public void run() {

               String totalTime = String.format(Locale.ENGLISH,"%d min, %d sec",
                       TimeUnit.MILLISECONDS.toMinutes(executionTime),
                       TimeUnit.MILLISECONDS.toSeconds(executionTime) -
                               TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(executionTime))
               );
                execution_time.setText("Shortest time taken: " + totalTime + "\nfor alphabate: " + alphabet);
            }
        });
    }

    private synchronized void downloadCompleteAction(){

            runOnUiThread(new Runnable() {
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    double total_exec_time = totalExecutionTime / ExecutionTime ;
                    total_executionTime.setText("Total Execution time (in exec_time unit) : " +String.valueOf(total_exec_time) + " exec_time");
                }
            });

    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {

                 Toast.makeText(getApplicationContext(), toast , Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void startDownload() {

        count = 0;
        ExecutionTime = 0;
        totalExecutionTime = 0;

        ArrayList<ContentProviderOperation> dictonary = new ArrayList<ContentProviderOperation>();
        dictonary.add(ContentProviderOperation.newDelete(DictionaryProvider.Dictionary.CONTENT_URI).build());
        try {
            getApplicationContext().getContentResolver().
                    applyBatch(DictionaryProvider.AUTHORITY,dictonary);
        } catch (RemoteException | OperationApplicationException e) {
            Toast.makeText(this, "OOPS! Something went wrong", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.VISIBLE);
        for (char alphabate = 'a'; alphabate <= 'z'; alphabate++)
            getInstance().
                    mDecodeThreadPool.
                    execute(downloadTask(String.valueOf(alphabate)));

    }

    public Runnable downloadTask(final String alphabet) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Started for: "+ alphabet);
                long startTime = System.currentTimeMillis();
                ArrayList<ContentProviderOperation> dictionary = new ArrayList<ContentProviderOperation>();
                try {
                    String url = "http://unreal3112.16mb.com/wb1913_" + alphabet + ".html";
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("body").select("p");

                    for (Element link : links) {
                        String word = (link.select("b").text());
                        String fos = (link.select("i").text());
                        String meaning = link.text().substring(link.text().indexOf(")") + 1);
                        ContentValues values = new ContentValues();

                        values.put(DictionaryColumns.WORD, word);
                        values.put(DictionaryColumns.FIGURE_OF_SPEECH, fos);
                        values.put(DictionaryColumns.MEANING, meaning);
                        values.put(DictionaryColumns.STARTING_ALPHABATE, alphabet);
                        dictionary.add(ContentProviderOperation.newInsert(DictionaryProvider.Dictionary.CONTENT_URI)
                                .withValues(values).build());

                    }

                    try {
                        getApplicationContext().getContentResolver().
                                applyBatch(DictionaryProvider.AUTHORITY, dictionary);
                        long endTime   = System.currentTimeMillis();
                        long executionTime = endTime - startTime;
                        displayTImetaken(executionTime, alphabet);
                        Log.v(TAG, "Saved in db for "+ alphabet);

                    } catch (RemoteException | OperationApplicationException e) {
                       showToast("OOPS!! Something went wrong");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return runnable;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}

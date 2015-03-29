package fr.insa_lyon.bg4if.trollgame;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
            file.delete();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendToServer(File file) {

        file.renameTo(new File(file.getParent() + "/" +getUsername() + ".jpg"));

        try {

            Log.i(TAG, "Sending file to server : " + getUsername());
            HttpClient httpCLient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MyApp.SERVER_ROOT + "/api/photo");


            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("user_photo", file);
            multipartEntity.addPart("user_name", getUsername());
            httpPost.setEntity(multipartEntity);

            HttpResponse response = httpCLient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();


        } catch (IOException ex) {

        }
    }
<<<<<<< HEAD

=======
>>>>>>> origin/experimental

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

<<<<<<< HEAD
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

=======
        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

>>>>>>> origin/experimental
            if (parts.length > 1) {
                sp.edit().putString(MyApp.PREF_USERNAME, parts[0]).commit();
                return parts[0];
            }
        }
        return "unknown";
    }
}

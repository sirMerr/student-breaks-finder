package roantrevormarcdanieltiffany.com.dawsonbestfinder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.URL;

import roantrevormarcdanieltiffany.com.dawsonbestfinder.api.FriendFinder;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.api.OpenWeather;


/**
 * Main activity that displays all the image buttons, the menu, and
 * the current temperature
 *
 * @author Tiffany Le-Nguyen
 * @author Roan Chamberlain
 * @author Marc-Daniel Dialogo
 * @author Trevor Eames
 */
public class MainActivity extends MenuActivity {

    TextView tempView = null;
    private static final String TAG = MainActivity.class.getSimpleName();

    // For retrieving data from Settings SharedPreferences.
    protected static final String FIRST_NAME = "firstName";
    protected static final String LAST_NAME = "lastName";
    protected static final String EMAIL = "email";
    protected static final String PASSWORD = "password";
    private static final String FAILED = "FAILED";

    protected final String LAT = "lat";
    protected final String LON = "lon";

    // Name for Settings SharedPreferences
    protected static final String SETTINGS = "settings";
  
    /**
     * When invoke, will set up the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempView = findViewById(R.id.temperatureTextView);

        setTemp();

        // SharedPreferences for the Settings activity are stored in "settings"
        SharedPreferences prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE);

        // If the user has no account credentials stored, open the settings activity so that he or she can enter and save them.
        if(!prefs.contains(FIRST_NAME) && !prefs.contains(LAST_NAME) && !prefs.contains(EMAIL) && !prefs.contains(PASSWORD))
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTemp();
    }

    /**
     * Will fire an intent taking user to dawson homepage
     */
    public void onDawsonButton(View v) {
        String url = "https://dawsoncollege.qc.ca";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Will fire an intent taking the user to the About activity.
     */
    public void onTeamLogoButton(View v)
    {
        Intent intent = new Intent(this, AboutActivity.class);

        startActivity(intent);
    }

    /**
     * Will fire an intent taking user to class cancellations activity
     */
    public void onClassCancellations(View v) {
        Intent cci = new Intent(MainActivity.this, CancelledClassActivity.class);
        MainActivity.this.startActivity(cci);
    }

    /**
     * Will fire an intent taking user to find teacher activity
     */
    public void onFindTeacher(View v) {
        Intent intent = new Intent(this, FindTeacherActivity.class);
        startActivity(intent);
    }

    /**
     * Will fire an intent taking user to add to calendar activity
     */
    public void onAddToCalendar(View v) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * Will fire an intent taking user to notes activity
     */
    public void onNotes(View v) {
        Intent i = new Intent(this, NotesActivity.class);
        startActivity(i);
    }

    /**
     * Will fire an intent taking user to weather activity
     */
    public void onWeather(View v) {
        Intent i = new Intent(this, WeatherActivity.class);
        startActivity(i);
    }

    /**
     * Will fire an intent taking user to academic calendar activity
     */
    public void onAcademicCalendar(View v) {
        Intent i = new Intent(this, AcademicCalendarActivity.class);
        startActivity(i);
    }

    /**
     * Will fire an intent taking user to find friends activity
     */
    public void onFindFriends(View v) {
        Intent i = new Intent(this, FindFriendsActivity.class);
        startActivity(i);
    }

    /**
     * Will fire an intent taking user to where is friend activity
     */
    public void onWhereIsFriend(View v) {
        Intent i = new Intent(this, WhoIsFreeActivity.class);
        startActivity(i);
    }

    /**
     * Verifies whether or not the user has required permission for accessing
     * locaiton, if not will request them.
     */
    public void permissionCheck() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    0);
        }
    }

    public void setTemp() {
        permissionCheck();
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Log.d(TAG, "Can get location!");
            loadTempData();
        }
    }

    /**
     * Fetches latitude and logitude from shared preferences, and then runs GetTempTask
     * inner class.
     */
    public void loadTempData() {
        Log.d(TAG, "loadTempData()");
        double lat = PreferenceManager.getDefaultSharedPreferences(this).getFloat(LAT, 0);
        double lon = PreferenceManager.getDefaultSharedPreferences(this).getFloat(LON, 0);

        new GetTempTask().execute(String.valueOf(lat), String.valueOf(lon));
    }

    /**
     * Class that extends AsyncTask so as to perform network ops on a non-main thread
     */
    public class GetTempTask extends AsyncTask<String, Void, String> {

        /**
         * Using a background thread will take the parameters passed, which
         * will be latitude and longitude, and will perform the HttpUrlConnection
         * methods which are required.
         *
         * @param strings Task params
         * @return Temperature
         */
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground()");
            if(strings.length == 0) {
                Log.d(TAG, "No params:");
                return FAILED;
            }

            String lat = strings[0];
            String lon = strings[1];

            if(lat == null || lon == null){
                Log.e(TAG, "Null params");
                return FAILED;
            }

            URL url = OpenWeather.buildTempUrl(lat, lon);

            try {
                String response = OpenWeather.getResponseFromHttpUrl(url);
                Log.d(TAG, response);

                String temp = OpenWeather.getTempValueFromJSON(response);
                Log.d(TAG, temp);

                return temp;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return FAILED;
            }
        }

        /**
         * Will set the tempView
         * @param string
         */
        @Override
        protected void onPostExecute(String string) {
            Log.d(TAG, "onPostExecute()");
            Log.d(TAG, string);

            double temp = Double.parseDouble(string);
            temp = temp - 273.15;
            int roundTemp = (int) Math.round(temp);

            if(string != null) {
                tempView.setText(roundTemp + "\u00b0" + "C");
            }
        }
    }
}



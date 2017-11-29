package roantrevormarcdanieltiffany.com.dawsonbestfinder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.net.URL;

import roantrevormarcdanieltiffany.com.dawsonbestfinder.api.OpenWeather;

/**
 * Activity which has a widget to input the city & use a spinner
 * for the ISO 3166 country codes, default to Montreal,CA.
 * Have a button to display the UV index and the 5 day forecast
 * This can be in the same Activity/Fragment or launch a new one
 *
 * {@see https://developer.android.com/guide/topics/providers/calendar-provider.html}
 *
 * @author Tiffany Le-Nguyen
 * @author Roan Chamberlain
 * @author Marc-Daniel Dialogo
 * @author Trevor Eames
 */
public class WeatherActivity extends MenuActivity {
    private static final String TAG = WeatherActivity.class.getSimpleName();

    private TextView tvTestData;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "called onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvTestData = findViewById(R.id.tvTestData);

        loadUVIData();
    }

    /**
     * This method will get the user's location to get
     * a ultraviolet index from the openweather api in the
     * background
     */
    private void loadUVIData() {
        Log.d(TAG, "called loadUVIData()");
        // @todo Replace lat/lon with non-fake data
        double lat = 37.75;
        double lon = -122.37;

        new OpenWeatherTask().execute(String.valueOf(lat),String.valueOf(lon));
    }

    /**
     * Class that extends AsyncTask to perform network requests
     */
    public class OpenWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String TAG = WeatherActivity.class.getSimpleName();

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String[] doInBackground(String... strings) {
            Log.d(TAG, "called doInBackground()");
            if (strings.length == 0) {
                Log.d(TAG, "No params");
                return null;
            }

            String lat = strings[0];
            String lon = strings[1];

            URL url = OpenWeather.buildUrl(lat, lon);

            try {
                String json = OpenWeather.getResponseFromHttpUrl(url);
                return OpenWeather.getUviValueFromJSON(WeatherActivity.this, json);
            } catch (Exception err) {
                Log.e(TAG, err.getLocalizedMessage());
                return null;
            }
        }

        /**
         * Overrides {@code onPostExecute} to display results
         *
         * @param strings
         */
        @Override
        protected void onPostExecute(String[] strings) {
            Log.d(TAG, "called onPostExecute()");
//            super.onPostExecute(strings);

            for (String data: strings) {
                Log.d(TAG, data);
            }
            if (strings != null) {
                tvTestData.setText(strings[0]);
            }
        }
    }
}

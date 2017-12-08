package roantrevormarcdanieltiffany.com.dawsonbestfinder;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import roantrevormarcdanieltiffany.com.dawsonbestfinder.api.FriendFinder;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.api.NetworkUtils;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.beans.Friend;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.beans.QueryParam;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.fragments.WhoIsFreeTimePickerFragment;

/**
 * Activity which will allow the user to specify a day of the week
 * and a start time and end time. A list of friends will display
 * with the option to click on any of them, firing an intent to
 * launch an email activity with the email being automatically
 * set as the recipient
 *
 * @author Tiffany Le-Nguyen
 * @author Roan Chamberlain
 * @author Marc-Daniel Dialogo
 * @author Trevor Eames
 */
public class WhoIsFreeActivity extends MenuActivity implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = WhoIsFreeActivity.class.getSimpleName();

    private boolean clickedStart;
    private Spinner daySpinner;
    private EditText etBreakStart, etBreakEnd;
    private ListView lvFriends;

    private int hStart, hEnd, mStart, mEnd;

    /**
     * Will set up the activity.
     * Instantiates fields and calls
     * method to set up the spinner
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_free);

        etBreakStart = findViewById(R.id.etBreakStart);
        etBreakEnd = findViewById(R.id.etBreakEnd);
        daySpinner = findViewById(R.id.daySpinner);
        lvFriends = findViewById(R.id.lvFriends);

        setDaySpinner();
    }

    /**
     * Set the values of the spinner to be
     * the days of the week
     */
    private void setDaySpinner() {
        String[] days = getResources().getStringArray(R.array.days);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);

        daySpinner.setAdapter(adapter);
        daySpinner.setSelection(adapter.getPosition(getString(R.string.def_day)));
    }

    /**
     * Displays the time picker dialog
     *
     * @param v
     */
    public void showTimePickerDialog(View v) {
        Log.d(TAG, "showTimePickerDialog()");
        DialogFragment timeFragment = new WhoIsFreeTimePickerFragment();
        timeFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * set the fields to the values of
     * the widgets.
     *
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "onTimeSet()");

        DateFormat simp = new SimpleDateFormat("hh:mm", Locale.getDefault());
        Date date = null;
        try {
            date = simp.parse(hourOfDay + ":" + minute);
        } catch (ParseException pe) {
            Log.e(TAG, pe.getMessage());
        }

        String time = simp.format(date);

        if(clickedStart) {
            etBreakStart.setText(time);
            this.hStart = hourOfDay;
            this.mStart = minute;
        } else {
            etBreakEnd.setText(time);
            this.hEnd = hourOfDay;
            this.mEnd = minute;
        }
    }

    public void clickSearch(View v) {
        Log.d(TAG, "clickSearch()");
        loadFriendsFreeData();
    }

    public void clickBreakStart(View v) {
        Log.d(TAG, "clickBreakStart()");
        showTimePickerDialog(v);
        clickedStart = true;
    }

    public void clickBreakEnd(View v) {
        Log.d(TAG, "clickBreakEnd()");
        showTimePickerDialog(v);
        clickedStart = false;
    }

    /**
     * Grab data from shared preferences, as well
     * as widgets. Then call OpenFriendsFreeTask
     * with all of the data
     */
    private void loadFriendsFreeData() {
        Log.d(TAG, "loadFriendsFreeData()");
        if(etBreakStart.getText().toString().trim().equals("") || etBreakEnd.getText().toString().trim().equals("")) {
            Log.d(TAG, "No start or end time provided");
            Toast.makeText(WhoIsFreeActivity.this, "Invalid Input",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.SETTINGS, MODE_PRIVATE);

        if(prefs.getString( SettingsActivity.EMAIL, "") == "" || prefs.getString(SettingsActivity.PASSWORD, "") == "") {
            Log.d(TAG, "No email or password provided");
            Toast.makeText(WhoIsFreeActivity.this, "Invalid Input",
                    Toast.LENGTH_LONG).show();
            return;
        }
        new OpenFriendsFreeTask().execute(daySpinner.getSelectedItem().toString(),
                etBreakStart.getText().toString().replace(":",""),
                etBreakEnd.getText().toString().replace(":",""));
    }

    /**
     * Async Task class for retrieving the who is free data
     */
    public class OpenFriendsFreeTask extends AsyncTask<String, Void, List<Friend>> {
        private final String TAG = OpenFriendsFreeTask.class.getSimpleName();

        @Override
        protected List<Friend> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground()");

            if (strings.length == 0) {
                Log.d(TAG, "No params");
                return null;
            }

            SharedPreferences prefs = getSharedPreferences(SettingsActivity.SETTINGS, MODE_PRIVATE);
            String email = prefs.getString(SettingsActivity.EMAIL, "");
            String password = prefs.getString(SettingsActivity.PASSWORD, "");

            String day = strings[0];

            switch (day) {
                case "Tuesday":
                    day = "2";
                    break;
                case "Wednesday":
                    day = "3";
                    break;
                case "Thursday":
                    day = "4";
                    break;
                case "Friday":
                    day = "5";
                    break;
                default:
                    day = "1";
                    break;
            }

            try {
                List<QueryParam> queryParams = new ArrayList<>();
                queryParams.add(new QueryParam(FriendFinder.EMAIL_KEY, email));
                queryParams.add(new QueryParam(FriendFinder.PASSWORD_KEY, password));
                queryParams.add(new QueryParam(FriendFinder.DAY_KEY, day));
                queryParams.add(new QueryParam(FriendFinder.BREAK_START, strings[1]));
                queryParams.add(new QueryParam(FriendFinder.BREAK_END, strings[2]));

                URL url = NetworkUtils.buildUrl(FriendFinder.WHO_IS_FREE_URL, queryParams);
                String json = NetworkUtils.getResponseFromHttpUrl(url);

                return FriendFinder.getFriendsFromJSON(json);
            } catch (IOException | JSONException err) {
                Log.e(TAG, err.getMessage());
            }
            return new ArrayList<>();
        }

        /**
         * Populates the list of friends.
         * On click it will open an email intent,
         * with the clicked friend as the filled
         * in recipient email.
         *
         * @param friends
         */
        @Override
        protected void onPostExecute(final List<Friend> friends) {
            Log.d(TAG, "onPostExecute()");
            lvFriends.setAdapter(new BaseAdapter() {

                @Override
                public int getCount() {
                    return friends.size();
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) WhoIsFreeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.listview_friend, null);
                    Holder holder = new Holder();
                    convertView.setTag(holder);

                    holder.tv = convertView.findViewById(R.id.friendTextView);
                    holder.tv.setText(friends.get(position).toString());

                    holder.tv.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String[] address = new String[1];
                            address[0] = friends.get(position).getEmail().trim();
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, address);
                            if(intent.resolveActivity(getPackageManager()) != null)
                                startActivity(intent);
                        }
                    });
                    return convertView;
                }
                class Holder {
                    TextView tv;
                }
            });
        }
    }
}
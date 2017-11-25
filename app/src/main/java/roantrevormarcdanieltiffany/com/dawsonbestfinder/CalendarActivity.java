package roantrevormarcdanieltiffany.com.dawsonbestfinder;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import roantrevormarcdanieltiffany.com.dawsonbestfinder.fragments.DatePickerFragment;
import roantrevormarcdanieltiffany.com.dawsonbestfinder.fragments.TimePickerFragment;

/**
 * Activity which adds an event to the calendar.
 * The user can specify a date, start time, end time
 * and the event itself. Uses a calendar provider
 *
 * @see "https://developer.android.com/guide/topics/providers/calendar-provider.html"
 *
 * @author Tiffany Le-Nguyen
 * @author Roan Chamberlain
 * @author Marc-Daniel Dialogo
 * @author Trevor Eames
 */
public class CalendarActivity extends MenuActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = ChooseTeacherActivity.class.getSimpleName();
    private boolean clickedStart;
    private EditText eventTitle;
    private EditText etDate;
    private EditText etStartTime;
    private EditText etStopTime;
    private int year, month, day, startHour, startMinute, endHour, endMinute;

    /**
     * When invoked, will set up the activity.
     * Assigns the list view, get the data from firebase
     * and set the list of category titles
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "called onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etStopTime = findViewById(R.id.etStopTime);
        eventTitle = findViewById(R.id.etEvent);
    }

    public void showTimePickerDialog(View view) {
        Log.d(TAG, "called showTimePickerDialog()");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View view) {
        Log.d(TAG, "called showDatePickerDialog()");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * @param view       the picker associated with the dialog
     * @param year       the selected year
     * @param month      the selected month (0-11 for compatibility with
     *                   {@link Calendar#MONTH})
     * @param dayOfMonth th selected day of the month (1-31, depending on
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "called onDateSet()");
        Log.d(TAG,"Date = " + year);
        etDate.setText(dayOfMonth + "/" + month + "/" + year);
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
    }

    /**
     * Called when the user is done setting a new time and the dialog has
     * closed.
     *
     * @param view      the view associated with this listener
     * @param hourOfDay the hour that was set
     * @param minute    the minute that was set
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "called onTimeSet()");
        Log.d(TAG,"Time = " + hourOfDay + ":" + minute);
        Log.d(TAG, "After start time: " + clickedStart);

        if (clickedStart) {
            etStartTime.setText(hourOfDay + ":" + minute);
            this.startHour = hourOfDay;
            this.startMinute = minute;
        } else {
            etStopTime.setText(hourOfDay + ":" + minute);
            this.endHour = hourOfDay;
            this.endMinute = minute;
        }
    }

    public void clickStartTime(View view) {
        Log.d(TAG, "called clickStartTime()");
        showTimePickerDialog(view);
        clickedStart = true;
    }

    public void clickEndTime(View view) {
        Log.d(TAG, "called clickEndTime()");
        showTimePickerDialog(view);
        clickedStart = false;
    }

    /**
     * @todo Check what to put inside calendar event (title/description/location/etc)
     * @param view
     */
    public void clickAddToCalendar(View view) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, startHour, startMinute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, endHour, endMinute);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, eventTitle.getText().toString());

        /**
         *                 .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
         .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym");
         */
        startActivity(intent);

    }
}

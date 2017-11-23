package roantrevormarcdanieltiffany.com.dawsonbestfinder;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import roantrevormarcdanieltiffany.com.dawsonbestfinder.fragments.TeacherContactFragment;

/**
 * Created by sirMerr on 2017-11-19.
 */

public class TeacherContactActivity extends MenuActivity {
    private static final String TAG = TeacherContactActivity.class.getSimpleName();

    private Context context;
    private TeacherContactFragment teacherContactFragment;
    private Bundle extras;
    protected static final String TEACHER_ID = "teacherId";

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Called onCreate");
        super.onCreate(savedInstanceState);

        this.context = this;
        this.extras = getIntent().getExtras();

        teacherContactFragment = new TeacherContactFragment();
        createFragments(teacherContactFragment);
    }
}

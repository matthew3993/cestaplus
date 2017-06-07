package sk.cestaplus.cestaplusapp.activities.other_activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.MenuItem;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.utilities.Util;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextSizeUtil;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * /////////////////////////////////////////////////////////////////////////////////
 * MMMM's NOTES:
 *  - create new project and add SettingActivity with wizard to see very good example
 *    of how to do settings and how it all works
 *  - normally SettingActivity is list of subcategories of activities =>
 *    when these subcategories are clicked, it shows fragment corresponding that subcategory
 *    (in new activity or swaps the fragment - depends on screen size)
 *  - using very good Android PreferenceActivity APIs.
 *
 *  - **BUT** this app doesn't have so many setting (at least for now), so they are show always in single list
 *      => I do NOT use any fragments at all (for now)
 *  - for this I have slightly modified 'setupSimplePreferencesScreen()' method =>
 *      => this method initialize preferences declared in 'preferences.xml' file
 *      => it binds corresponding preferences to it's values
 *      => add desired onClick behavior (if needed)
 *      => it is called from activity's 'onCreate()'
 *      => note: initialisation of preferences can be done right inside onCreate(), but was moved to setupSimplePreferencesScreen()
 *
 * AlertDialogs SOURCES:
 *      Simple: http://stackoverflow.com/questions/26097513/android-simple-alert-dialog
 *      Theme.AppCompat Exception: http://stackoverflow.com/a/30181104
 *      TwoButtons: http://stackoverflow.com/a/8228190
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setupSimplePreferencesScreen();
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).

                    //preference.setSummary(R.string.pref_ringtone_silent); // ******** WARNING !!! commented manually **********

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     * ************ slightly modified ************ => see class comment
     */
    private void setupSimplePreferencesScreen() {
        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add preferences from resource -- MODIFICATION
        addPreferencesFromResource(R.xml.preferences);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.

    // POST NOTIFICATIONS
        // checkbox and Switch preferences don't need to bind summaries to values
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_post_notifications_key)));

    // LIST STYLE
        // binding is needed to automatically show actually selected value in summary of preference
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_list_style_key)));

    // TEXT SIZE
        // other parameters are set in 'preferences.xml' file
        String [] entries = TextSizeUtil.getTextSizes(getApplicationContext());
        //CharSequence[] entryValues = { "0", "1", "2" };
        //ListPreference lp = (ListPreference)findPreference("pref_text_size");
        ListPreference lp = (ListPreference)findPreference(getString(R.string.pref_text_size_key));
        lp.setEntries(entries);
        //lp.setEntryValues(entryValues);
        //lp.setDefaultValue("1"); //probably don't work

        // binding is needed to automatically show actually selected value in summary of preference
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_text_size_key)));

    // VERSION
        // all done in xml file

    //DISPLAY PARAMETERS
        // SOURCE: https://stackoverflow.com/questions/10537320/how-to-open-a-dialog-from-a-text-entry-on-a-preferencescreen

        Preference dialogPreference = (Preference) getPreferenceScreen().findPreference(getString(R.string.pref_show_display_parameters_key));
        final Context activityContext = this;

        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                // SOURCE: check class comment
                AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activityContext, R.style.alertDialog)).create();
                alertDialog.setTitle(activityContext.getString(R.string.alert_dialog_display_parameters_title));

                String msg = Util.checkScreenSizeAndDensity(activityContext);

                alertDialog.setMessage(msg);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            }
        });
    }// end setupSimplePreferencesScreen()

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Show the Up button in the action bar.
            // This only SHOWS up button, this don't handles click - click are handled
            // in 'SettingsActivity.onOptionsItemSelected()' - 'case android.R.id.home:'
            // WARNING: NOT (or maybe also) in GeneralPreferenceFragment.onOptionsItemSelected() !
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                // HANDLE NAVIGATION UP = BACK
                //startActivity(new Intent(getActivity(), SettingsActivity.class));

                // simply finish settings activity, because SettingActivity doesn't start any activity (for now)
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName) ;
                // other fragments
                //|| DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                //|| NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class));

                // simply finish settings activity, because SettingActivity doesn't start any activity (for now)
                getActivity().finish();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
}
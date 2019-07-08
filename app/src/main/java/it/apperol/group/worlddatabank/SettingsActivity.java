package it.apperol.group.worlddatabank;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchAnimListener;

import java.io.File;
import java.util.Locale;

import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setTitle(getResources().getString(R.string.title_activity_settings));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // This static call will reset default values only on the first ever read
        SharedPreferences prefs = getSharedPreferences("it.apperol.group.worlddatabank_preferences", MODE_PRIVATE);
        prefs.edit().putString("language", PreferenceManager.getDefaultSharedPreferences(this).getString("language", "it")).apply();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private ListPreference lang;
        private Preference day_night;
        private DayNightSwitch dayNightSwitch;

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            dayNightSwitch = getActivity().findViewById(R.id.day_night_switch);
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                dayNightSwitch.setIsNight(true);
            } else {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                dayNightSwitch.setIsNight(false);
            }

            if(preference.getKey().equals("day_night")){
                // return "true" to indicate you handled the click

                if(dayNightSwitch.isNight()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //restartApp();
                }

                dayNightSwitch.setIsNight(!dayNightSwitch.isNight());
                return true;
            }
            return false;
        }

        private void restartApp() {
            Intent i = new Intent(MainActivity.mainActivityContext, MainActivity.class);
            startActivity(i);
            getActivity().finish();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //addPreferencesFromResource(R.xml.root_preferences);
            setLang(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("language", "it"));

            lang = findPreference("language");
            day_night = findPreference("day_night");

            if (lang.getValue() == null) {
                lang.setValueIndex(1);
            }

            lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setLang(newValue.toString());
                    return true;
                }
            });
        }

        private void setLang(String lang) {
            MainActivity.language = lang;
            Locale locale = new Locale(MainActivity.language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,this.getResources().getDisplayMetrics());

        }
    }
}
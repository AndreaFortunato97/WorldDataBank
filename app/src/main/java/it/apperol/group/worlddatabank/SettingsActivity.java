package it.apperol.group.worlddatabank;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchAnimListener;

import java.io.File;

import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
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

        private ListPreference sync;
        private Preference day_night;
        private DayNightSwitch dayNightSwitch;

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if(preference.getKey().equals("day_night")){
                // user clicked "day_night" switch
                // take appropriate actions
                // return "true" to indicate you handled the click
                dayNightSwitch = getActivity().findViewById(R.id.day_night_switch);
                //dayNightSwitch.setIsNight(!dayNightSwitch.isNight());
                if(dayNightSwitch.isNight()){
                        Animation animation = new Animation(){
                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                dayNightSwitch.setIsNight(false);
                            }

                            @Override
                            public boolean willChangeBounds() {
                                return true;
                            }
                        };

                        animation.setDuration(500);
                        dayNightSwitch.startAnimation(animation);
                    } else {
                        Animation animation = new Animation(){
                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                dayNightSwitch.setIsNight(true);
                            }

                            @Override
                            public boolean willChangeBounds() {
                                return true;
                            }
                        };

                        animation.setDuration(500);
                        dayNightSwitch.startAnimation(animation);
                    }


                Toast.makeText(getActivity(), "Night: " + dayNightSwitch.isNight(), Toast.LENGTH_SHORT).show();

                return true;
            }
            return false;
        }

        private void deleteDataFiles() {
            String path =  getActivity().getApplicationContext().getFilesDir().toString();
            File directory = new File(path);
            File[] files = directory.listFiles();
            if(files.length > 0) {
                Log.i("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //addPreferencesFromResource(R.xml.root_preferences);

            sync = findPreference("language");
            day_night = findPreference("day_night");

            sync.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), "Nuova lingua: " + newValue.toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }
    }
}
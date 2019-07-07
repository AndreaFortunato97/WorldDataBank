package it.apperol.group.worlddatabank;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchAnimListener;
import com.mahfa.dnswitch.DayNightSwitchListener;

public class DayNightSwitchPreference extends Preference {
    public DayNightSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DayNightSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DayNightSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayNightSwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final DayNightSwitch dayNightSwitch = holder.itemView.findViewById(R.id.day_night_switch);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            dayNightSwitch.setIsNight(true);
        } else {
            dayNightSwitch.setIsNight(false);
        }

        dayNightSwitch.setAnimListener(new DayNightSwitchAnimListener() {
            @Override
            public void onAnimStart() {
            }

            @Override
            public void onAnimEnd() {
                if(dayNightSwitch.isNight()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //restartApp();
                }
            }

            @Override
            public void onAnimValueChanged(float v) {

            }
        });
    }

    private void restartApp() {
        Intent i = new Intent(MainActivity.mainActivityContext, MainActivity.class);
        getContext().startActivity(i);
    }
}

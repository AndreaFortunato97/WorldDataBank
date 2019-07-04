package it.apperol.group.worlddatabank;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.mahfa.dnswitch.DayNightSwitch;
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
        DayNightSwitch dayNightSwitch = holder.itemView.findViewById(R.id.day_night_switch);

        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean b) {
                Toast.makeText(holder.itemView.getContext(), "CLICKED!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

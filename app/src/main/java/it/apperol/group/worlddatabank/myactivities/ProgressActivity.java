package it.apperol.group.worlddatabank.myactivities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import it.apperol.group.worlddatabank.R;

public class ProgressActivity extends AppCompatActivity {
    public static Context contextProg;
    public static Activity actProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        contextProg = this;
        actProg = this;

    }
}

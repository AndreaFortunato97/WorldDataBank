package it.apperol.group.worlddatabank.myactivities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.apperol.group.worlddatabank.R;

public class ProgressActivity extends AppCompatActivity {
    public static Context contextProg;
    public static Activity actProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        contextProg = this;
        actProg = this;

    }
}

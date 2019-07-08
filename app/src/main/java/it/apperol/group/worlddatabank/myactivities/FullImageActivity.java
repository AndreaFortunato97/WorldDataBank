package it.apperol.group.worlddatabank.myactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import it.apperol.group.worlddatabank.R;

public class FullImageActivity extends AppCompatActivity {

    ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.MainAppThemeDark);
        } else {
            setTheme(R.style.MainAppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = (ImageView) findViewById(R.id.full_image);

        String data = getIntent().getExtras().getString("png");

        fullImage.setImageURI(Uri.parse(data));
    }
}

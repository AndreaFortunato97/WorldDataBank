package it.apperol.group.worlddatabank.myactivities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import it.apperol.group.worlddatabank.mydialogs.InfoDialog;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.myfragments.GalleryFragment;
import it.apperol.group.worlddatabank.myfragments.OfflineFragment;
import it.apperol.group.worlddatabank.myfragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Boolean doubleBackPressed = false;
    public static Context mainActivityContext;
    NavigationView navigationView;

    public static String language;

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            theme.applyStyle(R.style.MainAppThemeDark, true);
        } else {
            theme.applyStyle(R.style.MainAppTheme, true);
        }
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);

        mainActivityContext = this.getApplicationContext();

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new WelcomeFragment()).commit();
        setTitle(getResources().getString(R.string.app_title));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            navigationView.setBackgroundColor(getColor(R.color.backgroundDark));
            int colorInt = getResources().getColor(R.color.textColorDark);
            ColorStateList csl = ColorStateList.valueOf(colorInt);
            navigationView.setItemTextColor(csl);
        } else {
            navigationView.setBackgroundColor(getColor(R.color.backgroundLight));
            int colorInt = getResources().getColor(R.color.textColorLight);
            ColorStateList csl = ColorStateList.valueOf(colorInt);
            navigationView.setItemTextColor(csl);
        }

        setLang();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoDialog();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void openInfoDialog() {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(getSupportFragmentManager(), "InfoDialog");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(!(f instanceof WelcomeFragment)) {
                navigationView.getMenu().getItem(0).setChecked(true);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
                setTitle(getResources().getString(R.string.app_title));
            } else {
                if (doubleBackPressed) {
                    finish();
                    return;
                }
                doubleBackPressed = true;
                Toast.makeText(this, getResources().getString(R.string.back_exit), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed=false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        int positionOfMenuItem = 0;

        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString(getResources().getString(R.string.action_settings));

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        } else {
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        }
        item.setTitle(s);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home && !(navigationView.getMenu().getItem(0).isChecked())) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
            setTitle(getResources().getString(R.string.app_title));
        } else if (id == R.id.nav_gallery && !(navigationView.getMenu().getItem(1).isChecked())) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new GalleryFragment()).commit();
            setTitle(getResources().getString(R.string.menu_gallery));
        } else if (id == R.id.nav_offline && !(navigationView.getMenu().getItem(2).isChecked())) {
            WelcomeFragment.count = 2;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new OfflineFragment()).commit();
            setTitle(getResources().getString(R.string.menu_offline));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // 'false' se non voglio che rimanga selezionata l'opzione nel menu'
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        File tmpFolderToDelete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/.tmpChart/");

        if(tmpFolderToDelete.exists()) {
            deleteTempFolderRecursive(tmpFolderToDelete);
        }

        setLang();
    }

    private void setLang() {
        language = PreferenceManager.getDefaultSharedPreferences(this).getString("language", "it");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,this.getResources().getDisplayMetrics());
    }

    private void deleteTempFolderRecursive(File tmpFolderToDelete) {
        if (tmpFolderToDelete.isDirectory()) {
            if(tmpFolderToDelete.listFiles() != null) {
                for (File filesInDir : tmpFolderToDelete.listFiles()) {
                    deleteTempFolderRecursive(filesInDir);
                }
            }
        }
        tmpFolderToDelete.delete();
    }
}

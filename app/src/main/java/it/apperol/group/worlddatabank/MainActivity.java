package it.apperol.group.worlddatabank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import it.apperol.group.worlddatabank.myviews.MyTextView;

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

        // Inserisco nell'activity principale 'MainActivity' il fragment 'WelcomeFragment', cioè la pagina iniziale
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new WelcomeFragment()).commit();

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


        /*// TODO: PREFERENCE (LINGUA)
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean x = s.getBoolean("lang", false);
        Toast.makeText(this, x.toString(), Toast.LENGTH_SHORT).show();*/




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
        int positionOfMenuItem = 0; // or whatever...

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home && !(navigationView.getMenu().getItem(0).isChecked())) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
        } else if (id == R.id.nav_gallery && !(navigationView.getMenu().getItem(1).isChecked())) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new GalleryFragment()).commit();
        } else if (id == R.id.nav_offline && !(navigationView.getMenu().getItem(2).isChecked())) {
            WelcomeFragment.count = 2;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new OfflineFragment()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // 'false' se non voglio che rimanga selezionata l'opzione
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
        Locale.setDefault(locale); // Imposto la lingua dell'applicazione in Inglese
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,this.getResources().getDisplayMetrics()); // Aggiorno la configurazione (impostazione interna) dell'applicazione con la nuova lingua

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

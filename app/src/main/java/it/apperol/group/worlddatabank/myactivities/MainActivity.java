package it.apperol.group.worlddatabank.myactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
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
import java.util.ArrayList;
import java.util.Locale;

import it.apperol.group.worlddatabank.mydialogs.InfoDialog;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.myfragments.GalleryFragment;
import it.apperol.group.worlddatabank.myfragments.OfflineFragment;
import it.apperol.group.worlddatabank.myfragments.WelcomeFragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Boolean doubleBackPressed = false;
    public static Context mainActivityContext;
    NavigationView navigationView;

    public static String language;
    private String del_every;
    private Long pref_time;

    private ArrayList permissions = new ArrayList();
    private ArrayList permissionsToRequest;

    @Override
    public Resources.Theme getTheme() { // Override del 'getTheme' da parte dei fragments (NoActionBar)
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
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) { // Tema principale (con ActionBar)
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);

        mainActivityContext = this.getApplicationContext();

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new WelcomeFragment()).commit(); // Inserimento WelcomeFragment
        setTitle(getResources().getString(R.string.app_title));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // ActionBar superiore

        navigationView = findViewById(R.id.nav_view); // Menù laterale
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) { // Imposto il tema per la NavigationView
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

        setLang(); // Imposto la lingua dell'app

        SharedPreferences prefs = getSharedPreferences("it.apperol.group.worlddatabank_preferences", MODE_PRIVATE);
        prefs.edit().putString("del_after_time", PreferenceManager.getDefaultSharedPreferences(this).getString("del_after_time", "never")).apply(); // Imposto 'never' come default

        del_every = PreferenceManager.getDefaultSharedPreferences(this).getString("del_after_time", "never");
        pref_time = PreferenceManager.getDefaultSharedPreferences(this).getLong("time", System.currentTimeMillis());

        delEvery(); // Controllo periodico cancellazione files scaricati

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
        drawer.addDrawerListener(toggle); // Creazione menù laterale
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this); // OnClickListener dei bottoni nel menù laterale appena creato

        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void openInfoDialog() {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(getSupportFragmentManager(), "InfoDialog"); // Apertura 'InfoDialog' nel fragment corrente
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); // Se c'è il menù laterale aperto, chiudilo
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(!(f instanceof WelcomeFragment)) { // Se l'utente non è nel WelcomeFragment, torna a quest'ultimo
                navigationView.getMenu().getItem(0).setChecked(true);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
                setTitle(getResources().getString(R.string.app_title));
            } else { // Se l'utente è nel WelcomeFragment e preme due volte indietro entro 2 secondi, esci dall'applicazione
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
        getMenuInflater().inflate(R.menu.main, menu); // Aggiungo il tasto 'Impostazioni' in alto a destra (...)
        int positionOfMenuItem = 0;

        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString(getResources().getString(R.string.action_settings));

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0); // Scritta bianca
        } else {
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0); // Scritta nera
        }
        item.setTitle(s); // Imposto il colore della scritta 'Impostazioni' (in base al tema)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent); // Se l'utente clicca il tasto 'Impostazioni' apro l'activity 'SettingsActivity'
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // Quando l'utente clicca un tasto nella NavigationView
        int id = item.getItemId();

        // Se l'utente clicca un tasto E NON si trova già in quel fragment, cambia fragment con quello cliccato
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
        drawer.closeDrawer(GravityCompat.START); // Chiudi menù laterale a sinistra

        return true; // Seleziono il tasto cliccato nel menù laterale
    }

    @Override
    protected void onResume() {
        super.onResume();
        File tmpFolderToDelete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/.tmpChart/");

        if(tmpFolderToDelete.exists()) {
            deleteTempFolderRecursive(tmpFolderToDelete); // Se la cartella '.tmpChart' esiste, eliminala insieme al suo contenuto
        }

        setLang();
    }

    private void setLang() {
        language = PreferenceManager.getDefaultSharedPreferences(this).getString("language", "it");
        Locale locale = new Locale(language);
        Locale.setDefault(locale); // Imposto la lingua scelta
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,this.getResources().getDisplayMetrics()); // Aggiorno l'app con la nuova lingua
    }

    private void deleteTempFolderRecursive(File tmpFolderToDelete) { // Funzione che elimina una cartella e tutto il suo contenuto (incluse eventuali sottocartelle)
        if (tmpFolderToDelete.isDirectory()) {
            if(tmpFolderToDelete.listFiles() != null) {
                for (File filesInDir : tmpFolderToDelete.listFiles()) {
                    deleteTempFolderRecursive(filesInDir);
                }
            }
        }
        tmpFolderToDelete.delete();
    }

    private void delEvery() {
        if(hasAllPermissions() && !del_every.equals("never")) { // Se l'utente ha i permessi E NON ha selezionato 'Never' dalle impostazioni
            // Cancella tutti gli eventuali file scaricati dopo l'unità di tempo selezionata
            switch(del_every)
            {
                case "1d":
                    if((pref_time + Long.parseLong("86400000")) <= System.currentTimeMillis()) {
                        delAllFilesInFilesDir();
                    }
                    break;
                case "3d":
                    if((pref_time + Long.parseLong("259200000")) <= System.currentTimeMillis()) {
                        delAllFilesInFilesDir();
                    }
                    break;
                case "7d":
                    if((pref_time + Long.parseLong("604800000")) <= System.currentTimeMillis()) {
                        delAllFilesInFilesDir();
                    }
                    break;
                case "30d":
                    if((pref_time + Long.parseLong("2592000000")) <= System.currentTimeMillis()) {
                        delAllFilesInFilesDir();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void delAllFilesInFilesDir() {
        File path = getFilesDir(); // Directory 'files' standard, non visibile dall'utente
        Integer numberOfFiles;
        if(path.exists()) {
            File[] filesInFilesDir = path.listFiles();
            if(filesInFilesDir != null) {
                numberOfFiles = filesInFilesDir.length;
                if(numberOfFiles != 0) {
                    for(int i = 0; i < filesInFilesDir.length; i++) {
                        filesInFilesDir[i].delete(); // Cancella tutti i files
                    }
                    Toast.makeText(this, String.format(getResources().getString(R.string.deleted_offline_data), numberOfFiles), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private Boolean hasAllPermissions() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if(permissionsToRequest.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result; // Array di permessi NON accettati
    }

    private boolean hasPermission(String permission) {
        Boolean hasMarshmallow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (hasMarshmallow) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }
}

package it.apperol.group.worlddatabank.myactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import it.apperol.group.worlddatabank.MainActivity;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.MyCountryItem;
import it.apperol.group.worlddatabank.myadapters.MyCountryAdapter;
import it.apperol.group.worlddatabank.mythreads.FetchData;

public class CountryActivity extends AppCompatActivity {

    public static Context countryActivityContext;

    public static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static JSONArray ja;

    private static List<MyCountryItem> myCountryItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);



        countryActivityContext = this;

        recyclerView = findViewById(R.id.rvCountry);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FetchData process = new FetchData(getResources().getString(R.string.country_url), this, 0);
        process.execute();
    }

    public static void fetchCountry() {
        myCountryItems = new ArrayList<>();

        ja = FetchData.ja;

        try {
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                JSONObject jo = (JSONObject) ja.getJSONArray(1).get(i);
                if(!jo.get("capitalCity").equals("")) {
                    MyCountryItem myCountryItem = new MyCountryItem(
                            jo.getString("name") + " (" + jo.getString("iso2Code") + ")",
                            jo.getString("capitalCity"),
                    countryActivityContext.getResources().getString(R.string.flag_url) + jo.getString("iso2Code").toLowerCase() + ".png", jo.getString("iso2Code"));

                    myCountryItems.add(myCountryItem);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Collections.sort(myCountryItems, new Comparator<MyCountryItem>(){
            public int compare(MyCountryItem obj1, MyCountryItem obj2) {
                // Ordine crescente
                return obj1.getCountryName().compareToIgnoreCase(obj2.getCountryName());
            }
        });

        adapter = new MyCountryAdapter(myCountryItems, countryActivityContext);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((MyCountryAdapter) adapter).getFilter().filter(newText.toLowerCase());
                return false;
            }
        });
        return true;
    }
}

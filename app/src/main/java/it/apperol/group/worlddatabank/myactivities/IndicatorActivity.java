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

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.apperol.group.worlddatabank.MainActivity;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.MyIndicatorItem;
import it.apperol.group.worlddatabank.myadapters.MyIndicatorAdapter;
import it.apperol.group.worlddatabank.myadapters.MyTopicAdapter;
import it.apperol.group.worlddatabank.mythreads.FetchData;

public class IndicatorActivity extends AppCompatActivity {

    public static Context indicatorActivityContext;

    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static JSONArray ja;

    private static List<MyIndicatorItem> myIndicatorItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        indicatorActivityContext = this;

        setContentView(R.layout.activity_indicator);

        recyclerView = findViewById(R.id.rvIndicator);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        myIndicatorItems = new ArrayList<>();
        FetchData process = new FetchData("http://api.worldbank.org/v2/topic/" + MyTopicAdapter.topicID + "/indicator?format=json", this, 1);
        process.execute();
    }

    public static void fetchIndicator() {

        ja = FetchData.ja;

        try {
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                JSONObject jo = (JSONObject) ja.getJSONArray(1).get(i);
                MyIndicatorItem myIndicatorItem = new MyIndicatorItem(jo.getString("name"), jo.getString("id"));

                myIndicatorItems.add(myIndicatorItem);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Collections.sort(myIndicatorItems, new Comparator<MyIndicatorItem>(){
            public int compare(MyIndicatorItem obj1, MyIndicatorItem obj2) {
                // Ordine crescente
                return obj1.getIndicatorName().compareToIgnoreCase(obj2.getIndicatorName());
            }
        });

        adapter = new MyIndicatorAdapter(myIndicatorItems, indicatorActivityContext);
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
                ((MyIndicatorAdapter) adapter).getFilter().filter(newText.toLowerCase());
                return false;
            }
        });
        return true;
    }
}

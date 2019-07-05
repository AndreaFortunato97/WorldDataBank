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
import it.apperol.group.worlddatabank.itemlist.MyTopicItem;
import it.apperol.group.worlddatabank.myadapters.MyCountryAdapter;
import it.apperol.group.worlddatabank.myadapters.MyTopicAdapter;
import it.apperol.group.worlddatabank.mythreads.FetchData;

public class TopicActivity extends AppCompatActivity {

    private static Context topicActivityContext;

    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static JSONArray ja;

    private static List<MyTopicItem> myTopicItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        topicActivityContext = this;

        recyclerView = findViewById(R.id.rvTopic);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        myTopicItems = new ArrayList<>();
        FetchData process = new FetchData("http://api.worldbank.org/v2/topic/?format=json", this, 2);
        process.execute();
    }

    public static void fetchTopic() {

        ja = FetchData.ja;

        try {
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                JSONObject jo = (JSONObject) ja.getJSONArray(1).get(i);
                MyTopicItem myTopicItem = new MyTopicItem(jo.getString("value"), jo.getInt("id"));

                myTopicItems.add(myTopicItem);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Collections.sort(myTopicItems, new Comparator<MyTopicItem>(){
            public int compare(MyTopicItem obj1, MyTopicItem obj2) {
                // Ordine crescente
                return obj1.getTopicName().compareToIgnoreCase(obj2.getTopicName());
            }
        });

        adapter = new MyTopicAdapter(myTopicItems, topicActivityContext);
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
                ((MyTopicAdapter) adapter).getFilter().filter(newText.toLowerCase());
                return false;
            }
        });
        return true;
    }
}

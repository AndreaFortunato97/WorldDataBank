package it.apperol.group.worlddatabank.myadapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import it.apperol.group.worlddatabank.MainActivity;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.WelcomeFragment;
import it.apperol.group.worlddatabank.itemlist.MyCountryItem;
import it.apperol.group.worlddatabank.myactivities.CountryActivity;
import it.apperol.group.worlddatabank.myactivities.IndicatorActivity;
import it.apperol.group.worlddatabank.myactivities.PlotActivity;
import it.apperol.group.worlddatabank.myactivities.TopicActivity;
import it.apperol.group.worlddatabank.mythreads.FetchData;
import it.apperol.group.worlddatabank.myviews.MyTextView;

import static it.apperol.group.worlddatabank.myadapters.MyIndicatorAdapter.myIndicatorItemText;

public class MyCountryAdapter extends RecyclerView.Adapter<MyCountryAdapter.ViewHolder> implements Filterable {

    public static String countryIso2Code;
    public static String countryName;

    private List<MyCountryItem> myCountryItems;
    private List<MyCountryItem> myCountryItemFull;
    private static Context context;
    public static JSONArray ja;

    public MyCountryAdapter(List<MyCountryItem> myCountryItems, Context context) {
        this.myCountryItems = myCountryItems;
        this.context = context;
        myCountryItemFull = new ArrayList<>(myCountryItems);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.country_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MyCountryItem myCountryItem = myCountryItems.get(i);

        viewHolder.myTvCountryName.setText(myCountryItem.getCountryName());
        viewHolder.myTvCapitalName.setText(myCountryItem.getCapitalName());

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            CountryActivity.recyclerView.setBackgroundColor(context.getResources().getColor(R.color.backgroundDark));
        } else {
            CountryActivity.recyclerView.setBackgroundColor(context.getResources().getColor(R.color.backgroundLight));
        }

        Picasso.get()
                .load(myCountryItem.getImageUrl())
                .into(viewHolder.ivFlag);

        viewHolder.llCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryName = myCountryItem.getCountryName();
                countryIso2Code = myCountryItem.getCountryIso2Code();
                if(WelcomeFragment.count == 0) {
                    Intent topicIntent = new Intent(context, TopicActivity.class);
                    context.startActivity(topicIntent);
                }
                else if(WelcomeFragment.count == 1){
                    FetchData process = new FetchData("http://api.worldbank.org/v2/country/" + MyCountryAdapter.countryIso2Code + "/indicator/" + MyIndicatorAdapter.indicatorID + "?format=json", CountryActivity.countryActivityContext, 3);
                    process.execute();
                }
                    Toast.makeText(context, "Hai cliccato sul paese " + myCountryItem.getCountryName(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return myCountryItems.size();
    }

    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyCountryItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(myCountryItemFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MyCountryItem item : myCountryItemFull) {
                    if (item.getCountryName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myCountryItems.clear();
            myCountryItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        public MyTextView myTvCountryName;
        public MyTextView myTvCapitalName;
        public ImageView ivFlag;
        public LinearLayout llCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            myTvCountryName = itemView.findViewById(R.id.myTvCountryName);
            myTvCapitalName = itemView.findViewById(R.id.myTvCapitalName);
            ivFlag = itemView.findViewById(R.id.ivFlag);
            llCountry = itemView.findViewById(R.id.llCountry);
        }
    }

    public static void fetchDataControl() {
        ja = FetchData.ja;

                /*try {
                    for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                        JSONObject jo = (JSONObject) ja.getJSONArray(1).get(i);
                        if(!jo.get("value").equals("") && jo.get("value") != null && !jo.get("value").equals("null") && !jo.isNull("value")) {
                            plotDatas.add(new PlotObj(jo.get("date").toString(), jo.get("value").toString()));
                        }
                    }
                }catch (Exception e){
                    Log.i("[CRASH]", "CRASH");
                    e.printStackTrace();
                    return;
                }*/

        if(ja != null) {
            Intent plotIntent = new Intent(context, PlotActivity.class);
            context.startActivity(plotIntent);
            Toast.makeText(context, "Hai cliccato sul paese " + countryName, Toast.LENGTH_SHORT).show();
        } else {
            Log.i("[ERRORE]", "ERRORE");
        }
    }


}

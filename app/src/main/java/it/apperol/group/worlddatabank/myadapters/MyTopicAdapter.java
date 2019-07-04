package it.apperol.group.worlddatabank.myadapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.ListPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.MyCountryItem;
import it.apperol.group.worlddatabank.itemlist.MyTopicItem;
import it.apperol.group.worlddatabank.myactivities.IndicatorActivity;
import it.apperol.group.worlddatabank.myviews.MyTextView;

public class MyTopicAdapter extends RecyclerView.Adapter<MyTopicAdapter.ViewHolder> implements Filterable {

    public static Integer topicID;
    public static  String topicName;

    private List<MyTopicItem> myTopicItems;
    private List<MyTopicItem> myTopicItemsFull;
    private Context context;

    public MyTopicAdapter(List<MyTopicItem> myTopicItems, Context context) {
        this.myTopicItems = myTopicItems;
        this.context = context;
        myTopicItemsFull = new ArrayList<>(myTopicItems);
    }


    @NonNull
    @Override
    public MyTopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.topic_item, viewGroup, false);
        return new MyTopicAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTopicAdapter.ViewHolder viewHolder, int i) {
        final MyTopicItem myTopicItem = myTopicItems.get(i);

        viewHolder.myTvTopicName.setText(myTopicItem.getTopicName());

        viewHolder.llTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicID = myTopicItem.getTopicID();
                Intent indicatorIntent = new Intent(context, IndicatorActivity.class);
                context.startActivity(indicatorIntent);
                Toast.makeText(context, "Hai cliccato sul topic " + myTopicItem.getTopicName() + " ID: " + myTopicItem.getTopicID(), Toast.LENGTH_SHORT).show();
                topicName = myTopicItem.getTopicName();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTopicItems.size();
    }

    @Override
    public Filter getFilter() {
        return topicFilter;
    }

    private Filter topicFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyTopicItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(myTopicItemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MyTopicItem item : myTopicItemsFull) {
                    if (item.getTopicName().toLowerCase().contains(filterPattern)) {
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
            myTopicItems.clear();
            myTopicItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        public MyTextView myTvTopicName;
        public LinearLayout llTopic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            myTvTopicName = itemView.findViewById(R.id.myTvTopicName);
            llTopic = itemView.findViewById(R.id.llTopic);
        }
    }
}

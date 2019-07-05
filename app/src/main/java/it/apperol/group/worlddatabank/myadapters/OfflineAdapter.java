package it.apperol.group.worlddatabank.myadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;
import it.apperol.group.worlddatabank.myviews.MyTextView;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views

public class OfflineAdapter extends
        RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    private List<OfflineDataItem> offlineDataItemList;
    private  Context context;

    public OfflineAdapter(List<OfflineDataItem> offlineDataItemList,Context context){
        this.offlineDataItemList = offlineDataItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offline_data_card, parent, false);
        return new OfflineAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OfflineDataItem data = offlineDataItemList.get(position);

        holder.tvOfflineCountry.setText(data.getFileName());
        holder.llOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, data.getFileName(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return offlineDataItemList.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public MyTextView tvOfflineCountry;
        public  MyTextView tvOfflineIndicator;
        public LinearLayout llOffline;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(@NonNull View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvOfflineCountry = itemView.findViewById(R.id.myTvOfflineCountry);
            tvOfflineIndicator = itemView.findViewById(R.id.myTvOfflineIndicator);
            llOffline = itemView.findViewById(R.id.llOffline);
        }
    }
}

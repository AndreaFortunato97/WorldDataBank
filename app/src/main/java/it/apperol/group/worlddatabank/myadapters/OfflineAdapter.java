package it.apperol.group.worlddatabank.myadapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import it.apperol.group.worlddatabank.MainActivity;
import it.apperol.group.worlddatabank.OfflineFragment;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;
import it.apperol.group.worlddatabank.myactivities.PlotActivity;
import it.apperol.group.worlddatabank.myviews.MyTextView;


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views

public class OfflineAdapter extends
        RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    private List<OfflineDataItem> offlineDataItemList;
    private  Context context;
    public static JSONArray ja;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final OfflineDataItem data = offlineDataItemList.get(position);

        holder.tvOfflineCountry.setText(data.getFileName());
        holder.llOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, data.getFileName(), Toast.LENGTH_SHORT).show();
                String dataString = readData(data);
                try {
                    ja = new JSONArray(dataString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(MainActivity.mainActivityContext, PlotActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.mainActivityContext.startActivity(i);
            }
        });
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileToDelete = new File(MainActivity.mainActivityContext.getFilesDir(), data.getFileName());
                fileToDelete.delete();
                OfflineFragment.itemList.remove(position);
                notifyDataSetChanged();
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
        public MyTextView tvOfflineIndicator;
        public LinearLayout llOffline;
        public ImageView ivDel;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(@NonNull View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvOfflineCountry = itemView.findViewById(R.id.myTvOfflineCountry);
            tvOfflineIndicator = itemView.findViewById(R.id.myTvOfflineIndicator);
            llOffline = itemView.findViewById(R.id.llOffline);
            ivDel = itemView.findViewById(R.id.ivDel);
        }
    }

    private String readData(OfflineDataItem data) {
        File file = new File(MainActivity.mainActivityContext.getFilesDir(),data.getFileName());
        int len = (int) file.length();
        String contents;
        byte[] bytes = new byte[len];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        contents = new String(bytes);
        return contents;
    }
}

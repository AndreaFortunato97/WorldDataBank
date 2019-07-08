package it.apperol.group.worlddatabank.myadapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import it.apperol.group.worlddatabank.myactivities.MainActivity;
import it.apperol.group.worlddatabank.myfragments.OfflineFragment;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;
import it.apperol.group.worlddatabank.myactivities.PlotActivity;
import it.apperol.group.worlddatabank.myviews.MyTextView;


public class OfflineAdapter extends
        RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    public static String currentFileName = "";

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

        String[] fileNameSplitted = data.getFileName().split("-");
        String country = fileNameSplitted[0];
        String topic = fileNameSplitted[1];
        String indicator = fileNameSplitted[2].substring(0, fileNameSplitted[2].length()-4);

        holder.tvOfflineCountry.setText(country);
        holder.tvOfflineTopic.setText(topic);
        holder.tvOfflineIndicator.setText(indicator);

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
                currentFileName = data.getFileName().substring(0, data.getFileName().length()-4);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public MyTextView tvOfflineCountry;
        public MyTextView tvOfflineTopic;
        public MyTextView tvOfflineIndicator;
        public LinearLayout llOffline;
        public ImageView ivDel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOfflineCountry = itemView.findViewById(R.id.myTvOfflineCountry);
            tvOfflineTopic = itemView.findViewById(R.id.myTvOfflineTopic);
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

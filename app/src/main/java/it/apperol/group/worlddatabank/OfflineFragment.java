package it.apperol.group.worlddatabank;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;
import it.apperol.group.worlddatabank.myadapters.OfflineAdapter;
import it.apperol.group.worlddatabank.myviews.MyRecyclerView;

public class OfflineFragment extends Fragment {
    private View myView;

    private List<OfflineDataItem> itemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.offline_layout, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String path =  getActivity().getApplicationContext().getFilesDir().toString();

        Log.i("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.i("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            OfflineDataItem item = new OfflineDataItem(files[i].getName());
            itemList.add(item);
            Log.i("Files", "FileName:" + itemList.get(i).getFileName());
        }

        MyRecyclerView rvOffline = (MyRecyclerView) Objects.requireNonNull(getActivity()).findViewById(R.id.rvOffline);
        rvOffline.setHasFixedSize(true);

        OfflineAdapter offlineAdapter = new OfflineAdapter(itemList,getActivity().getApplicationContext());
        rvOffline.setAdapter(offlineAdapter);
        rvOffline.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

    }
}

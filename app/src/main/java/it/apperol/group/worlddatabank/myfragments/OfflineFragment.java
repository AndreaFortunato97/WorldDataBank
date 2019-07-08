package it.apperol.group.worlddatabank.myfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.itemlist.OfflineDataItem;
import it.apperol.group.worlddatabank.myadapters.OfflineAdapter;
import it.apperol.group.worlddatabank.myviews.MyRecyclerView;
import it.apperol.group.worlddatabank.myviews.MyTextView;

public class OfflineFragment extends Fragment {
    public static MyRecyclerView rvOffline;
    public static FragmentManager fmOffline;
    public static Fragment offlineFragment;

    private View myView;
    private MyTextView myTvNoData;

    public static List<OfflineDataItem> itemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.offline_layout, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fmOffline = getFragmentManager();
        offlineFragment = this;
        itemList = new ArrayList<>();

        String path =  getActivity().getApplicationContext().getFilesDir().toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                OfflineDataItem item = new OfflineDataItem(files[i].getName());
                itemList.add(item);
            }

            rvOffline = (MyRecyclerView) Objects.requireNonNull(getActivity()).findViewById(R.id.rvOffline);
            rvOffline.setVisibility(View.VISIBLE);
            rvOffline.setHasFixedSize(true);

            OfflineAdapter offlineAdapter = new OfflineAdapter(itemList, getActivity().getApplicationContext());
            rvOffline.setAdapter(offlineAdapter);
            rvOffline.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }
        else {
            myTvNoData = Objects.requireNonNull(getActivity()).findViewById(R.id.myTvNoData);
            myTvNoData.setVisibility(View.VISIBLE);
        }

    }
}

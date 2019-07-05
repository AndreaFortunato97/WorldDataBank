package it.apperol.group.worlddatabank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

import it.apperol.group.worlddatabank.myactivities.FullImageActivity;
import it.apperol.group.worlddatabank.myactivities.GalleryActivity;

public class GalleryFragment extends Fragment {

    private View myView;
    private ArrayList<File> list;
    private GridView gridView;
    private File file;
    boolean success = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.gallery_layout, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.image_grid);

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + "ChartGallery"+"/");

        if (!folder.exists())
        {
            success = folder.mkdirs();
        }

        if (success)
        {
            file = new File(String.valueOf(folder));
            ;           }
        else
        {
            File folder2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + "ChartGallery"+"/");
            file = new File(String.valueOf(folder2));
        }

        list = imageReader(file);

        gridView.setAdapter(new gridAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                openActivity(i);
            }
        });
    }

    public class gridAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View convertView = null;

            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.image_preview,viewGroup,false);
                ImageView myImage = (ImageView) convertView.findViewById(R.id.myImagePreview);
                myImage.setImageURI(Uri.parse(list.get(i).toString()));

            }
            return convertView;
        }
    }


    private ArrayList<File> imageReader(File exstarnalStorageDirectory)
    {
        ArrayList<File> b = new ArrayList<>();
        File[] files = exstarnalStorageDirectory.listFiles();
        for (int i =0; i<files.length;i++)
        {
            if (files[i].isDirectory() )
            {
                b.addAll(imageReader(files[i]));
            }
            else
            {
                if (files[i].getName().endsWith(".png"))
                {
                    b.add(files[i]);
                }
            }
        }

        return b;
    }

    private void openActivity(int i) {
        Intent intent = new Intent(MainActivity.mainActivityContext, FullImageActivity.class);
        intent.putExtra("png",list.get(i).toString());
        startActivity(intent);
    }
}

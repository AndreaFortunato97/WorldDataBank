package it.apperol.group.worlddatabank;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

import it.apperol.group.worlddatabank.myactivities.FullImageActivity;
import it.apperol.group.worlddatabank.myactivities.GalleryActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class GalleryFragment extends Fragment {

    private View myView;
    private ArrayList<File> list;
    private GridView gridView;
    private File file;
    boolean success = true;

    private ArrayList permissions = new ArrayList();
    private ArrayList permissionsToRequest;
    private SaveShareDialog saveShareDialog = new SaveShareDialog();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.gallery_layout, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        askPermissions();

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

    private void askPermissions() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if(!permissionsToRequest.isEmpty()) {
            Toast.makeText(getContext(), getResources().getString(R.string.grantPerm), Toast.LENGTH_LONG).show();
            requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 777);
        }

    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        Boolean hasMarshmallow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (hasMarshmallow) {
            return (checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

            if (shouldShowRequestPermissionRationale(permissions[0])) {
                Toast.makeText(getContext(), R.string.grantPerm, Toast.LENGTH_LONG).show();
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 777);

            } else {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permError)
                        .setMessage(R.string.permDenied)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                navigationView.getMenu().getItem(0).setChecked(true);

                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, new WelcomeFragment()).commit();
                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                navigationView.getMenu().getItem(0).setChecked(true);

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
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
        if(files != null) {
            getActivity().findViewById(R.id.image_grid).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.no_images).setVisibility(View.INVISIBLE);

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    b.addAll(imageReader(files[i]));
                } else {
                    if (files[i].getName().endsWith(".png")) {
                        b.add(files[i]);
                    }
                }
            }
        } else {
            getActivity().findViewById(R.id.image_grid).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.no_images).setVisibility(View.VISIBLE);
        }

        return b;
    }

    private void openActivity(int i) {
        Intent intent = new Intent(MainActivity.mainActivityContext, FullImageActivity.class);
        intent.putExtra("png",list.get(i).toString());
        startActivity(intent);
    }
}

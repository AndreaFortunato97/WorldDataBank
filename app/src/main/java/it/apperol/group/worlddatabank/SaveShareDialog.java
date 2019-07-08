package it.apperol.group.worlddatabank;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.apperol.group.worlddatabank.myactivities.PlotActivity;
import it.apperol.group.worlddatabank.myadapters.MyCountryAdapter;
import it.apperol.group.worlddatabank.myadapters.MyIndicatorAdapter;
import it.apperol.group.worlddatabank.myadapters.MyTopicAdapter;

public class SaveShareDialog extends DialogFragment implements View.OnClickListener {

    private MaterialButton mbSaveData, mbSave, mbShare, mbCancel;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.save_share_layout, null, false);

        builder.setView(view);
        mbSaveData = view.findViewById(R.id.mbSaveData);
        mbSave = view.findViewById(R.id.mbSave);
        mbShare = view.findViewById(R.id.mbShare);
        mbCancel = view.findViewById(R.id.mbCancel);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            mbSaveData.setTextColor(getResources().getColor(R.color.textColorDark));
            mbSave.setTextColor(getResources().getColor(R.color.textColorDark));
            mbShare.setTextColor(getResources().getColor(R.color.textColorDark));
            mbCancel.setTextColor(getResources().getColor(R.color.textColorDark));
        }

        // 2. set click listeners on desired views
        mbSaveData.setOnClickListener(this);
        mbSave.setOnClickListener(this);
        mbShare.setOnClickListener(this);
        mbCancel.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mbSaveData:
                saveData(PlotActivity.ja.toString());
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.data_saved), Snackbar.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.mbSave:
                createFolder("ChartGallery");
                final File file = saveImage(PlotActivity.mpLineChart, "ChartGallery", false);

                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.chart_saved), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.delete), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AsyncTask<Void, Void, Void>(){
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        String filePath = file.getAbsolutePath();
                                        file.delete();
                                        MediaScannerConnection.scanFile(PlotActivity.plotActivityContext,
                                                new String[]{filePath}, null, null);
                                        return null;
                                    }
                                }.execute();

                                Toast.makeText(v.getContext(), getResources().getString(R.string.chart_image_deleted), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                dismiss();
                break;
            case R.id.mbShare:
                createFolder(".tmpChart");
                shareToWhatsapp();
                dismiss();
                break;
            case R.id.mbCancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void saveData(String data) {
        File path = Objects.requireNonNull(getActivity()).getApplicationContext().getFilesDir();
        File file = new File(path, MyCountryAdapter.countryName + "-" + MyTopicAdapter.topicName + "-" + MyIndicatorAdapter.indicatorName + ".txt");
        if(file.exists()) {
            file.delete();
        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.getChannel().truncate(0);
            stream.getChannel().force(true);

            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFolder(String folderName) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + folderName + "/");

        if (!folder.exists()) {
            if(!folder.mkdirs()) {
                Objects.requireNonNull(getActivity()).finish();
            }
        }
    }

    private void shareToWhatsapp() {
        String pack = "com.whatsapp";
        File file = saveImage(PlotActivity.mpLineChart,".tmpChart", true);
        try {


            Uri imageUri = Uri.parse(file.getAbsolutePath());

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/png");
            waIntent.setPackage(pack);
            waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            waIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.chart_for) + MyIndicatorAdapter.indicatorName + getString(R.string.of_country_apostrophe) + MyCountryAdapter.countryName + "'");
            PlotActivity.plotActivityContext.startActivity(Intent.createChooser(waIntent, getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            Toast.makeText(PlotActivity.plotActivityContext, getResources().getString(R.string.no_whatsapp), Toast.LENGTH_SHORT).show();
        }
    }

    private File saveImage(LineChart chart, String folderName, Boolean tempSave)
    {
        Bitmap finalBitmap;
        int width = chart.getWidth();
        int height = chart.getHeight();
        Bitmap cBitmap = chart.getChartBitmap();
        finalBitmap = Bitmap.createScaledBitmap(cBitmap, width, height, true);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + folderName + "/";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
        Date date = new Date();
        String dateTime = "<" + sdf.format(date) + ">";
        String fname;

        if(tempSave) {
            fname = System.currentTimeMillis() + ".png";
        } else {
            fname = dateTime + MyCountryAdapter.countryIso2Code.toUpperCase() + "-" + MyIndicatorAdapter.indicatorName + ".png";
        }
        File file = new File(path, fname);
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }
}

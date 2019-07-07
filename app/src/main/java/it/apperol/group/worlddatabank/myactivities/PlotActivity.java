package it.apperol.group.worlddatabank.myactivities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.apperol.group.worlddatabank.MainActivity;
import it.apperol.group.worlddatabank.OfflineFragment;
import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.SaveShareDialog;
import it.apperol.group.worlddatabank.WelcomeFragment;
import it.apperol.group.worlddatabank.myadapters.MyCountryAdapter;
import it.apperol.group.worlddatabank.myadapters.MyIndicatorAdapter;
import it.apperol.group.worlddatabank.myadapters.MyTopicAdapter;
import it.apperol.group.worlddatabank.myadapters.OfflineAdapter;
import it.apperol.group.worlddatabank.myobjects.PlotObj;
import it.apperol.group.worlddatabank.mythreads.FetchData;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PlotActivity extends AppCompatActivity {

    private String[] currentFileName;

    public static Context plotActivityContext;

    private static List<PlotObj> plotDatas = new ArrayList<>();
    public static LineChart mpLineChart;
    static int[] colorArray = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    public static JSONArray ja;
    private static ArrayList<Entry> dataVals;

    private ArrayList permissions = new ArrayList();
    private ArrayList permissionsToRequest;

    private SaveShareDialog saveShareDialog = new SaveShareDialog();
    private android.app.AlertDialog.Builder noDataFoundDialog;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.MainAppThemeDark);
        } else {
            setTheme(R.style.MainAppTheme);
        }

        super.onCreate(savedInstanceState);

        plotActivityContext = this;

        if(WelcomeFragment.count == 0)
            ja = MyIndicatorAdapter.ja;
        else if(WelcomeFragment.count == 1)
            ja = MyCountryAdapter.ja;
        else if(WelcomeFragment.count == 2){
            ja = OfflineAdapter.ja;
        }

        setContentView(R.layout.activity_plot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(WelcomeFragment.count == 2) {
            fab.setVisibility(View.INVISIBLE);
            currentFileName = OfflineAdapter.currentFileName.split("-");
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    askPermissions();

                    saveShareDialog.show(getSupportFragmentManager(), "mySaveShareDialog");
                }
            });
        }

        mpLineChart = (LineChart) findViewById(R.id.line_chart);

        fetchPlot();

    }

    private void askPermissions() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if(!permissionsToRequest.isEmpty()) {
            Toast.makeText(this, getString(R.string.grantPerm), Toast.LENGTH_LONG).show();
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
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

            if (shouldShowRequestPermissionRationale(permissions[0])) {
                Toast.makeText(this, R.string.grantPerm, Toast.LENGTH_LONG).show();
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 777);

            } else {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.permError)
                        .setMessage(R.string.permDenied)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saveShareDialog.dismiss();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                saveShareDialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    private static ArrayList<Entry> dataValues1()
    {
        getDatas();
        if(ja == null) {
            return null;
        }

        // Riordino i dati secondo l'anno (ordine crescente)
        Collections.sort(plotDatas, new Comparator<PlotObj>() {
            @Override
            public int compare(PlotObj o1, PlotObj o2) {
                return o1.getYear().compareToIgnoreCase(o2.getYear());
            }
        });

        ArrayList<Entry> dataVals = new ArrayList<>();
        for(int i = 0; i < plotDatas.size(); i++) {
            dataVals.add(new Entry(Float.parseFloat(plotDatas.get(i).getYear()), Float.parseFloat(plotDatas.get(i).getValue())));
        }

        return dataVals;
    }

    @Override
    protected void onPause() {
        super.onPause();
        plotDatas = new ArrayList<>();
    }

    @Override
    protected void onStop() {
        super.onStop();
        plotDatas = new ArrayList<>();
    }

    private static void getDatas() {

        try {
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                JSONObject jo = (JSONObject) ja.getJSONArray(1).get(i);
                if(!jo.get("value").equals("") && jo.get("value") != null && !jo.get("value").equals("null") && !jo.isNull("value")) {
                    plotDatas.add(new PlotObj(jo.get("date").toString(), jo.get("value").toString()));
                }
            }
            if(plotDatas.size() == 0) {
                ja = null;

                return;
            }
        }catch (Exception e){
            Log.i("[CRASH]", "CRASH");
            e.printStackTrace();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        File tmpFolderToDelete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/.tmpChart/");

        if(tmpFolderToDelete.exists()) {
            deleteTempFolderRecursive(tmpFolderToDelete);
        }
    }

    private void deleteTempFolderRecursive(File tmpFolderToDelete) {
        if (tmpFolderToDelete.isDirectory()) {
            for (File filesInDir : tmpFolderToDelete.listFiles()) {
                deleteTempFolderRecursive(filesInDir);
            }
        }
        tmpFolderToDelete.delete();
    }

    private void fetchPlot() {

        dataVals = dataValues1();
        if(dataVals == null) {

            if(WelcomeFragment.count == 0) {
                noDataFoundDialog = new android.app.AlertDialog.Builder(IndicatorActivity.indicatorActivityContext);
            } else {
                noDataFoundDialog = new android.app.AlertDialog.Builder(CountryActivity.countryActivityContext);

            }
            noDataFoundDialog.setTitle(getResources().getString(R.string.error));
            noDataFoundDialog.setMessage(getResources().getString(R.string.indicator_not_available));
            noDataFoundDialog.setCancelable(false);
            noDataFoundDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.app.AlertDialog alert = noDataFoundDialog.create();
            alert.show();
            finish();
        }
        LineDataSet lineDataSet1;
        if(WelcomeFragment.count == 2) {
            lineDataSet1 = new LineDataSet(dataVals, "Indicator: " + currentFileName[2]);
        } else {
            lineDataSet1 = new LineDataSet(dataVals, "Indicator: " + MyIndicatorAdapter.indicatorName);
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();    // insieme di tutti i dati
        dataSets.add(lineDataSet1);         //aggiungo dati 1

        mpLineChart.setNoDataText("No Data");        // NO DATA se non ci sono valori
        mpLineChart.setNoDataTextColor(Color.BLUE);  // Colore di No Data

        mpLineChart.setDrawGridBackground(true);
        mpLineChart.setDrawBorders(true);            // Bordo Grafico
        mpLineChart.setBorderColor(Color.RED);       // Colore Bordo Grafico
        mpLineChart.setBorderWidth(1);              // spessore bordo
        lineDataSet1.setColor(Color.RED);            // Colore di linea 1
        lineDataSet1.setValueTextSize(10);

        Legend legend = mpLineChart.getLegend();      //legenda del grafico
        legend.setEnabled(true);
        legend.setTextColor(Color.RED);
        legend.setTextSize(10);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10);
        legend.setXEntrySpace(5);                     //spazio tra 2 text nella legenda
        legend.setFormToTextSpace(10);                  //spazio tra icona e testo*/
        legend.setWordWrapEnabled(true);

        mpLineChart.setExtraTopOffset(10f);
        mpLineChart.setExtraRightOffset(10f);
        mpLineChart.setExtraBottomOffset(10f);
        mpLineChart.setExtraLeftOffset(10f);

        LegendEntry[] legendEntries = new LegendEntry[4];

        for (int i=0; i<legendEntries.length; i++)
        {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colorArray[i];
            //entry.label = String.valueOf(legendName[i]);
            legendEntries[i] = entry;

        }

        //legend.setCustom(legendEntries);     //Customizzare la legenda con legendEntries

        Description description = new Description();       //Descrizione da aggiungere sul grafico
        if(WelcomeFragment.count == 2) {
            description.setText(currentFileName[1] + " of country " + currentFileName[0]);
        } else {
            description.setText(MyTopicAdapter.topicName + " of country " + MyCountryAdapter.countryName);
        }
        description.setTextColor(Color.RED);
        description.setTextSize(10);
        mpLineChart.setDescription(description);             //rende visibile la descrizione sul grafico

        // Imposta con che grana verranno mostrati gli anni ('1f' corrissponde a: di anno in anno, '2f' un anno si ed uno no, '3f' un anno si e tre no, ecc...)
        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(true);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            mpLineChart.setGridBackgroundColor(Color.BLACK);
            lineDataSet1.setValueTextColor(Color.WHITE);
            xAxis.setTextColor(Color.WHITE);
            mpLineChart.getAxisLeft().setTextColor(Color.WHITE);
            mpLineChart.getAxisRight().setTextColor(Color.WHITE);
            mpLineChart.setBackgroundColor(getResources().getColor(R.color.backgroundDark));
        }


        LineData data = new LineData(dataSets);    //grafio a linee
        mpLineChart.setData(data);                //imposta dati nel grafico
    }
}

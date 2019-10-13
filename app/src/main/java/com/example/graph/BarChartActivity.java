package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class BarChartActivity extends AppCompatActivity {

    final static int SENSORS = 4;
    Button reget;
    BarChart chart;
    BarDataSet barSet;
    BarData data;
    Random losowanie = new Random();
    ArrayList<IBarDataSet> dataSets = null;
    Description dd = new Description();

    ArrayList<BarEntry> slupki = new ArrayList<BarEntry>();
    ArrayList<String> sensorListLabel = new ArrayList<String>();
    ArrayList<Integer> sensorListColor = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        reget = (Button) findViewById(R.id.nowe);
        chart = (BarChart) findViewById(R.id.barchart);

        iniBarChart();

        reget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.INFO,"AGRORUN","Updating bar chart data");
                updateBarChartData();
                barSet.notifyDataSetChanged();
                data.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
            }
        });
    }

    private void iniBarChart() {
        for (int i = 0 ; i < SENSORS ; i++) sensorListLabel.add("Czujnik "+(i+1));

        // ustawienie osi X - labelek
        chart.getXAxis().setGranularity(1);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sensorListLabel));
        chart.getXAxis().setTextSize(14);
        chart.setExtraOffsets(10, 10, 10, 10);

        // Konfiguracja osi Y
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setAxisMaximum(100);
        chart.getAxisRight().setAxisMinimum(0);
        chart.getAxisRight().setAxisMaximum(1200);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setLabelCount(12);
        chart.getAxisLeft().setLabelCount(10);
        chart.getAxisLeft().setTextSize(18);
        chart.getAxisRight().setTextSize(18);
        chart.setDrawValueAboveBar(false);
        chart.setTouchEnabled(false);

        updateBarChartData();

        // ustawienie osi Y - danych wykresu
        barSet = new BarDataSet(slupki, "Lista podłączonych czujników");
        barSet.setColors(sensorListColor);

        dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barSet);

        // wyswietlenie danyxh
        data = new BarData(dataSets);
        data.setValueTextSize(25f);
        data.setHighlightEnabled(false);
        data.setBarWidth(0.9f);
        data.setDrawValues(true);

        chart.setData(data);
    }

    private void updateBarChartData(){
  //      sensorListLabel.clear();
        sensorListColor.clear();
        slupki.clear();
        int val = 0;
        int connectedSensor = 0;
        for (int i = 0 ; i < SENSORS ; i++) {
            // decoded sensor message as array
            val = generujemy();
            // this check is to detect sensor presence only
            if (val > 5) {
                slupki.add(new BarEntry(connectedSensor, val));
                sensorListColor.add(calculateColor(val));
                connectedSensor++;
            }

            dd.setText("Brak podłączonych czujników");
            chart.getDescription().setTextSize(18);
            chart.getDescription().setTextColor(Color.RED);
            chart.setDescription(dd);
            chart.getDescription().setEnabled(slupki.isEmpty());
            chart.getXAxis().setDrawLabels(!slupki.isEmpty());
        }
    }

    private int generujemy(){
        return (int) losowanie.nextInt(100-5) + 5;
    }

    private int calculateColor(int sensor_value) {
        int color = Color.TRANSPARENT;
        if(sensor_value < 10){
            color = Color.RED;
        } else if (sensor_value >= 10 && sensor_value < 30) {
            color = Color.YELLOW;
        } else if (sensor_value >= 30 && sensor_value < 50) {
            color = Color.rgb(0x5a, 0xf0, 0x38);
        } else if (sensor_value >= 50) {
            color = Color.rgb(0x1e, 0x7b, 0x09);
        }
        return color;
    }
}
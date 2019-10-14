package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Random;

/*
                              EtykietaX<String>
                                    |
Entry           DataSet             |               ViewGroup
  BarEntry ------> BarDataSet ------+--> BarData ------> BarChart

Surowe dane, ktore chcemy wyswietlic na danym wykresie musza byc reprezentowane przez obiekty klasy
Entry. Dla wykresu typu BarChart będą to odpowiednio BarEntry (podklasa Entry zapewne).

Tworzac liste BarEntry musimy stworzyć dla niej liste etykiet opisujących dane na osi X.
Do tego celu możemy stworzyć np liste o typie String z daną ilością etykiet. * w zależności od
tego jak rozwiążemy temat tworzenia tej listy osiągniemy różne rezultaty - np statyczna lista przy
dynamicznym wykresie bedzie zawsze wyswietlac etykiety w kolejnosci od 0 do N dla kolejnych danych.
Z kolei jesli taka lista etykiet bedzie tworzona dynamicznie wraz ze zmieniajaca sie
liczba elementow Entry wowczas bedziemy mieli mozliwosc wyswietlania danych odpowiednio
skorelowanych z Entry. Przyklad poniżej zawiera rozwiazanie statyczne.
Zeby bylo dynamiczne nalezy linie:
for (int i = 0 ; i < SENSORS ; i++) sensorListLabel.add("Czujnik "+(i+1));
przeniesc do petli w funkcji updateBarChartData (odpowiednio przerabiajac liczniki).

Dane w listy w postaci BarEntry wykożystujemy do stworzenia serii danych BarDAtaSet.

Z kolei BarDataSet jest używana do tworzenia danych wykresu BarData.

Te z kolei są bezpośrednio wykożystywane przez instancje wykresy BarChart.
*/


public class BarChartActivity extends AppCompatActivity {

    final static int SENSORS = 4;
    Button reget;
    BarChart wykresbc;
    BarDataSet slupkiDanychJednaSeria;
    BarData slupkiDanych;
    Random losowanie = new Random();
    Description opisWykresu = new Description();

    ArrayList<BarEntry> listaSurowychDanych = new ArrayList<BarEntry>();
    ArrayList<String> opisLabelkiOsiX = new ArrayList<String>();
    ArrayList<Integer> kolorDanychBarData = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        reget = (Button) findViewById(R.id.nowe);
        wykresbc = (BarChart) findViewById(R.id.barchart);

        iniBarChart();

        reget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.INFO,"AGRORUN","Updating bar chart data");
                updateBarChartData();
                slupkiDanychJednaSeria.notifyDataSetChanged();
                slupkiDanych.notifyDataChanged();
                wykresbc.notifyDataSetChanged();
                wykresbc.invalidate();
            }
        });
    }

    private void iniBarChart() {
        for (int i = 0 ; i < SENSORS ; i++) opisLabelkiOsiX.add("Czujnik "+(i+1));

        // ustawienie osi X - labelek
        wykresbc.getXAxis().setGranularity(1);
        wykresbc.getXAxis().setGranularityEnabled(true);
        wykresbc.getXAxis().setValueFormatter(new IndexAxisValueFormatter(opisLabelkiOsiX));
        wykresbc.getXAxis().setTextSize(14);
        wykresbc.setExtraOffsets(10, 10, 10, 10);

        // Konfiguracja osi Y
        wykresbc.getAxisLeft().setAxisMinimum(0);
        wykresbc.getAxisLeft().setAxisMaximum((int)100);
        wykresbc.getAxisRight().setAxisMinimum(0);
        wykresbc.getAxisRight().setAxisMaximum(1200);
        wykresbc.getAxisRight().setDrawGridLines(false);
        wykresbc.getAxisRight().setLabelCount(6);
        wykresbc.getAxisLeft().setLabelCount(5);
        wykresbc.getAxisLeft().setTextSize(18);
        wykresbc.getAxisRight().setTextSize(18);
        wykresbc.setDrawValueAboveBar(false);
        wykresbc.setTouchEnabled(false);

        updateBarChartData();

        // ustawienie osi Y - danych wykresu
        slupkiDanychJednaSeria = new BarDataSet(listaSurowychDanych, "Lista podłączonych czujników");
        slupkiDanychJednaSeria.setColors(kolorDanychBarData);

        // wyswietlenie danyxh
        slupkiDanych = new BarData(slupkiDanychJednaSeria);
        slupkiDanych.setValueTextSize(25f);
        slupkiDanych.setHighlightEnabled(false);
        slupkiDanych.setBarWidth(0.9f);
        slupkiDanych.setDrawValues(true);

        wykresbc.setData(slupkiDanych);
    }

    private void updateBarChartData(){
  //      sensorListLabel.clear();
        kolorDanychBarData.clear();
        listaSurowychDanych.clear();
        int val = 0;
        int connectedSensor = 0;
        for (int i = 0 ; i < SENSORS ; i++) {
            // decoded sensor message as array
            val = generujemy();
            // this check is to detect sensor presence only
            if (val > 5) {
                listaSurowychDanych.add(new BarEntry(connectedSensor, val));
                kolorDanychBarData.add(calculateColor(val));
                connectedSensor++;
            }

            opisWykresu.setText("Brak podłączonych czujników");
            wykresbc.getDescription().setTextSize(18);
            wykresbc.getDescription().setTextColor(Color.RED);
            wykresbc.setDescription(opisWykresu);
            wykresbc.getDescription().setEnabled(listaSurowychDanych.isEmpty());
            wykresbc.getXAxis().setDrawLabels(!listaSurowychDanych.isEmpty());
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
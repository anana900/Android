package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.github.mikephil.charting.charts.BarChart;

public class MainActivity extends AppCompatActivity {
        Button btnBarChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            BarChart barChart = (BarChart) findViewById(R.id.barchart);

            btnBarChart = findViewById(R.id.btnBarChart);
            btnBarChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent I = new Intent(MainActivity.this, BarChartActivity.class);
                    startActivity(I);
                }
            });
        }
}

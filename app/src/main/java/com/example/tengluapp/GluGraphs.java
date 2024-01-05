package com.example.tengluapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GluGraphs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glu_graphs);
        getSupportActionBar().setTitle("Graph du Glucometre");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) GraphView graph = (GraphView) findViewById(R.id.graphGlu);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TenGluApp");
        File file = new File(dir, "listGResults.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                Date date = dateFormat.parse(values[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hourInt = calendar.get(Calendar.HOUR_OF_DAY);

                double TDG = Double.parseDouble(values[2]);

                // Use value1 and value2 to calculate the y value
                series.appendData(new DataPoint(hourInt, TDG), true, 90);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        graph.addSeries(series);
    }
}
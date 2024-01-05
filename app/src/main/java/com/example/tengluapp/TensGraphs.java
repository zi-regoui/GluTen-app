package com.example.tengluapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

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

public class TensGraphs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tens_graphs);
        getSupportActionBar().setTitle("Resultats du Tensiometre");

        GraphView graphSYS = (GraphView) findViewById(R.id.graphTensSYS);
        GraphView graphDIA = (GraphView) findViewById(R.id.graphTensDIA);

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TenGluApp");
        File file = new File(dir, "listTResults.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            //int x = 0;
            while ((line = reader.readLine()) != null) {
                Log.e("blaaaablaaablaaa", "onCreate: "+line );
                String[] values = line.split(",");

                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                Date date = dateFormat.parse(values[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hourInt = calendar.get(Calendar.HOUR_OF_DAY);

                double SYS = Double.parseDouble(values[2]);
                double DIA = Double.parseDouble(values[3]);
                // Use value1 and value2 to calculate the y value
//                double y = TDG;

                series1.appendData(new DataPoint(hourInt, DIA), true, 90);
                series2.appendData(new DataPoint(hourInt, SYS), true, 90);
                //x++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        graphSYS.addSeries(series1);
        graphDIA.addSeries(series2);
    }
}
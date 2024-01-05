package com.example.tengluapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListGluResults extends AppCompatActivity {

    RecyclerView recyclerView;
    Button Graph;
    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TenGluApp");

    private List<MyDataModelGlu> readDataFromFile() {
        List<MyDataModelGlu> data = new ArrayList<>();
        File file = new File(dir, "listGResults.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("blablabla", "readDataFromFile: "+line.toString());
                String[] values = line.split(",");
                String date = values[0];
                String hour = values[1];
                String TDG = values[2];
                data.add(new MyDataModelGlu(date, hour, TDG));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_glu_results);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Resultats du Gluco Metre");

        Graph = findViewById(R.id.draw_graph_Glu);
        Graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent( ListGluResults.this, GluGraphs.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view_Glu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MyDataModelGlu> dataModels = readDataFromFile();

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(new TableAdapterGlu(dataModels));


    }
}
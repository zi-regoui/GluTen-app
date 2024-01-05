package com.example.tengluapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class ListTensResults extends AppCompatActivity {
    RecyclerView recyclerView;
    Button Graph;
    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TenGluApp");

    private List<MyDataModel> readDataFromFile() {
        List<MyDataModel> data = new ArrayList<>();
        File file = new File(dir, "listTResults.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String date = values[0];
                String hour = values[1];
                String SYS = values[2];
                String DIA = values[3];
                data.add(new MyDataModel(date, hour, SYS, DIA));
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
        setContentView(R.layout.activity_list_tens_results);
        getSupportActionBar().setTitle("Resultats du Tensiometre");

        Graph = findViewById(R.id.draw_graph_Tens);
        Graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent( ListTensResults.this, TensGraphs.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view_Tens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MyDataModel> dataModels = readDataFromFile();

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(new TableAdapter(dataModels));

    }
}

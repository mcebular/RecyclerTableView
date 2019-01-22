package com.mc0239.recyclertableviewexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;

import com.mc0239.recyclertableview.RecyclerTableView;
import com.mc0239.recyclertableview.RecyclerTableViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter recyclerTableViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare some data for the table
        ArrayList<SparseArray<Object>> data = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            SparseArray<Object> s = new SparseArray<>();
            s.put(R.id.checkBoxSelected, false);
            s.put(R.id.textViewID, i);
            s.put(R.id.textViewUsername, "johnd");
            s.put(R.id.textViewName, "John");
            s.put(R.id.textViewSurname, "Doe");
            data.add(s);
        }

        // Get RecyclerTableView view
        recyclerTableView = findViewById(R.id.recyclerTableView1);

        // Set header of the table
        recyclerTableView.setHeaderResource(R.layout.table_row_sample1, R.color.colorPrimary, R.color.colorWhite);

        // Set TableView checkable by providing ID of the checkbox view
        recyclerTableView.setCheckable(R.id.checkBoxSelected);

        // Set LayoutManager
        recyclerTableView.setLayoutManager(new LinearLayoutManager(this));

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter(
                data,
                R.layout.table_row_sample1,
                new int[]{
                        R.id.checkBoxSelected,
                        R.id.textViewID,
                        R.id.textViewUsername,
                        R.id.textViewName,
                        R.id.textViewSurname
                },
                R.id.checkBoxSelected);

        // Set adapter to RecyclerTableView
        recyclerTableView.setAdapter(recyclerTableViewAdapter);

    }
}

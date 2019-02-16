package com.mc0239.recyclertableviewexample.samples;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mc0239.recyclertableview.RecyclerTableView;
import com.mc0239.recyclertableview.RecyclerTableViewAdapter;
import com.mc0239.recyclertableviewexample.R;

import java.util.ArrayList;

public class FragmentSampleUsage extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter recyclerTableViewAdapter;

    public FragmentSampleUsage() {
        // Prepare some data for the table
        ArrayList<SparseArray<Object>> data = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            SparseArray<Object> s = new SparseArray<>();
            s.put(R.id.textViewID, i);
            s.put(R.id.textViewUsername, "johnd");
            s.put(R.id.textViewName, "John");
            s.put(R.id.textViewSurname, "Doe " + i);
            data.add(s);
        }

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter(
                data,
                R.layout.table_row_sample_usage,
                new int[]{
                        R.id.textViewID,
                        R.id.textViewUsername,
                        R.id.textViewName,
                        R.id.textViewSurname
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);

        // Get RecyclerTableView view
        recyclerTableView = view.findViewById(R.id.recyclerTableView1);

        // Set adapter to RecyclerTableView
        recyclerTableView.setAdapter(recyclerTableViewAdapter);

        return view;
    }

}

package com.mc0239.recyclertableviewexample.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mc0239.recyclertableview.RecyclerTableView;
import com.mc0239.recyclertableview.RecyclerTableViewAdapter;
import com.mc0239.recyclertableviewexample.R;
import com.mc0239.recyclertableviewexample.rows.UserCheckable;

import java.util.ArrayList;

public class FragmentSampleInteractive extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter<UserCheckable> recyclerTableViewAdapter;

    private Button buttonAdd;
    private Button buttonRemove;

    public FragmentSampleInteractive() {
        // Prepare some data for the table
        ArrayList<UserCheckable> users = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            UserCheckable u = new UserCheckable();
            u.checked = i % 3 == 0;
            u.id = i;
            u.username = "johnd";
            u.name = "John";
            u.surname = "Doe " + i;
            users.add(u);
        }

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter<>(UserCheckable.class, users);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample_interactive, container, false);

        // Get RecyclerTableView view
        recyclerTableView = view.findViewById(R.id.recyclerTableView1);

        // Set adapter to RecyclerTableView
        recyclerTableView.setAdapter(recyclerTableViewAdapter);

        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonRemove = view.findViewById(R.id.buttonRemove);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCheckable u = new UserCheckable();
                u.checked = false;
                u.id = recyclerTableViewAdapter.getItemCount() + 1;
                u.username = "johna";
                u.name = "John";
                u.surname = "Added";
                recyclerTableViewAdapter.addItem(u);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTableViewAdapter.removeItems(recyclerTableViewAdapter.getCheckedItems());
            }
        });

        return view;
    }

}

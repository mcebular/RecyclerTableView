package com.mc0239.recyclertableviewexample.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mc0239.recyclertableview.RecyclerTableView;
import com.mc0239.recyclertableview.RecyclerTableViewAdapter;
import com.mc0239.recyclertableviewexample.R;
import com.mc0239.recyclertableviewexample.rows.UserCheckableSingle;

import java.util.ArrayList;

public class FragmentSampleRadiobutton extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter recyclerTableViewAdapter;

    public FragmentSampleRadiobutton() {
        // Prepare some data for the table
        ArrayList<UserCheckableSingle> users = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            UserCheckableSingle u = new UserCheckableSingle();
            u.checked = i == 0;
            u.id = i;
            u.username = "johnd";
            u.name = "John";
            u.surname = "Doe " + i;
            users.add(u);
        }

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter(UserCheckableSingle.class, users);
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

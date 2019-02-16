package com.mc0239.recyclertableviewexample.samples;

import android.arch.lifecycle.Observer;
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
import com.mc0239.recyclertableviewexample.database.AppDatabase;
import com.mc0239.recyclertableviewexample.database.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentSampleLivedata extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter recyclerTableViewAdapter;

    public FragmentSampleLivedata() {

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter(
                null,
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

        // Add LiveData observer
        AppDatabase.getDatabase(getContext()).userDao().getAll().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users == null) return;

                ArrayList<SparseArray<Object>> data = new ArrayList<>();
                for(User u : users) {
                    SparseArray<Object> s = new SparseArray<>();
                    s.put(R.id.textViewID, u.id);
                    s.put(R.id.textViewUsername, u.username);
                    s.put(R.id.textViewName, u.name);
                    s.put(R.id.textViewSurname, u.surname);
                    data.add(s);
                }
                recyclerTableViewAdapter.setRows(data);
            }
        });

        return view;
    }

}

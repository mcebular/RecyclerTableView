package com.mc0239.recyclertableviewexample.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.mc0239.recyclertableview.RecyclerTableView;
import com.mc0239.recyclertableview.RecyclerTableViewAdapter;
import com.mc0239.recyclertableviewexample.R;
import com.mc0239.recyclertableviewexample.database.AppDatabase;
import com.mc0239.recyclertableviewexample.database.User;

import java.util.List;

public class FragmentSampleLivedata extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter recyclerTableViewAdapter;

    public FragmentSampleLivedata() {

        // Create a new RecyclerTableViewAdapter
        recyclerTableViewAdapter = new RecyclerTableViewAdapter(User.class, null);
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
                recyclerTableViewAdapter.setItems(users);
            }
        });

        return view;
    }

}

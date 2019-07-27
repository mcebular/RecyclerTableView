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
import com.mc0239.recyclertableviewexample.SampleUserGenerator;
import com.mc0239.recyclertableviewexample.rows.UserCheckable;

import java.util.ArrayList;
import java.util.List;

public class FragmentSampleInteractive extends Fragment {

    private RecyclerTableView recyclerTableView;
    private RecyclerTableViewAdapter<UserCheckable> recyclerTableViewAdapter;

    private Button buttonAdd;
    private Button buttonAddAt;
    private Button buttonAddManyAt;
    private Button buttonRemove;

    private UserCheckable lastUser;
    private List<UserCheckable> lastUsers;

    public FragmentSampleInteractive() {
        // Prepare some data for the table
        ArrayList<UserCheckable> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            UserCheckable u = SampleUserGenerator.generateUser();
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

        view.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastUser = SampleUserGenerator.generateUser();
                recyclerTableViewAdapter.addItem(lastUser);
            }
        });

        view.findViewById(R.id.buttonAddAt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastUser = SampleUserGenerator.generateUser();
                recyclerTableViewAdapter.addItemAt(5, lastUser);
            }
        });

        view.findViewById(R.id.buttonAddMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastUsers = new ArrayList<>(3);
                lastUsers.add(SampleUserGenerator.generateUser());
                lastUsers.add(SampleUserGenerator.generateUser());
                lastUsers.add(SampleUserGenerator.generateUser());
                recyclerTableViewAdapter.addItems(lastUsers);
            }
        });

        view.findViewById(R.id.buttonAddMoreAt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastUsers = new ArrayList<>(3);
                lastUsers.add(SampleUserGenerator.generateUser());
                lastUsers.add(SampleUserGenerator.generateUser());
                lastUsers.add(SampleUserGenerator.generateUser());
                recyclerTableViewAdapter.addItemsAt(5, lastUsers);
            }
        });

        view.findViewById(R.id.buttonRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastUser != null)
                    recyclerTableViewAdapter.removeItem(lastUser);
                lastUser = null;
            }
        });

        view.findViewById(R.id.buttonRemoveAt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTableViewAdapter.getItemCount() > 5)
                    recyclerTableViewAdapter.removeItemAt(5);
            }
        });

        view.findViewById(R.id.buttonRemoveMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastUsers != null)
                    recyclerTableViewAdapter.removeItems(lastUsers);
                lastUsers = null;
            }
        });

        view.findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTableViewAdapter.clearItems();
            }
        });

        return view;
    }

}

package com.mc0239.recyclertableviewexample.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mc0239.recyclertableview.RecyclerTableViewAdapter
import com.mc0239.recyclertableviewexample.databinding.FragmentSampleInteractiveBinding
import com.mc0239.recyclertableviewexample.rows.UserCheckable
import com.mc0239.recyclertableviewexample.util.SampleUserGenerator

class FragmentSampleInteractive : Fragment() {

    private lateinit var binding: FragmentSampleInteractiveBinding

    private val recyclerTableViewAdapter: RecyclerTableViewAdapter<UserCheckable>

    init {
        // Prepare some data for the table
        val users = mutableListOf<UserCheckable>()
        // Stress-test: generate 10 000 entries!!
        for (i in 0 until 10_000) {
            users.add(SampleUserGenerator.generateUser())
        }

        recyclerTableViewAdapter = RecyclerTableViewAdapter(UserCheckable::class.java, users)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSampleInteractiveBinding.inflate(inflater, container, false)

        binding.recyclerTableView1.setAdapter(recyclerTableViewAdapter)

        binding.buttonAdd.setOnClickListener {
            recyclerTableViewAdapter.addItem(SampleUserGenerator.generateUser())
        }

        binding.buttonAddAt.setOnClickListener {
            recyclerTableViewAdapter.addItemAt(minOf(recyclerTableViewAdapter.itemCount, 5), SampleUserGenerator.generateUser())
        }

        binding.buttonAddMore.setOnClickListener {
            recyclerTableViewAdapter.addItems((1 until 100).map { SampleUserGenerator.generateUser() })
        }

        binding.buttonAddMoreAt.setOnClickListener {
            recyclerTableViewAdapter.addItemsAt(minOf(recyclerTableViewAdapter.itemCount, 5), (1 until 100).map { SampleUserGenerator.generateUser() })
        }

        binding.buttonRemove.setOnClickListener {
            if (recyclerTableViewAdapter.itemCount > 0) {
                recyclerTableViewAdapter.removeItemAt(0)
            }
        }

        binding.buttonRemoveAt.setOnClickListener {
            if (recyclerTableViewAdapter.itemCount >= 5) {
                recyclerTableViewAdapter.removeItemAt(5)
            }
        }

        binding.buttonRemoveMore.setOnClickListener {
            val items = recyclerTableViewAdapter.items.slice(0 until minOf(recyclerTableViewAdapter.itemCount, 100))
            recyclerTableViewAdapter.removeItems(items)
        }

        binding.buttonRemoveChecked.setOnClickListener {
            recyclerTableViewAdapter.removeItems(recyclerTableViewAdapter.checkedItems)
        }

        binding.buttonClear.setOnClickListener {
            recyclerTableViewAdapter.clearItems()
        }

        binding.buttonCheckEllies.setOnClickListener {
            for (u in recyclerTableViewAdapter.items) {
                u.checked = u.name == "Ellie"
            }

            // Important: If doing any changes with the objects in rows, do not forget to notify
            // the adapter that there were changes made!
            recyclerTableViewAdapter.notifyDataSetChanged()
        }

        return binding.root
    }

}
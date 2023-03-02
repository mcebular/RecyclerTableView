package com.mc0239.recyclertableviewexample

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.mc0239.recyclertableviewexample.database.AppDatabase
import com.mc0239.recyclertableviewexample.databinding.ActivityMainBinding
import com.mc0239.recyclertableviewexample.samples.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fragmentSampleUsage: FragmentSampleUsage
    private lateinit var fragmentSampleCheckbox: FragmentSampleCheckbox
    private lateinit var fragmentSampleRadiobutton: FragmentSampleRadiobutton
    private lateinit var fragmentSampleEdittext: FragmentSampleEdittext
    private lateinit var fragmentSampleLiveData: FragmentSampleLivedata
    private lateinit var fragmentSampleInteractive: FragmentSampleInteractive

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        binding.navView.setNavigationItemSelectedListener(this)

        fragmentSampleUsage = FragmentSampleUsage()
        fragmentSampleCheckbox = FragmentSampleCheckbox()
        fragmentSampleRadiobutton = FragmentSampleRadiobutton()
        fragmentSampleEdittext = FragmentSampleEdittext()
        fragmentSampleLiveData = FragmentSampleLivedata()
        fragmentSampleInteractive = FragmentSampleInteractive()

        AppDatabase.getDatabase(this).generateSampleData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawers()

        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.nav_sample -> transaction.replace(R.id.fragment_frame, fragmentSampleUsage)
            R.id.nav_sample_checkbox -> transaction.replace(R.id.fragment_frame, fragmentSampleCheckbox)
            R.id.nav_sample_radio -> transaction.replace(R.id.fragment_frame, fragmentSampleRadiobutton)
            R.id.nav_sample_edittext -> transaction.replace(R.id.fragment_frame, fragmentSampleEdittext)
            R.id.nav_sample_livedata -> transaction.replace(R.id.fragment_frame, fragmentSampleLiveData)
            R.id.nav_sample_interactive -> transaction.replace(R.id.fragment_frame, fragmentSampleInteractive)
            else -> Log.w(javaClass.simpleName, "Navigation menu selection not handled.")
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.disallowAddToBackStack()
        transaction.commit()

        return true
    }

}
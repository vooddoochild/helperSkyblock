package com.example.skyblockhelper

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.example.skyblockhelper.BazaarFragment
import com.example.skyblockhelper.ElectionsFragment
import com.example.skyblockhelper.ProfileFragment
import com.example.skyblockhelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Skyblock Helper"

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Obsługa przycisku wstecz (nowa metoda)
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })

        // Startuj z profilem
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
            binding.navView.setCheckedItem(R.id.nav_profile)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment())
                    .commit()
            }
            R.id.nav_elections -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ElectionsFragment())
                    .commit()
            }
            R.id.nav_bazaar -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BazaarFragment())
                    .commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
package com.example.chama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchDarkMode: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Find the switch widget
        switchDarkMode = findViewById(R.id.switchDarkMode)

        // Set initial state based on user preference or system default
        val isDarkModeEnabled = isDarkModeEnabled()
        switchDarkMode.isChecked = isDarkModeEnabled

        // Set listener for switch state change
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Save the selected mode to shared preferences or other storage
            saveDarkModePreference(isChecked)

            // Apply the selected mode immediately (optional)
            applyDarkMode(isChecked)
        }

        // Other code for your settings activity
        // ...
    }

    private fun isDarkModeEnabled(): Boolean {
        // Retrieve the dark mode preference from shared preferences or other storage
        // Return the stored value, or false if not found
        // Example implementation using shared preferences:
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        return sharedPreferences.getBoolean("DarkModeEnabled", false)
    }

    private fun saveDarkModePreference(enabled: Boolean) {
        // Save the dark mode preference to shared preferences or other storage
        // Example implementation using shared preferences:
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("DarkModeEnabled", enabled)
        editor.apply()
    }

    private fun applyDarkMode(enabled: Boolean) {
        // Apply the selected mode immediately, such as recreating the activity or changing the theme
        // You can customize this method to fit your app's dark mode implementation
        // Example implementation: recreate the activity
        recreate()
    }
}

package com.dropBoxText.alexVasquez;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
    
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(Menu.NONE, 0, 0, "Show current settings");
            return super.onCreateOptionsMenu(menu);
        }
    
    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case 0:
                	/*
                	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                	  StringBuilder builder = new StringBuilder();
                	  builder.append("\n" + sharedPrefs.getBoolean("perform_updates", false));
                	  builder.append("\n" + sharedPrefs.getString("updates_interval", "-1"));
                	  builder.append("\n" + sharedPrefs.getString("welcome_message", "NULL"));
                	  */
                  return true;
            }
            return false;
        }

}
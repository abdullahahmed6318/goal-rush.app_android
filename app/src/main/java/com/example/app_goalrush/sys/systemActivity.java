package com.example.app_goalrush.sys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_goalrush.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class systemActivity extends AppCompatActivity {
    private SwitchCompat themeSwitch;
    private TextView themetext;
    private ImageView img;
    private SharedPreferences sharedPreferences;
    private final String PREFS_NAME = "AppPrefs";
    private final String NIGHT_MODE_KEY = "NightMode";
    BottomNavigationView dayview;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system);


        themeSwitch = findViewById(R.id.themeSwitch4);
        themetext = findViewById(R.id.themetext);
        img = findViewById(R.id.mode_img_view);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int savedMode = sharedPreferences.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_NO);
        if (savedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            themeSwitch.setChecked(true);
            themetext.setText("dark mode");
            img.setBackgroundResource(R.drawable.night_24);

        } else {
            themeSwitch.setChecked(false);
            themetext.setText("light mode");
            img.setBackgroundResource(R.drawable.sunny_24);
        }

        // تطبيق الوضع المحفوظ عند بداية التطبيق
        AppCompatDelegate.setDefaultNightMode(savedMode);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int newMode;
                if (isChecked) {
                    newMode = AppCompatDelegate.MODE_NIGHT_YES;
//                    buttonView.setText("الوضع الفاتح");
                } else {
                    newMode = AppCompatDelegate.MODE_NIGHT_NO;
//                    buttonView.setText("الوضع الداكن");
                }

                // تطبيق الثيم الجديد وحفظ الحالة
                AppCompatDelegate.setDefaultNightMode(newMode);
                saveNightModeState(newMode);
            }
        });


    }
    private void saveNightModeState(int mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NIGHT_MODE_KEY, mode);
        editor.apply();
    }

}
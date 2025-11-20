package com.example.app_goalrush;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class savedMode extends Application{

    private final String PREFS_NAME = "AppPrefs";
    private final String NIGHT_MODE_KEY = "NightMode";

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. قراءة التفضيل المحفوظ من الذاكرة
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedMode = sharedPreferences.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_NO);

        // 2. 🚨 تطبيق الوضع الليلي فوراً على مستوى التطبيق بالكامل
        // هذا يضمن أن أول Activity يتم إنشاؤها (systemActivity) ستحصل على الوضع الصحيح
        AppCompatDelegate.setDefaultNightMode(savedMode);
    }

}


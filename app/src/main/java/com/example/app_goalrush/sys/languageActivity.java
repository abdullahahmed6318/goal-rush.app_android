package com.example.app_goalrush.sys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_goalrush.MainActivity;
import com.example.app_goalrush.R;
import com.example.app_goalrush.databinding.ActivityLanguageBinding;
import com.example.app_goalrush.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class languageActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LANGUAGE = "selected_language";

    private SharedPreferences sharedPreferences;
    private ActivityLanguageBinding languageBinding;

    // لست بحاجة إلى تعريف هذه المتغيرات مرة أخرى لأننا سنستخدم الـ Binding
    // private TextView welcomeMessageTextView;
    // private TextView languageInfoTextView;
    // private Button btnAr, btnEn, btnFr;
    // private List<Button> languageButtons;
    // private String activeLanguage = "ar";
    // private ActivityMainBinding mainBinding;
    // private final Map<String, Map<String, String>> messages = new HashMap<>();

    // ** هام: يجب تطبيق اللغة المحفوظة قبل onCreate **
    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferences = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // قراءة آخر لغة محفوظة، الافتراضي "en"
        String currentLang = sharedPreferences.getString(KEY_LANGUAGE, "");

        // تطبيق اللغة عبر الكلاس المساعد
        Context context = LocaleHelper.setLocale(newBase, currentLang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // استخدام الـ View Binding لتضخيم ملف التخطيط
        languageBinding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(languageBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // زر العربية
        languageBinding.btnAr.setOnClickListener(v -> {
            setNewLocale("ar");
//            languageBinding.btnAr.setBackgroundResource(R.color.btn);
        });

        // زر الإنجليزية
        languageBinding.btnEn.setOnClickListener(v -> {
            setNewLocale("en");
        });

        // زر الفرنسية (يجب أن يكون لديك مجلد values-fr وملف strings.xml فيه)
        languageBinding.btnFr.setOnClickListener(v -> {
            setNewLocale("fr");
        });
    }

    private void setNewLocale(String languageCode) {
        // 1. حفظ اللغة الجديدة في SharedPreferences
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
        LocaleHelper.setLocale(getBaseContext(), languageCode);
        // 2. إعادة تشغيل الـ Activity لتطبيق الموارد الجديدة
        // هذا هو الأسلوب الأكثر شيوعاً وبساطة لتحديث واجهة المستخدم بالكامل.

        Intent refresh = new Intent(this, MainActivity.class);
        // منع إضافة الـ Activity إلى stack مرة أخرى
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refresh);
        // أغلق الـ Activity الحالية
        finish();
//        recreate();

        // رسالة تأكيد (اختياري)
        // Toast.makeText(this, "Language set to " + languageCode, Toast.LENGTH_SHORT).show();
    }

}
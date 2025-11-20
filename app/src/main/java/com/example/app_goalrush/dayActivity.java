package com.example.app_goalrush;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_goalrush.databinding.ActivityDayBinding;
import com.example.app_goalrush.day.todayFragment;
import com.example.app_goalrush.day.tomorrowFragment;
import com.example.app_goalrush.day.yesterdayFragment;
import com.example.app_goalrush.team.barcelonaFragment;
//import com.example.app_goalrush.team.team_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class dayActivity extends AppCompatActivity {
    private Button myButton; // Declare the button here

    private SharedPreferences sharedPreferences;
    private final String PREFS_NAME = "AppPrefs";
    private final String NIGHT_MODE_KEY = "NightMode";
    private AppBarConfiguration mAppBarConfiguration;

    private ActivityDayBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;

    BottomNavigationView dayview;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedMode = sharedPreferences.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedMode);

        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main3);
        binding = ActivityDayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // **الكود الخاص بعملية التحديث يوضع هنا**

                // مثال: استدعاء دالة تقوم بجلب البيانات الجديدة
                fetchNewData();
            }
        });

        myButton = findViewById(R.id.button);
//        myButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(dayActivity.this, HomeActivity.class);
//                startActivity(intent);
//            }
//        });
        myButton.setOnClickListener(v -> {
            // 1. إنشاء مثيل للـ Fragment
            barcelonaFragment fragment = new barcelonaFragment();

            // 2. استخدام getSupportFragmentManager()
            getSupportFragmentManager()
                    .beginTransaction()
                    // 3. تأكد أن R.id.fragment_container هو ID الحاوية في HomeActivity
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.dayview.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // **استخدم if/else if أو switch لتنفيذ الإجراء المناسب لكل زر**
            if (itemId == R.id.today) {
                replaceFragment(new todayFragment());
                // ضع هنا الكود الخاص بعرض بيانات اليوم (مثلاً: تغيير Fragment)
                // مثال: loadFragment(new TodayFragment());
                // يمكنك إظهار رسالة لتأكيد التنفيذ
                Toast.makeText(this, "عرض بيانات اليوم", Toast.LENGTH_SHORT).show();
                return true; // يجب إعادة true للإشارة إلى أن الضغطة قد تم التعامل معها
            } else if (itemId == R.id.tomorrow) {
                replaceFragment(new tomorrowFragment());
                // ضع هنا الكود الخاص بعرض بيانات الغد
                // مثال: loadFragment(new TomorrowFragment());
                Toast.makeText(this, "عرض بيانات الغد", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.yesterday) {
                replaceFragment(new yesterdayFragment());
                // ضع هنا الكود الخاص بعرض بيانات الأمس
                // مثال: loadFragment(new YesterdayFragment());
                Toast.makeText(this, "عرض بيانات الأمس", Toast.LENGTH_SHORT).show();
                return true;
            }

            ViewCompat.setOnApplyWindowInsetsListener(binding.dayview, (v, insets) -> {
                // استخدم binding.dayview بدلاً من findViewById(R.id.dayview) إذا كنت تستخدم View Binding
                final int systemBarsBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

                // إزالة مساحة الحشو الإضافية عن طريق طرح مسافة شريط النظام
                v.setPadding(
                        v.getPaddingLeft(),
                        v.getPaddingTop(),
                        v.getPaddingRight(),
                        v.getPaddingBottom() - systemBarsBottom
                );
                return insets;
            });

            // إذا لم يكن أي من الـ IDs المذكورة (قد لا تحتاج إليها)
            return false;
        });



        }


    private void fetchNewData() {
        // ... (هنا يتم تنفيذ منطق جلب البيانات من الإنترنت أو قاعدة البيانات)

        // لمحاكاة عملية تحديث
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 3. إيقاف مؤشر التحميل بعد انتهاء التحديث
                swipeRefreshLayout.setRefreshing(false);

                // رسالة تأكيد (اختياري)
                Toast.makeText(dayActivity.this, "Data Refreshed!", Toast.LENGTH_SHORT).show();
            }
        }, 3000); // 3000 مللي ثانية = 3 ثواني
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_day_fragment_activity_main,fragment);
        fragmentTransaction.commit();
    }




}
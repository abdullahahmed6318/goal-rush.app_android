package com.example.app_goalrush;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.app_goalrush.sys.LocaleHelper;
import com.example.app_goalrush.sys.MyBottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_goalrush.databinding.ActivityMainBinding;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.sql.ConnectionEvent;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private final String PREFS_NAME = "AppPrefs";
    private final String NIGHT_MODE_KEY = "NightMode";
    private static final String KEY_LANGUAGE = "selected_language";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private final String unityGameID = "5969915"; // استخدم المعرّف الموجود في الصورة لمنصة Android
    private final Boolean testMode = true; // استخدم True للاختبار و False للنشر
    private final String interstitialAdUnitId = "Interstitial_Android";
    private final String rewardedAdUnitId = "Rewarded_Android";
    private final String bannerAdUnitId = "Banner_Android";
    private BannerView bannerView;
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferences = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // قراءة آخر لغة محفوظة، الافتراضي "en"
        String currentLang = sharedPreferences.getString(KEY_LANGUAGE, "en");

        // تطبيق اللغة عبر الكلاس المساعد (LocaleHelper)
        Context context = LocaleHelper.setLocale(newBase, currentLang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        int savedMode = sharedPreferences.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_NO);
//        AppCompatDelegate.setDefaultNightMode(savedMode);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeUnityAds();


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd();
                Intent intent = new Intent(MainActivity.this, dayActivity.class);
                startActivity(intent);
            }
        });


        binding.showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // عرض Bottom Sheet Dialog
                MyBottomSheetDialogFragment bottomSheet = MyBottomSheetDialogFragment.newInstance();
                bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
            }
        });


    }

    private void loadBannerAd() {
        // 1. إنشاء الـ BannerView
        // استخدم الحجم القياسي 320x50. UnityBannerSize يوفر أحجاماً جاهزة.
        bannerView = new BannerView(
                MainActivity.this,
                bannerAdUnitId,
                new UnityBannerSize(320, 50)
        );
        // 2. إيجاد الحاوية وربط البانر بها
        RelativeLayout bannerContainer = findViewById(R.id.bannerAdContainer);
        if (bannerContainer != null && bannerContainer.getChildCount() == 0) {
            bannerContainer.addView(bannerView);
        }

        // 3. تحميل الإعلان
        bannerView.load();
        Log.i(TAG, "Banner Ad Loading Initiated");
    }
    private void initializeUnityAds() {
        UnityAds.initialize(getApplicationContext(), unityGameID, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                loadBannerAd();
                Log.i(TAG, "Unity Ads Initialization Complete");
                // عند اكتمال التهيئة، ابدأ بتحميل إعلان المكافأة فوراً
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                loadRewardedAd();
                            }
                        },
                        1000); // تأخير لمدة ثانية
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                Log.e(TAG, "Unity Ads Initialization Failed: " + message);
            }
        });
    }

    // دالة تحميل إعلان المكافأة
    private void loadRewardedAd() {
        UnityAds.load(rewardedAdUnitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.i(TAG, "Rewarded Ad Loaded: " + placementId);
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Rewarded Ad Failed to Load: " + message);
                // يمكن إضافة منطق لإعادة المحاولة بعد فترة
            }
        });
    }

    // دالة عرض إعلان المكافأة
    private void showRewardedAd() {
        // التحقق مما إذا كان الإعلان جاهزاً للعرض
        if (UnityAds.isInitialized()) { // تحقق فقط من التهيئة
            UnityAds.show(MainActivity.this, rewardedAdUnitId, new IUnityAdsShowListener() {
                // ... (بقية الدالة: onUnityAdsShowFailure, onUnityAdsShowComplete, إلخ)
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    Log.e(TAG, "Rewarded Ad Show Failure: " + message);
                    Toast.makeText(MainActivity.this, "فشل عرض الإعلان. حاول مجدداً.", Toast.LENGTH_SHORT).show();
                    loadRewardedAd(); // حاول تحميل إعلان جديد بعد الفشل
                }

                @Override
                public void onUnityAdsShowStart(String placementId) {
                    Log.i(TAG, "Rewarded Ad Show Started");
                    // إيقاف اللعبة أو الموسيقى هنا
                }

                @Override
                public void onUnityAdsShowClick(String placementId) {
                    Log.i(TAG, "Rewarded Ad Clicked");
                }

                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    Log.i(TAG, "Rewarded Ad Show Complete. State: " + state);

                    // منطق المكافأة: يتم إعطاء المكافأة فقط إذا شاهد المستخدم الإعلان بالكامل
                    if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                        // **هنا يتم منح المستخدم المكافأة**
//                        Toast.makeText(MainActivity.this, "مبروك! لقد حصلت على المكافأة.", Toast.LENGTH_LONG).show();

                        // الانتقال إلى الشاشة التالية (dayActivity) بعد منح المكافأة
//                        Intent intent = new Intent(MainActivity.this, dayActivity.class);
//                        startActivity(intent);

                    } else {
                        // لم يكمل المستخدم الإعلان (ضغط على تخطي أو أغلق الشاشة)
//                        Toast.makeText(MainActivity.this, "لم تشاهد الإعلان كاملاً. لم تحصل على مكافأة.", Toast.LENGTH_LONG).show();
                    }

                    // بعد انتهاء العرض، قم بتحميل إعلان جديد للاستخدام القادم
                    loadRewardedAd();
                }
            });
        } else {
            // إذا لم يكن الإعلان جاهزاً، أخبر المستخدم وقم بتحميله
            Toast.makeText(MainActivity.this, "الإعلان غير جاهز حالياً. يرجى الانتظار...", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }
        @Override
        public boolean onSupportNavigateUp () {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }


        @Override
        protected void onResume () {
            super.onResume();

        }

    }

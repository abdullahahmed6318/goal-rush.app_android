package com.example.app_goalrush.sys;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    /**
     * يقوم بتغيير لغة التطبيق إلى اللغة المحددة.
     * يستخدم أحدث الطرق (Context.createConfigurationContext) لإصدارات الأندرويد الحديثة.
     *
     * @param context السياق الحالي للـ Activity.
     * @param languageCode رمز اللغة (مثل "en", "ar", "fr").
     * @return سياق جديد تم تطبيق الإعدادات اللغوية الجديدة عليه.
     */
    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            // استخدم updateConfiguration لإصدارات Oreo وما فوق
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return context.createConfigurationContext(config);
            } else {
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                return context;
            }
        } else {
            // الطريقة القديمة (للإصدارات الأقدم من N)
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }

    /**
     * دالة مساعدة لتطبيق اللغة عند بداية كل Activity.
     * يتم استدعاؤها في دالة attachBaseContext.
     */
    public static Context onAttach(Context context, String defaultLanguage) {
        // هنا يجب قراءة اللغة المحفوظة من SharedPreferences لو كنت تستخدمها في هذا الكلاس
        // لكن بما أننا سنستخدم SharedPreferences في LanguageActivity، نستخدم اللغة الافتراضية
        return setLocale(context, defaultLanguage);
    }
}

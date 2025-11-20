package com.example.app_goalrush.day;

import android.widget.ImageView;
import androidx.fragment.app.Fragment;

/**
 * واجهة لتحديد الإجراءات المشتركة المطلوبة من الـ Fragments التي تستخدم Match Adapter.
 * هذا يجعل الـ Adapter لا يعتمد مباشرة على نوع Fragment محدد.
 */
public interface MatchActions {
    /**
     * دالة تحميل الصورة وتنفيذها على الـ ImageView
     * يجب أن يتم تنفيذها في TodayFragment و YesterdayFragment.
     */
    void loadAndSetImage(String imageUrlString, ImageView imageView);

    /**
     * دالة للحصول على نسخة الـ Fragment لتنفيذ Fragment Transactions
     * (يمكن استخدامها أيضًا للحصول على Context والـ FragmentManager)
     */
    Fragment getFragmentInstance();
}

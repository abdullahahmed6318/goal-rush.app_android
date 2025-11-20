package com.example.app_goalrush.sys;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.app_goalrush.R;
import com.example.app_goalrush.dayActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;

    // الثوابت (Constants)
    private static final String PREFS_NAME = "NightModePrefs";
    private static final String NIGHT_MODE_KEY = "NightMode";

    public static MyBottomSheetDialogFragment newInstance() {
        return new MyBottomSheetDialogFragment();
    }

    // طريقة لحفظ حالة الوضع الليلي
    private void saveNightModeState(int mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NIGHT_MODE_KEY, mode);
        editor.apply(); // استخدام apply للتنفيذ غير المتزامن (Asynchronous)
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // تضخيم الـ Layout الخاص بالـ Bottom Sheet
        View view = inflater.inflate(R.layout.btn_sheet, container, false); // استخدم btn_sheet كما في الصورة

        // 1. ربط المكونات (Finding Views)
        Button actionOneButton = view.findViewById(R.id.button_action_one);
        Button actionTwoButton = view.findViewById(R.id.button_action_two);
        Button actionTwo2Button = view.findViewById(R.id.button_action_two2);



        // 5. معالج حدث الإجراء الأول (فتح Activity)
        actionOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // فتح Activity جديد
                // تأكد من وجود NewActivity مسجل في AndroidManifest.xml
                Intent intent = new Intent(getContext(), systemActivity.class);
                startActivity(intent);

                // إغلاق الـ Bottom Sheet
                dismiss();
            }
        });

        // 6. معالج حدث الإجراء الثاني
        actionTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), languageActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        actionTwo2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "تم النقر على الإجراء الثاني", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return view;
    }

}
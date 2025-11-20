package com.example.app_goalrush.day;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_goalrush.R;
import java.util.List;

/**
 * الـ Adapter الخاص بعرض المباريات في TodayFragment.
 * يستخدم واجهة MatchActions لتبسيط الـ Constructor ومنع أخطاء الـ NullPointer.
 */
public class TodayMatchAdapter extends RecyclerView.Adapter<TodayMatchAdapter.MatchViewHolder> {

    private static final String TAG = "TodayMatchAdapter";
    private final List<MatchItem> matchList;
    private final MatchActions matchActions; // 💥 يستخدم الواجهة بدلاً من TodayFragment/YesterdayFragment

    // 💥 Constructor بسيط يستقبل الواجهة فقط
    public TodayMatchAdapter(List<MatchItem> matchList, MatchActions matchActions) {
        this.matchList = matchList;
        this.matchActions = matchActions;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchItem item = matchList.get(position);
        holder.bind(item, matchActions); // تمرير الواجهة
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        private final TextView leagueTextView;
        private final ImageView team1ImgView , team2ImgView, team3ImgView, team4ImgView, team5ImgView, team6ImgView, team7ImgView, team8ImgView, team9ImgView, team10ImgView;
        private final TextView team1NameView, team2NameView , team3NameView, team4NameView, team5NameView, team6NameView, team7NameView, team8NameView, team9NameView, team10NameView;
        private final ConstraintLayout rootLayout;

        private final ConstraintLayout teams_row_1 ,teams_row_2 , teams_row_3 , teams_row_4 , teams_row_5;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            // ربط الـ Views من item_match.xml
            rootLayout = (ConstraintLayout) itemView.findViewById(R.id.match_root_layout);
            leagueTextView = itemView.findViewById(R.id.league_text_view);

            teams_row_1 = (ConstraintLayout) itemView.findViewById(R.id.teams_row_1);
            team1ImgView = itemView.findViewById(R.id.team1_img_view);
            team1NameView = itemView.findViewById(R.id.team1_name_view);
            team2ImgView = itemView.findViewById(R.id.team2_img_view);
            team2NameView = itemView.findViewById(R.id.team2_name_view);

            teams_row_2 = itemView.findViewById(R.id.teams_row_2);
            team3ImgView = itemView.findViewById(R.id.team3_img_view);
            team3NameView = itemView.findViewById(R.id.team3_name_view);
            team4ImgView = itemView.findViewById(R.id.team4_img_view);
            team4NameView = itemView.findViewById(R.id.team4_name_view);

            teams_row_3 = itemView.findViewById(R.id.teams_row_3);
            team5ImgView = itemView.findViewById(R.id.team5_img_view);
            team5NameView = itemView.findViewById(R.id.team5_name_view);
            team6ImgView = itemView.findViewById(R.id.team6_img_view);
            team6NameView = itemView.findViewById(R.id.team6_name_view);

            teams_row_4 = itemView.findViewById(R.id.teams_row_4);
            team7ImgView = itemView.findViewById(R.id.team7_img_view);
            team7NameView = itemView.findViewById(R.id.team7_name_view);
            team8ImgView = itemView.findViewById(R.id.team8_img_view);
            team8NameView = itemView.findViewById(R.id.team8_name_view);

            teams_row_5 = itemView.findViewById(R.id.teams_row_5);
            team9ImgView = itemView.findViewById(R.id.team9_img_view);
            team9NameView = itemView.findViewById(R.id.team9_name_view);
            team10ImgView = itemView.findViewById(R.id.team10_img_view);
            team10NameView = itemView.findViewById(R.id.team10_name_view);
        }

        public void bind(MatchItem item, MatchActions matchActions) { // 💥 الـ bind يستقبل الواجهة
            Context context = itemView.getContext();

            // نحصل على نسخة الـ Fragment لتنفيذ الـ Transactions والوصول للـ Activity
            Fragment activeFragment = matchActions.getFragmentInstance();

            leagueTextView.setText(item.league);
            team1NameView.setText(item.team1Name);
            team2NameView.setText(item.team2Name);

            team3NameView.setText(item.teamNamethird3);
            team4NameView.setText(item.teamNamefourth4);

            team5NameView.setText(item.teamNameFive5);
            team6NameView.setText(item.teamNamesixth6);

            team7NameView.setText(item.teamNameseven7);
            team8NameView.setText(item.teamNameeighth8);

            team9NameView.setText(item.teamNameNinth9);
            team10NameView.setText(item.teamNametenth10);


            // منطق إخفاء/إظهار الصفوف بناءً على وجود بيانات الفريق الثالث
            if (item.teamNamethird3 == null || item.teamNamethird3.trim().isEmpty()) {
                teams_row_2.setVisibility(View.GONE);
            } else {
                teams_row_2.setVisibility(View.VISIBLE);
            }

            if (item.teamNameFive5 == null || item.teamNameFive5.trim().isEmpty()) {
                teams_row_3.setVisibility(View.GONE);
            } else {
                teams_row_3.setVisibility(View.VISIBLE);
            }

            if (item.teamNameseven7 == null || item.teamNameseven7.trim().isEmpty()) {
                teams_row_4.setVisibility(View.GONE);
            } else {
                teams_row_4.setVisibility(View.VISIBLE);
            }

            if (item.teamNameNinth9 == null || item.teamNameNinth9.trim().isEmpty()) {
                teams_row_5.setVisibility(View.GONE);
            } else {
                teams_row_5.setVisibility(View.VISIBLE);
            }

            // 💥💥 استخدام الواجهة لتحميل الصور 💥💥
            matchActions.loadAndSetImage(item.team1ImgUrl, team1ImgView);
            matchActions.loadAndSetImage(item.team2ImgUrl, team2ImgView);
            matchActions.loadAndSetImage(item.teamImgthird3, team3ImgView);
            matchActions.loadAndSetImage(item.teamImgfourth4, team4ImgView);
            matchActions.loadAndSetImage(item.teamImgFive5, team5ImgView);
            matchActions.loadAndSetImage(item.teamImgsixth6, team6ImgView);
            matchActions.loadAndSetImage(item.teamImgseven7, team7ImgView);
            matchActions.loadAndSetImage(item.teamImgeighth8, team8ImgView);
            matchActions.loadAndSetImage(item.teamImgNinth9, team9ImgView);
            matchActions.loadAndSetImage(item.teamImgtenth10, team10ImgView);

            // 5. منطق الضغط (Click Listener)
            if (item.activityFileName == null || item.activityFileName.trim().isEmpty()) {
                teams_row_1.setClickable(false);
                teams_row_1.setEnabled(false);
            } else {
                teams_row_1.setClickable(true);
                teams_row_1.setEnabled(true);
                teams_row_1.setOnClickListener(v -> {
                    try {
                        String targetClassName = item.activityFileName.replace(".java", "");
                        Class<?> targetComponentClass = Class.forName(targetClassName);

                        if (Fragment.class.isAssignableFrom(targetComponentClass) && (context instanceof AppCompatActivity) && activeFragment != null) {
                            // تشغيل Fragment Transaction
                            java.lang.reflect.Constructor<?> constructor = targetComponentClass.getDeclaredConstructor();
                            Fragment targetFragment = (Fragment) constructor.newInstance();
                            Bundle args = new Bundle();
                            args.putString("LEAGUE_NAME", item.league);
                            // ... أضف المزيد من البيانات
                            targetFragment.setArguments(args);

                            ((AppCompatActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, targetFragment)
                                    .addToBackStack(null)
                                    .commit();

                            // إخفاء الـ dayview باستخدام الـ Activity الخاص بالـ Fragment النشط
                            View dayView = activeFragment.getActivity().findViewById(R.id.dayview);
                            if (dayView != null) {
                                dayView.setVisibility(View.GONE);
                            }

                        } else if (android.app.Activity.class.isAssignableFrom(targetComponentClass)) {
                            // تشغيل Activity Intent
                            Intent intent = new Intent(context, targetComponentClass);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: Failed to navigate to " + item.activityFileName, e);
                    }
                });
            }

            if (item.activityFile2Name == null || item.activityFile2Name.trim().isEmpty()) {
                teams_row_2.setClickable(false);
                teams_row_2.setEnabled(false);
            } else {
                teams_row_2.setClickable(true);
                teams_row_2.setEnabled(true);
                teams_row_2.setOnClickListener(v -> {
                    try {
                        String targetClassName = item.activityFileName.replace(".java", "");
                        Class<?> targetComponentClass = Class.forName(targetClassName);

                        if (Fragment.class.isAssignableFrom(targetComponentClass) && (context instanceof AppCompatActivity) && activeFragment != null) {
                            // تشغيل Fragment Transaction
                            java.lang.reflect.Constructor<?> constructor = targetComponentClass.getDeclaredConstructor();
                            Fragment targetFragment = (Fragment) constructor.newInstance();
                            Bundle args = new Bundle();
                            args.putString("LEAGUE_NAME", item.league);
                            // ... أضف المزيد من البيانات
                            targetFragment.setArguments(args);

                            ((AppCompatActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, targetFragment)
                                    .addToBackStack(null)
                                    .commit();

                            // إخفاء الـ dayview باستخدام الـ Activity الخاص بالـ Fragment النشط
                            View dayView = activeFragment.getActivity().findViewById(R.id.dayview);
                            if (dayView != null) {
                                dayView.setVisibility(View.GONE);
                            }

                        } else if (android.app.Activity.class.isAssignableFrom(targetComponentClass)) {
                            // تشغيل Activity Intent
                            Intent intent = new Intent(context, targetComponentClass);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: Failed to navigate to " + item.activityFileName, e);
                    }
                });
            }

            if (item.activityFile3Name == null || item.activityFile3Name.trim().isEmpty()) {
                teams_row_3.setClickable(false);
                teams_row_3.setEnabled(false);
            } else {
                teams_row_3.setClickable(true);
                teams_row_3.setEnabled(true);
                teams_row_3.setOnClickListener(v -> {
                    try {
                        String targetClassName = item.activityFileName.replace(".java", "");
                        Class<?> targetComponentClass = Class.forName(targetClassName);

                        if (Fragment.class.isAssignableFrom(targetComponentClass) && (context instanceof AppCompatActivity) && activeFragment != null) {
                            // تشغيل Fragment Transaction
                            java.lang.reflect.Constructor<?> constructor = targetComponentClass.getDeclaredConstructor();
                            Fragment targetFragment = (Fragment) constructor.newInstance();
                            Bundle args = new Bundle();
                            args.putString("LEAGUE_NAME", item.league);
                            // ... أضف المزيد من البيانات
                            targetFragment.setArguments(args);

                            ((AppCompatActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, targetFragment)
                                    .addToBackStack(null)
                                    .commit();

                            // إخفاء الـ dayview باستخدام الـ Activity الخاص بالـ Fragment النشط
                            View dayView = activeFragment.getActivity().findViewById(R.id.dayview);
                            if (dayView != null) {
                                dayView.setVisibility(View.GONE);
                            }

                        } else if (android.app.Activity.class.isAssignableFrom(targetComponentClass)) {
                            // تشغيل Activity Intent
                            Intent intent = new Intent(context, targetComponentClass);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: Failed to navigate to " + item.activityFileName, e);
                    }
                });
            }

            if (item.activityFile4Name == null || item.activityFile4Name.trim().isEmpty()) {
                teams_row_4.setClickable(false);
                teams_row_4.setEnabled(false);
            } else {
                teams_row_4.setClickable(true);
                teams_row_4.setEnabled(true);
                teams_row_4.setOnClickListener(v -> {
                    try {
                        String targetClassName = item.activityFileName.replace(".java", "");
                        Class<?> targetComponentClass = Class.forName(targetClassName);

                        if (Fragment.class.isAssignableFrom(targetComponentClass) && (context instanceof AppCompatActivity) && activeFragment != null) {
                            // تشغيل Fragment Transaction
                            java.lang.reflect.Constructor<?> constructor = targetComponentClass.getDeclaredConstructor();
                            Fragment targetFragment = (Fragment) constructor.newInstance();
                            Bundle args = new Bundle();
                            args.putString("LEAGUE_NAME", item.league);
                            // ... أضف المزيد من البيانات
                            targetFragment.setArguments(args);

                            ((AppCompatActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, targetFragment)
                                    .addToBackStack(null)
                                    .commit();

                            // إخفاء الـ dayview باستخدام الـ Activity الخاص بالـ Fragment النشط
                            View dayView = activeFragment.getActivity().findViewById(R.id.dayview);
                            if (dayView != null) {
                                dayView.setVisibility(View.GONE);
                            }

                        } else if (android.app.Activity.class.isAssignableFrom(targetComponentClass)) {
                            // تشغيل Activity Intent
                            Intent intent = new Intent(context, targetComponentClass);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: Failed to navigate to " + item.activityFileName, e);
                    }
                });
            }

            if (item.activityFile5Name == null || item.activityFile5Name.trim().isEmpty()) {
                teams_row_5.setClickable(false);
                teams_row_5.setEnabled(false);
            } else {
                teams_row_5.setClickable(true);
                teams_row_5.setEnabled(true);
                teams_row_5.setOnClickListener(v -> {
                    try {
                        String targetClassName = item.activityFileName.replace(".java", "");
                        Class<?> targetComponentClass = Class.forName(targetClassName);

                        if (Fragment.class.isAssignableFrom(targetComponentClass) && (context instanceof AppCompatActivity) && activeFragment != null) {
                            // تشغيل Fragment Transaction
                            java.lang.reflect.Constructor<?> constructor = targetComponentClass.getDeclaredConstructor();
                            Fragment targetFragment = (Fragment) constructor.newInstance();
                            Bundle args = new Bundle();
                            args.putString("LEAGUE_NAME", item.league);
                            // ... أضف المزيد من البيانات
                            targetFragment.setArguments(args);

                            ((AppCompatActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, targetFragment)
                                    .addToBackStack(null)
                                    .commit();

                            // إخفاء الـ dayview باستخدام الـ Activity الخاص بالـ Fragment النشط
                            View dayView = activeFragment.getActivity().findViewById(R.id.dayview);
                            if (dayView != null) {
                                dayView.setVisibility(View.GONE);
                            }

                        } else if (android.app.Activity.class.isAssignableFrom(targetComponentClass)) {
                            // تشغيل Activity Intent
                            Intent intent = new Intent(context, targetComponentClass);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: Failed to navigate to " + item.activityFileName, e);
                    }
                });
            }



        }

        }

    }


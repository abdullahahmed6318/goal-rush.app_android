package com.example.app_goalrush.day;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_goalrush.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class tomorrowFragment extends Fragment implements MatchActions {

    private static final String TAG = "TodayFragment";
    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(4); // استخدام مجموعة خيوط للشبكة والصور
    private volatile boolean isDataLoading = false; // لمنع الجلب المتكرر
    private RecyclerView recyclerView;
    private TomorrowMatchAdapter matchAdapter;
    private List<MatchItem> matchesList = new ArrayList<>();
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        recyclerView = view.findViewById(R.id.matches_recycler_view);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);

        // إعداد الـ RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 💥💥 استخدام TodayMatchAdapter ونمرر 'this' (الذي هو الآن MatchActions) 💥💥
        matchAdapter = new TomorrowMatchAdapter(matchesList, this);

        recyclerView.setAdapter(matchAdapter);

        fetchJsonFromUrl("https://goal-rush.netlify.app/json/items_tomorrow.json");

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            // نتحقق من عدد الإدخالات في مكدس العودة
            // إذا كان العدد صفراً، فهذا يعني أننا عدنا إلى الواجهة الرئيسية
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // *** 🎯 الإظهار هنا 🎯 ***
                View dayView = getActivity().findViewById(R.id.dayview); // تأكد من الـ ID الصحيح
                if (dayView != null) {
                    dayView.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private void fetchJsonFromUrl(String urlString) {
        if (isDataLoading) {
            Log.w(TAG, "Fetch request ignored: Data loading is already in progress.");
            return;
        }
        isDataLoading = true; // وضع علامة أننا بدأنا التحميل

        // 1. عرض ProgressBar على الخيط الرئيسي
        requireActivity().runOnUiThread(() -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        // 2. بدء عملية الجلب والتحليل على خيط خلفي
        backgroundExecutor.execute(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000); // مهلة الاتصال
                urlConnection.setReadTimeout(10000); // مهلة القراءة
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();

                // 3. استدعاء دالة التحليل في نفس الخيط الخلفي
                if (jsonString != null && !jsonString.isEmpty()) {
                    parseAndDisplayJson(jsonString);
                } else {
                    throw new IOException("Received empty or null response from server.");
                }

            } catch (IOException e) {
                Log.e(TAG, "Network error fetching data: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "خطأ في الاتصال بالشبكة.", Toast.LENGTH_LONG).show();
                });
            } finally {
                // 4. إزالة علامة التحميل وإخفاء ProgressBar على الخيط الرئيسي
                isDataLoading = false;
                requireActivity().runOnUiThread(() -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                });

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader: " + e.getMessage());
                    }
                }
            }
        });
    }


    private void parseAndDisplayJson(String jsonString) {
        List<MatchItem> newMatches = new ArrayList<>();


        try {
            JSONObject mainJsonObject = new JSONObject(jsonString);
            JSONArray videosArray = mainJsonObject.getJSONArray("videos");

            for (int i = 0; i < videosArray.length(); i++) {
                JSONObject videoObject = videosArray.getJSONObject(i);

                // استخراج البيانات وتخزينها في كائن MatchItem
                String league = videoObject.optString("side_bar_league", "");
                String team1Name = videoObject.optString("name_team_First", "");
                if (team1Name == null || team1Name.trim().isEmpty()) {
                    continue;
                }
                String team1ImgUrl = videoObject.optString("First_img", "");
                String team2Name = videoObject.optString("name_team_Second", "");


                String team2ImgUrl = videoObject.optString("Second_img", "");

                // استخراج باقي الفرق (يجب تعديل الكود هنا لاستخراج الفرق 3-10 بشكل صحيح)
                String teamNamethird3 = videoObject.optString("name_team_third", "");
                String teamImgthird3 = videoObject.optString("third_img", "");
                String teamNamefourth4 = videoObject.optString("name_team_fourth", "");
                String teamImgfourth4 = videoObject.optString("fourth_img", "");
                String teamNameFive5 = videoObject.optString("name_team_Five", "");
                String teamImgFive5 = videoObject.optString("Five_img", "");
                String teamNamesixth6 = videoObject.optString("name_team_sixth", "");
                String teamImgsixth6 = videoObject.optString("sixth_img", "");
                String teamNameseven7 = videoObject.optString("name_team_seven", "");
                String teamImgseven7 = videoObject.optString("seven_img", "");
                String teamNameeighth8 = videoObject.optString("name_team_eighth", "");
                String teamImgeighth8 = videoObject.optString("eighth_img", "");
                String teamNameNinth9 = videoObject.optString("name_team_Ninth", "");
                String teamImgNinth9 = videoObject.optString("Ninth_img", "");
                String teamNametenth10 = videoObject.optString("name_team_tenth", "");
                String teamImgtenth10 = videoObject.optString("tenth_img", "");

                String activityFileName = videoObject.optString("another_activity_file", "");
                String activityFile2Name = videoObject.optString("another_activity_file2", "");
                String activityFile3Name = videoObject.optString("another_activity_file3", "");
                String activityFile4Name = videoObject.optString("another_activity_file4", "");
                String activityFile5Name = videoObject.optString("another_activity_file5", "");

                MatchItem item = new MatchItem(league, team1Name, team1ImgUrl, team2Name, team2ImgUrl,
                        teamNamethird3, teamImgthird3, teamNamefourth4, teamImgfourth4,
                        teamNameFive5, teamImgFive5, teamNamesixth6, teamImgsixth6,
                        teamNameseven7, teamImgseven7, teamNameeighth8, teamImgeighth8,
                        teamNameNinth9, teamImgNinth9, teamNametenth10, teamImgtenth10,
                        activityFileName , activityFile2Name , activityFile3Name , activityFile4Name , activityFile5Name);
                newMatches.add(item);
            }

            // الانتقال إلى الخيط الرئيسي لتحديث الـ Adapter
            requireActivity().runOnUiThread(() -> {
                updateAdapter(newMatches);
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "خطأ في تحليل البيانات.", Toast.LENGTH_LONG).show();
            });
        }
    }

    /**
     * تحديث الـ Adapter على الخيط الرئيسي.
     */
    private void updateAdapter(List<MatchItem> newMatches) {
        matchesList.clear();
        matchesList.addAll(newMatches);
        matchAdapter.notifyDataSetChanged();
    }


    public void loadAndSetImage(String imageUrlString, ImageView imageView) {
//        if (imageUrlString == null || imageUrlString.trim().isEmpty() || !isAdded()) {
//            return;
//        }

        // استخدام ExecutorService لتنظيم عمل تحميل الصور
        backgroundExecutor.execute(() -> {
            HttpURLConnection imageConnection = null;
            InputStream imageStream = null;
            Bitmap bitmap = null;
            try {
                URL imageUrl = new URL(imageUrlString);
                imageConnection = (HttpURLConnection) imageUrl.openConnection();
                imageConnection.connect();
                imageStream = imageConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(imageStream);

            } catch (IOException e) {
                Log.e(TAG, "Error loading image: " + imageUrlString, e);
            } finally {
                // إغلاق الموارد
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) { /* ignore */ }
                }
                if (imageConnection != null) {
                    imageConnection.disconnect();
                }
            }

            // تحديث الواجهة بعد الانتهاء من التحميل
            if (isAdded()) {
                final Bitmap finalBitmap = bitmap;
                requireActivity().runOnUiThread(() -> {
                    if (finalBitmap != null) {
                        imageView.setImageBitmap(finalBitmap);
                    } else {
                        // صورة افتراضية في حالة الخطأ أو عدم التحميل
                        imageView.setImageResource(R.drawable.football_team);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 💥 ضروري: إغلاق ExecutorService لإلغاء أي مهام متبقية ومنع تسريب الذاكرة
        if (!backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdownNow();
            Log.d(TAG, "Background Executor Shut Down.");
        }
    }
    @Override
    public Fragment getFragmentInstance() {
        return this; // لإرجاع نسخة هذا الـ Fragment (اليوم)
    }

}
package com.example.app_goalrush.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;


public class team_Fragment extends Fragment {

    private static final String TAG = "MainActivity";
    private TextView side_bar_league;

    private TextView jsonDataTextView;
    private TextView jsonDataTextViewSecond;

    private ImageView teamImageView;
    private ImageView teamImageViewSecond;
    private LinearLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    public team_Fragment() {
        // مطلوب للـ Fragments
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        mainLayout = view.findViewById(R.id.main_todoy_layout);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Start the network operation once the view is created
        String jsonUrl = "https://goal-rush.netlify.app/team/barcelona/barcelona.json";
        fetchJsonFromUrl(jsonUrl);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        // 2. تعيين المستمع (Listener) لعملية السحب والتحديث
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // استدعاء الدالة التي تنفذ عملية جلب/تحديث البيانات
                refreshContent();
            }
        });

    }

    private void refreshContent() {

        if (mainLayout != null) {
            mainLayout.removeAllViews();
            // **اختياري:** يمكنك إضافة مؤشر "جاري التحميل..." مؤقت هنا
        }

        // 2. بدء عملية جلب البيانات الجديدة
        String jsonUrl = "https://goal-rush.netlify.app/team/barcelona/barcelona.json";
        fetchJsonFromUrl(jsonUrl);

    }

    private void fetchJsonFromUrl(String urlString) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect(); // Removed timeouts as per request

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();

                // Call the method to parse and display the result on the UI thread
                String finalJsonString = jsonString;
                requireActivity().runOnUiThread(() -> {
                    parseAndDisplayJson(finalJsonString);

                    // **الخطوة الأهم: إيقاف التحميل عند النجاح**
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "تم تحديث بيانات المباريات بنجاح!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "Network error fetching data: " + e.getMessage());
                e.printStackTrace();

                // Use requireActivity().runOnUiThread() to update the UI on the main thread
                // Adapted the original error handling logic for the Fragment context
                requireActivity().runOnUiThread(() -> {
                    // في حالة الخطأ، أضف TextView إلى التخطيط الديناميكي
                    TextView errorTextView = new TextView(requireContext());
                    errorTextView.setText("حدث خطأ في الاتصال بالشبكة.");

                    if (mainLayout != null) {
                        mainLayout.removeAllViews(); // Clear "Loading..." text
                        mainLayout.addView(errorTextView);
                    }
                    // **إيقاف التحميل عند الخطأ أيضاً**
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "فشل التحديث. خطأ في الشبكة.", Toast.LENGTH_LONG).show();
                    }

                });
            } finally {
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
        }).start();
    }

    private void parseAndDisplayJson(String jsonString) {
        try {
            JSONObject mainJsonObject = new JSONObject(jsonString);
            JSONArray videosArray = mainJsonObject.getJSONArray("videos");

            requireActivity().runOnUiThread(() -> {
                for (int i = 0; i < videosArray.length(); i++) {
                    try {
                        // الحصول على كائن الفيديو الرئيسي
                        JSONObject videoObject = videosArray.getJSONObject(i);
                        // الحصول على كائن "offers" المتداخل من كائن الفيديو
                        JSONObject offersObject = videoObject.getJSONObject("offers");

                        // استخراج البيانات من كائن "offers"
                        String type = offersObject.getString("type");
                        if (type == null || type.trim().isEmpty()) {
                            continue; // تجاهل هذا العنصر إذا كان النوع فارغًا
                        }
                        String championship = offersObject.getString("Championship");
                        String channel = offersObject.getString("channel");
                        String commentator = offersObject.getString("commentator");
                        String matchDateTime = offersObject.getString("Match_date_and_time");
                        String stadium = offersObject.getString("stadium");
                        String referee = offersObject.getString("referee");
                        String Formation = offersObject.getString("Formation");

                        // استخراج بيانات الفرق من كائن الفيديو الرئيسي
                        String team1Name = videoObject.getString("name_team_First");
                        String team1Img = videoObject.getString("First_img");
                        String team2Name = videoObject.getString("name_team_Second");
                        String team2Img = videoObject.getString("Second_img");
                        String countDownDate = videoObject.getString("countDownDate");
                        String iframe = videoObject.getString("iframe_1");
                        String iframe2 = videoObject.getString("iframe_2");
                        String iframe3 = videoObject.getString("iframe3");

                        // ملاحظة: ستحتاج إلى تحديث دالة createMatchLayout لتقبل هذه المتغيرات الجديدة
                        View matchLayout = createMatchLayout(
                                requireContext(),
                                type,
                                championship,
                                channel,
                                commentator,
                                matchDateTime,
                                stadium,
                                referee,
                                team1Name,
                                team1Img,
                                team2Name,
                                team2Img,
                                countDownDate,
                                Formation,
                                iframe,
                                iframe2,
                                iframe3
                        );

                        if (mainLayout != null) {
                            mainLayout.addView(matchLayout);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // يمكنك عرض رسالة خطأ هنا لكل عنصر يفشل تحليله بشكل منفصل
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(() -> {
                if (mainLayout != null) {
                    Log.e(TAG, "Error parsing video item: " + e.getMessage());
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    private ConstraintLayout createMatchLayout(Context context, String type, String championship, String channel,
                                               String commentator, String matchDateTime, String stadium,
                                               String referee ,
                                               String team1Name, String team1Img, String team2Name, String team2Img, String countDownDate, String Formation, String iframe, String iframe2, String iframe3) {
        ConstraintLayout layout = new ConstraintLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(0, 0, 0, 0);
        layout.setLayoutParams(layoutParams);


        // إنشاء تخطيط داخلي للمباراة (الفريق الأول vs الفريق الثاني)
        ConstraintLayout teamsLayout = new ConstraintLayout(context);
        teamsLayout.setId(View.generateViewId());
//        teamsLayout.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));
        layoutParams.setMargins(0, 0, 0, 0);
        teamsLayout.setBackgroundResource(R.drawable.container_t);
        teamsLayout.setPadding(16, 76, 16, 76);
        teamsLayout.setLayoutParams(layoutParams);
        teamsLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout timeLayout = new LinearLayout(context);
        timeLayout.setId(View.generateViewId());
        timeLayout.setOrientation(LinearLayout.VERTICAL);
//        timeLayout.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));
        timeLayout.setBackgroundResource(R.drawable.pattern);
//        timeLayout.setAlpha(0.22f);
        timeLayout.setGravity(Gravity.CENTER);
//        timeLayout.setLayoutParams(layoutParamswidth);
        LinearLayout.LayoutParams imgParamsimgtime = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600
        );
        imgParamsimgtime.setMargins(0, 0, 0, 0);
        timeLayout.setLayoutParams(imgParamsimgtime);
        teamsLayout.addView(timeLayout);



        TextView team1Names = new TextView(context);
        team1Names.setId(View.generateViewId());
        team1Names.setText(team1Name);
        team1Names.setTextSize(18);
        team1Names.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        team1Names.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
        int colorteam = ContextCompat.getColor(requireContext(), R.color.light_a);
        team1Names.setTextColor(colorteam);
        teamsLayout.addView(team1Names);

        ImageView team1Imgs = new ImageView(context);
        team1Imgs.setId(View.generateViewId());
        team1Imgs.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams imgParamsimg1 = new LinearLayout.LayoutParams(150, 150);
        imgParamsimg1.setMargins(0, 0, 0, 0);
        team1Imgs.setLayoutParams(imgParamsimg1);
        loadAndSetImage(team1Img, team1Imgs);
        teamsLayout.addView(team1Imgs);
        if (team1Imgs == null || iframe.trim().isEmpty()) {
            team1Imgs.setImageResource(R.drawable.football_team);
        }


//        ConstraintLayout.LayoutParams layoutParamst2 = new ConstraintLayout.LayoutParams(
//                widthInPixels,
//                heightInPixels
//        );
        TextView team2Names = new TextView(context);
        team2Names.setId(View.generateViewId());
        team2Names.setText(team2Name);
        team2Names.setTextSize(18);
        team2Names.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        team2Names.setTypeface(null, Typeface.BOLD);
        team2Names.setTextColor(colorteam);
        teamsLayout.addView(team2Names);

        ImageView team2Imgs = new ImageView(context);
        team2Imgs.setId(View.generateViewId());
        team2Imgs.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams imgParamsimg2 = new LinearLayout.LayoutParams(150, 150);
        imgParamsimg2.setMargins(0, 0, 0, 0);
        team2Imgs.setLayoutParams(imgParamsimg2);
        loadAndSetImage(team2Img, team2Imgs);
        teamsLayout.addView(team2Imgs);
        if (team2Imgs == null || iframe.trim().isEmpty()) {
            team2Imgs.setImageResource(R.drawable.football_team);
        }



        LinearLayout countdownLayout = new LinearLayout(context);
        countdownLayout.setId(View.generateViewId());
//        countdownLayout.setBackgroundColor(android.graphics.Color.parseColor("#CDDC39FF"));
        countdownLayout.setOrientation(LinearLayout.VERTICAL);
        countdownLayout.setGravity(Gravity.CENTER);
//        countdownLayout.setBackgroundResource(R.drawable.bottom_border);
        LinearLayout.LayoutParams imgParamsimgcountdown = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        imgParamsimgcountdown.setMargins(0, 0, 0, 0);
        countdownLayout.setLayoutParams(imgParamsimgcountdown);

        teamsLayout.addView(countdownLayout);

        TextView timerText = new TextView(context);
        timerText.setText("00:00:00");
        int colorteamprogressBar = ContextCompat.getColor(requireContext(), R.color.timebefore);
        timerText.setTextColor(colorteamprogressBar);
        timerText.setTextSize(28);
        timerText.setTypeface(null, Typeface.BOLD);
        timerText.setGravity(Gravity.CENTER);
        countdownLayout.addView(timerText);


        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(60); // مثلا دقيقة واحدة
//        progressBar.setProgress(160);
//        progressBar.setRotation(-90); // لجعله دائري يشبه الصورة
        int color = ContextCompat.getColor(requireContext(), R.color.progressbar);
        progressBar.setProgressTintList(ColorStateList.valueOf(color));
        countdownLayout.addView(progressBar);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                20
        );
        progressParams.setMargins(100, 0, 100, 0); // مسافة من فوق
        progressBar.setLayoutParams(progressParams);


// ---------------------  تشغيل العداد بناءً على countDownDate ---------------------
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.ENGLISH);
            Date targetDate = sdf.parse(countDownDate);
            long diff = targetDate.getTime() - System.currentTimeMillis();

            if (diff > 0) {
                new CountDownTimer(diff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000);
                        int days = seconds / (24 * 3600);
                        int hours = (seconds % (24 * 3600)) / 3600;
                        int minutes = (seconds % 3600) / 60;
                        int sec = seconds % 60;
                        progressBar.setProgress(minutes);
                        timerText.setText(String.format("%02d : %02d : %02d : %02d", days,hours,minutes,sec));
                    }

                    public void onFinish() {
//                        progressBar.setProgress(100);
                        timerText.setText("بدأت المباراة!");
                    }
                }.start();
            } else {
                timerText.setText("بدأت المباراة!");
                progressBar.setProgress(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            timerText.setText("تاريخ غير صالح");
        }




//        int widthInDp = 0;
//        int heightInDp = 100;
//        float scale = getResources().getDisplayMetrics().density;
//        int widthInPixels = (int) (widthInDp * scale + 0.5f);
//        int heightInPixels = (int) (heightInDp * scale + 0.5f);
//        ConstraintLayout.LayoutParams layoutParamswidth = new ConstraintLayout.LayoutParams(
//                widthInPixels,
//                heightInPixels
//        );
//        layoutParamswidth.setMargins(0, 0, 0, 0);
        LinearLayout newLayout = new LinearLayout(context);
        newLayout.setId(View.generateViewId());
//        newLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        newLayout.setOrientation(LinearLayout.VERTICAL);
        newLayout.setBackgroundResource(R.drawable.bottom_border);
        newLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        newLayout.setLayoutParams(layoutParams);
        newLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(newLayout);

        LinearLayout twoLayout = new LinearLayout(context);
        twoLayout.setId(View.generateViewId());
        twoLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        twoLayout.setOrientation(LinearLayout.VERTICAL);
        twoLayout.setBackgroundResource(R.drawable.bottom_border);
        twoLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        twoLayout.setLayoutParams(layoutParams);
        twoLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(twoLayout);

        LinearLayout thirdLayout = new LinearLayout(context);
        thirdLayout.setId(View.generateViewId());
//        thirdLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        thirdLayout.setOrientation(LinearLayout.VERTICAL);
        thirdLayout.setBackgroundResource(R.drawable.bottom_border);
        thirdLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        thirdLayout.setLayoutParams(layoutParams);
        thirdLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(thirdLayout);

        LinearLayout fourthLayout = new LinearLayout(context);
        fourthLayout.setId(View.generateViewId());
//        fourthLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        fourthLayout.setOrientation(LinearLayout.VERTICAL);
        fourthLayout.setBackgroundResource(R.drawable.bottom_border);
        fourthLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        fourthLayout.setLayoutParams(layoutParams);
        fourthLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(fourthLayout);

        LinearLayout FiveLayout = new LinearLayout(context);
        FiveLayout.setId(View.generateViewId());
//        FiveLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        FiveLayout.setOrientation(LinearLayout.VERTICAL);
        FiveLayout.setBackgroundResource(R.drawable.bottom_border);

        FiveLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        FiveLayout.setLayoutParams(layoutParams);
        FiveLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(FiveLayout);


        LinearLayout sevenLayout = new LinearLayout(context);
        sevenLayout.setId(View.generateViewId());
//        sevenLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        sevenLayout.setOrientation(LinearLayout.VERTICAL);
        sevenLayout.setBackgroundResource(R.drawable.bottom_border);
        sevenLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        sevenLayout.setLayoutParams(layoutParams);
        sevenLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(sevenLayout);



        LinearLayout NinthLayout = new LinearLayout(context);
        NinthLayout.setId(View.generateViewId());
        NinthLayout.setBackgroundColor(android.graphics.Color.parseColor("#C1980E0E"));
        NinthLayout.setOrientation(LinearLayout.VERTICAL);
        NinthLayout.setBackgroundResource(R.drawable.bottom_border);
        NinthLayout.setGravity(Gravity.CENTER);
//        newLayout.setLayoutParams(layoutParamswidth);
        layoutParams.setMargins(0, 0, 0, 0);
        NinthLayout.setLayoutParams(layoutParams);
        NinthLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        teamsLayout.addView(NinthLayout);

        // إنشاء TextView للدوري
        TextView types = new TextView(context);
        types.setId(View.generateViewId());
        types.setText(type);
        types.setTextSize(18);
        types.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        types.setGravity(Gravity.CENTER_HORIZONTAL);
        types.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
        int colors = ContextCompat.getColor(requireContext(), R.color.lightColor);
        types.setTextColor(colors);
        newLayout.addView(types);

        TextView championships = new TextView(context);
        championships.setId(View.generateViewId());
        championships.setText(championship);
        championships.setTextSize(18);
        championships.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        championships.setGravity(Gravity.END);
        championships.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        championships.setTextColor(colors);
        twoLayout.addView(championships);

        TextView channels = new TextView(context);
        channels.setId(View.generateViewId());
        channels.setText(channel);
        channels.setTextSize(18);
        channels.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        channels.setGravity(Gravity.END);
        channels.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        channels.setTextColor(colors);
        thirdLayout.addView(channels);

        TextView commentators = new TextView(context);
        commentators.setId(View.generateViewId());
        commentators.setText(commentator);
        commentators.setTextSize(18);
        commentators.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        commentators.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        commentators.setTextColor(colors);
        fourthLayout.addView(commentators);

        TextView matchDateTimes = new TextView(context);
        matchDateTimes.setId(View.generateViewId());
        matchDateTimes.setText(matchDateTime);
        matchDateTimes.setTextSize(18);
        matchDateTimes.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        matchDateTimes.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        matchDateTimes.setTextColor(colors);
        FiveLayout.addView(matchDateTimes);

        TextView stadiums = new TextView(context);
        stadiums.setId(View.generateViewId());
        stadiums.setText(stadium);
        stadiums.setTextSize(18);
        stadiums.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        stadiums.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        stadiums.setTextColor(colors);
        sevenLayout.addView(stadiums);

        TextView referees = new TextView(context);
        referees.setId(View.generateViewId());
        referees.setText(referee);
        referees.setTextSize(18);
        referees.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        referees.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        referees.setTextColor(Color.BLACK);
        referees.setTextColor(colors);
        NinthLayout.addView(referees);


        ImageView Formations = new ImageView(context);
        Formations.setId(View.generateViewId());
        Formations.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams imgParamsimgFormation = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
        );
        imgParamsimgFormation.setMargins(0, 0, 0, 0);
        Formations.setLayoutParams(imgParamsimgFormation);
        loadAndSetImage(Formation, Formations);
        teamsLayout.addView(Formations);
        if (Formations == null || iframe.trim().isEmpty()) {
            Formations.setImageResource(R.drawable.screenshot);
        }

// ---------------- WebView لتشغيل الفيديو بوضع ملء الشاشة ----------------
        WebView webView = new WebView(context);
        webView.setId(View.generateViewId());
//        webView.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));

// إعدادات WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            private View customView;
            private WebChromeClient.CustomViewCallback customViewCallback;
            private int originalSystemUiVisibility;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;

                // الدخول في وضع ملء الشاشة
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                originalSystemUiVisibility = decor.getSystemUiVisibility();
                decor.addView(customView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            @Override
            public void onHideCustomView() {
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
                customViewCallback.onCustomViewHidden();
                customViewCallback = null;
            }
        });

        ConstraintLayout.LayoutParams webParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
        );
        webView.setLayoutParams(webParams);
        webView.loadUrl(iframe);
        teamsLayout.addView(webView);


        WebView webView2 = new WebView(context);
        webView2.setId(View.generateViewId());
        webView2.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));
        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);
        webSettings2.setDomStorageEnabled(true);
        webSettings2.setMediaPlaybackRequiresUserGesture(false);
        webSettings2.setLoadWithOverviewMode(true);
        webSettings2.setUseWideViewPort(true);
        webView2.setVisibility(View.GONE);
        webView2.setWebViewClient(new WebViewClient());

        webView2.setWebChromeClient(new WebChromeClient() {
            private View customView;
            private WebChromeClient.CustomViewCallback customViewCallback;
            private int originalSystemUiVisibility;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;

                // الدخول في وضع ملء الشاشة
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                originalSystemUiVisibility = decor.getSystemUiVisibility();
                decor.addView(customView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            @Override
            public void onHideCustomView() {
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
                customViewCallback.onCustomViewHidden();
                customViewCallback = null;
            }
        });

        ConstraintLayout.LayoutParams webParams2 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
        );
        webView2.setLayoutParams(webParams2);
        webView2.loadUrl(iframe2);
        teamsLayout.addView(webView2);


        WebView webView3 = new WebView(context);
        webView3.setId(View.generateViewId());
        webView3.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));
        WebSettings webSettings3 = webView3.getSettings();
        webSettings3.setJavaScriptEnabled(true);
        webSettings3.setDomStorageEnabled(true);
        webSettings3.setMediaPlaybackRequiresUserGesture(false);
        webSettings3.setLoadWithOverviewMode(true);
        webSettings3.setUseWideViewPort(true);
        webView3.setVisibility(View.GONE);
        webView3.setWebViewClient(new WebViewClient());

        webView3.setWebChromeClient(new WebChromeClient() {
            private View customView;
            private WebChromeClient.CustomViewCallback customViewCallback;
            private int originalSystemUiVisibility;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;

                // الدخول في وضع ملء الشاشة
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                originalSystemUiVisibility = decor.getSystemUiVisibility();
                decor.addView(customView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            @Override
            public void onHideCustomView() {
                ViewGroup decor = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
                customViewCallback.onCustomViewHidden();
                customViewCallback = null;
            }
        });

        ConstraintLayout.LayoutParams webParams3 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
        );
        webView3.setLayoutParams(webParams3);
        webView3.loadUrl(iframe3);
        teamsLayout.addView(webView3);


// إنشاء الزر
        Button showWebViewButton = new Button(context);
        showWebViewButton.setText(R.string.showWebViewButton1);
        showWebViewButton.setTextSize(12);
        showWebViewButton.setId(View.generateViewId());
        showWebViewButton.setBackgroundResource(R.drawable.btn_web_video);
        showWebViewButton.setPadding(20, 0, 20, 0);
        float pxw = context.getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (10 * pxw);
        showWebViewButton.setCompoundDrawablePadding(paddingInPixels);
        showWebViewButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.server_icon, 0);
        ConstraintLayout.LayoutParams btnm1 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        final int MARGIN_btn1 = -280;
        float density = context.getResources().getDisplayMetrics().density;
        int marginbtn1Pixels = (int) (MARGIN_btn1 * density);
        btnm1.setMargins(marginbtn1Pixels, 0, 0, 0);
        btnm1.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        btnm1.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        showWebViewButton.setLayoutParams(btnm1);
        teamsLayout.addView(showWebViewButton);


        Button showWebViewButton2 = new Button(context);
        showWebViewButton2.setText(R.string.showWebViewButton2);
        showWebViewButton2.setTextSize(12);
        showWebViewButton2.setId(View.generateViewId());
        showWebViewButton2.setBackgroundResource(R.drawable.btn_web_video);
        showWebViewButton2.setPadding(20, 0, 20, 0);
        float pxw2 = context.getResources().getDisplayMetrics().density;
        int paddingInPixels2 = (int) (10 * pxw2);
        showWebViewButton2.setCompoundDrawablePadding(paddingInPixels2);
        showWebViewButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.server_icon, 0);
        ConstraintLayout.LayoutParams btnm2 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        final int MARGIN_btn2 = 0;
        float density2 = context.getResources().getDisplayMetrics().density;
        int marginbtn2Pixels = (int) (MARGIN_btn2 * density2);
        btnm2.setMargins(marginbtn2Pixels, 0, 0, 0);
        btnm2.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        btnm2.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm2.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        showWebViewButton2.setLayoutParams(btnm2);
        teamsLayout.addView(showWebViewButton2);


        Button showWebViewButton3 = new Button(context);
        showWebViewButton3.setText(R.string.showWebViewButton3);
        showWebViewButton3.setTextSize(12);
        showWebViewButton3.setId(View.generateViewId());
        showWebViewButton3.setBackgroundResource(R.drawable.btn_web_video);
        showWebViewButton3.setPadding(20, 0, 20, 0);
        float pxw3 = context.getResources().getDisplayMetrics().density;
        int paddingInPixels3 = (int) (10 * pxw3);
        showWebViewButton3.setCompoundDrawablePadding(paddingInPixels3);
        showWebViewButton3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.server_icon, 0);
        ConstraintLayout.LayoutParams btnm3 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        final int MARGIN_btn3 = 280;
        float density3 = context.getResources().getDisplayMetrics().density;
        int marginbtn3Pixels = (int) (MARGIN_btn3 * density3);
        btnm3.setMargins(marginbtn3Pixels, 0, 0, 0);
        btnm3.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        btnm3.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm3.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//        btnm3.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        showWebViewButton3.setLayoutParams(btnm3);
        teamsLayout.addView(showWebViewButton3);





        showWebViewButton.setOnClickListener(v -> {
            if (webView.getVisibility() == View.VISIBLE) {
                webView.setVisibility(View.VISIBLE);
                webView2.setVisibility(View.GONE);
                webView3.setVisibility(View.GONE);
                showWebViewButton.setText("Hide video1");
            } else {
                webView.setVisibility(View.VISIBLE);
                webView2.setVisibility(View.GONE);
                webView3.setVisibility(View.GONE);
                showWebViewButton.setText("Hide video1");
                showWebViewButton2.setText("Show video2");
                showWebViewButton3.setText("Show video3");
            }
        });
        showWebViewButton2.setOnClickListener(v -> {
            if (webView2.getVisibility() == View.VISIBLE) {
                webView.setVisibility(View.GONE);
                webView2.setVisibility(View.VISIBLE);
                webView3.setVisibility(View.GONE);
                showWebViewButton2.setText("Hide video2");
            } else {
                webView2.setVisibility(View.VISIBLE);
                webView3.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                showWebViewButton2.setText("Hide video2");
                showWebViewButton.setText("Show video2");
                showWebViewButton3.setText("Show video");
            }
        });
        showWebViewButton3.setOnClickListener(v -> {
            if (webView3.getVisibility() == View.VISIBLE) {
                webView.setVisibility(View.GONE);
                webView2.setVisibility(View.GONE);
                webView3.setVisibility(View.VISIBLE);
                showWebViewButton3.setText("Hide video3");

            } else {
                webView3.setVisibility(View.VISIBLE);
                webView2.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                showWebViewButton3.setText("Hide video3");
                showWebViewButton2.setText("Show video2");
                showWebViewButton.setText("Show video");
            }
        });



        // تطبيق القيود (Constraints)
        ConstraintSet teamsSet = new ConstraintSet();
        teamsSet.clone(teamsLayout);
        teamsSet.applyTo(teamsLayout);

        if (iframe == null || iframe.trim().isEmpty()) {
            showWebViewButton.setText("No Server");
            showWebViewButton.setEnabled(false);
        }

        if (iframe2 == null || iframe2.trim().isEmpty()) {
            showWebViewButton2.setText("No Server");
            showWebViewButton2.setEnabled(false);
        }

        if (iframe3 == null || iframe3.trim().isEmpty()) {
            showWebViewButton3.setText("No Server");
            showWebViewButton3.setEnabled(false);
        }

//        teamsSet.connect(types.getId(), ConstraintSet.START, newLayout.getId(), ConstraintSet.END,  110);
//        teamsSet.connect(types.getId(), ConstraintSet.TOP, teamsLayout.getId(), ConstraintSet.TOP, 170);
//
        teamsSet.connect(team1Names.getId(), ConstraintSet.END, team1Imgs.getId(), ConstraintSet.START,  -200);
        teamsSet.connect(team1Names.getId(), ConstraintSet.START, team1Imgs.getId(), ConstraintSet.END,  -200);
        teamsSet.connect(team1Names.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 250);
//
        teamsSet.connect(team2Names.getId(), ConstraintSet.END, team2Imgs.getId(), ConstraintSet.START,  -200);
        teamsSet.connect(team2Names.getId(), ConstraintSet.START, team2Imgs.getId(), ConstraintSet.END,  -200);
        teamsSet.connect(team2Names.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 250);
////
        teamsSet.connect(team1Imgs.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  -300);
        teamsSet.connect(team1Imgs.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 90);
//
        teamsSet.connect(team2Imgs.getId(), ConstraintSet.START, teamsLayout.getId(), ConstraintSet.END,  -300);
        teamsSet.connect(team2Imgs.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 90);
//
        //
        teamsSet.connect(countdownLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(countdownLayout.getId(), ConstraintSet.TOP, teamsLayout.getId(), ConstraintSet.TOP, 400);
//

        //
        teamsSet.connect(newLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(newLayout.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 650);
//

        teamsSet.connect(twoLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(twoLayout.getId(), ConstraintSet.TOP, newLayout.getId(), ConstraintSet.TOP, 100);
//

        teamsSet.connect(thirdLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(thirdLayout.getId(), ConstraintSet.TOP, twoLayout.getId(), ConstraintSet.TOP, 100);

        teamsSet.connect(fourthLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(fourthLayout.getId(), ConstraintSet.TOP, thirdLayout.getId(), ConstraintSet.TOP, 100);


        teamsSet.connect(FiveLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(FiveLayout.getId(), ConstraintSet.TOP, fourthLayout.getId(), ConstraintSet.TOP, 100);

        teamsSet.connect(sevenLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(sevenLayout.getId(), ConstraintSet.TOP, FiveLayout.getId(), ConstraintSet.TOP, 100);

        teamsSet.connect(NinthLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.END,  0);
        teamsSet.connect(NinthLayout.getId(), ConstraintSet.TOP, sevenLayout.getId(), ConstraintSet.TOP, 100);

        //
        teamsSet.connect(Formations.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(Formations.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 1300);
//

//        teamsSet.connect(showWebViewButton.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.START,  100);
        teamsSet.connect(showWebViewButton.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2150);
        teamsSet.connect(showWebViewButton2.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2150);
        teamsSet.connect(showWebViewButton3.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2150);

        teamsSet.connect(webView.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(webView.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2300);

        teamsSet.connect(webView2.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(webView2.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2300);

        teamsSet.connect(webView3.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(webView3.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2300);

//



        // إضافة التخطيط الداخلي وتكمله إلى التخطيط الرئيسي
        layout.addView(teamsLayout);
        teamsSet.applyTo(teamsLayout);

        // تطبيق القيود (Constraints) على التخطيط الرئيسي
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);



        mainSet.applyTo(layout);

        return layout;

    }

    private void loadAndSetImage(String imageUrlString, ImageView imageView) {
        new Thread(() -> {
            HttpURLConnection imageConnection = null;
            InputStream imageStream = null;
            try {
                URL imageUrl = new URL(imageUrlString);
                imageConnection = (HttpURLConnection) imageUrl.openConnection();
                imageConnection.connect();
                imageStream = imageConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    imageView.setImageBitmap(bitmap);
                });

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error on the main thread
                requireActivity().runOnUiThread(() -> {
//                    imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                });
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing image stream: " + e.getMessage());
                    }
                }
                if (imageConnection != null) {
                    imageConnection.disconnect();
                }
            }
        }).start();
    }



}

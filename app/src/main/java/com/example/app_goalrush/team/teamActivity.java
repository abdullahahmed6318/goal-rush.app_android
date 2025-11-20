package com.example.app_goalrush.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class teamActivity extends AppCompatActivity {
    private static final String TAG = "TeamActivity";
    private WebView webView;
    private TextView titleTextView;
    private RequestQueue requestQueue; // كائن Volley
    // تم تحديث الرابط لاستخدام Netlify، وهو خيار أفضل للاستضافة
    private static final String JSON_URL = "https://goal-rush.netlify.app/videos.json";
    private WebView myWebView;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    // متغيرات دعم الضغط المزدوج (Double Tap)
    private boolean isFullScreenMode = false;
    private GestureDetector gestureDetector;
    private ViewGroup.LayoutParams originalWebViewParams;
    private LinearLayout mainLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_team);

        mainLayout = findViewById(R.id.main_team_layout);
        // الرابط لجلب بيانات JSON منه
        String jsonUrl = "https://goal-rush.netlify.app/country/Egypt/Egypt.json";
        fetchJsonFromUrl(jsonUrl);


    }


    private void fetchJsonFromUrl(String urlString) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // تنفيذ طلب الشبكة في الخلفية
        executor.execute(() -> {
            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonResult.append(line).append("\n");
                    }
                    bufferedReader.close();
                    // عند النجاح، قم بتمرير البيانات المحملة إلى دالة التحليل
                    parseAndDisplayJson(jsonResult.toString());
//                    parseAndDisplaysJson(jsonResult.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // في حالة حدوث خطأ في الشبكة، قم بعرض رسالة خطأ على واجهة المستخدم
                runOnUiThread(() -> {
                    if (mainLayout != null) {
                        TextView errorTextView = new TextView(this);
                        errorTextView.setText("فشل تحميل البيانات من الرابط.");
                        mainLayout.addView(errorTextView);
                    }
                });
            }
        });
    }


    private void parseAndDisplayJson(String jsonString) {
        try {
            JSONObject mainJsonObject = new JSONObject(jsonString);
            JSONArray videosArray = mainJsonObject.getJSONArray("videos");

            runOnUiThread(() -> {
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

                        // ملاحظة: ستحتاج إلى تحديث دالة createMatchLayout لتقبل هذه المتغيرات الجديدة
                        ConstraintLayout matchLayout = createMatchLayout(
                                this,
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
                                iframe
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
            runOnUiThread(() -> {
                if (mainLayout != null) {
                    TextView errorTextView = new TextView(this);
                    errorTextView.setText("حدث خطأ أثناء تحليل بيانات JSON.");
                    mainLayout.addView(errorTextView);
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    private ConstraintLayout createMatchLayout(Context context, String type, String championship, String channel,
                                               String commentator, String matchDateTime, String stadium,
                                               String referee ,
                                               String team1Name, String team1Img, String team2Name, String team2Img, String countDownDate, String Formation, String iframe) {
        // إنشاء تخطيط ConstraintLayout جديد
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
        int colorteam = ContextCompat.getColor(this, R.color.light_a);
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
        timerText.setTextColor(colorteam);
        timerText.setTextSize(28);
        timerText.setTypeface(null, Typeface.BOLD);
        timerText.setGravity(Gravity.CENTER);
        countdownLayout.addView(timerText);


        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(60); // مثلا دقيقة واحدة
//        progressBar.setProgress(160);
//        progressBar.setRotation(-90); // لجعله دائري يشبه الصورة
        int color = ContextCompat.getColor(this, R.color.light_a);
        progressBar.setProgressTintList(ColorStateList.valueOf(color));
        countdownLayout.addView(progressBar);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                20
        );
        progressParams.setMargins(50, 0, 50, 0); // مسافة من فوق
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
        types.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        types.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
        int colors = ContextCompat.getColor(this, R.color.lightColor);
        types.setTextColor(colors);
        newLayout.addView(types);

        TextView championships = new TextView(context);
        championships.setId(View.generateViewId());
        championships.setText(championship);
        championships.setTextSize(18);
        championships.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        championships.setTypeface(null, Typeface.BOLD);
        layoutParams.setMargins(0, 0, 0, 0);
//        int colorteam2Names = ContextCompat.getColor(this, R.color.lightColor);
        championships.setTextColor(colors);
        twoLayout.addView(championships);

        TextView channels = new TextView(context);
        channels.setId(View.generateViewId());
        channels.setText(channel);
        channels.setTextSize(18);
        channels.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
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

// ---------------- WebView لتشغيل الفيديو بوضع ملء الشاشة ----------------
        WebView webView = new WebView(context);
        webView.setId(View.generateViewId());
        webView.setBackgroundColor(android.graphics.Color.parseColor("#FFC107FF"));

// إعدادات WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient());



// إنشاء الزر
        Button showWebViewButton = new Button(context);
        showWebViewButton.setText("Server1");
        showWebViewButton.setId(View.generateViewId());
        showWebViewButton.setBackgroundResource(R.drawable.btn_web_video);
        showWebViewButton.setPadding(26, 0, 26, 0);
        showWebViewButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_icon, 0, 0, 0);
        // إعداد مكان الزر
        ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
//        buttonParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.bottomMargin = 60;
        showWebViewButton.setLayoutParams(buttonParams);

        // أضف الحدث عند الضغط
        showWebViewButton.setOnClickListener(v -> {
            if (webView.getVisibility() == View.VISIBLE) {
                webView.setVisibility(View.GONE);
                showWebViewButton.setText("إظهار الفيديو"); // غيّر النص
            } else {
                webView.setVisibility(View.VISIBLE);
                showWebViewButton.setText("إخفاء الفيديو");
            }
        });
        teamsLayout.addView(showWebViewButton);


// التعامل مع الفيديوهات بوضع ملء الشاشة
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

// حجم WebView: ياخد الشاشة كلها
        ConstraintLayout.LayoutParams webParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
        );
        webView.setLayoutParams(webParams);

// تحميل الرابط (من JSON)
        webView.loadUrl(iframe);

// إضافة الـ WebView داخل التخطيط الرئيسي
        teamsLayout.addView(webView);





        // تطبيق القيود (Constraints)
        ConstraintSet teamsSet = new ConstraintSet();
        teamsSet.clone(teamsLayout);
        teamsSet.applyTo(teamsLayout);

//        teamsSet.connect(types.getId(), ConstraintSet.START, newLayout.getId(), ConstraintSet.END,  110);
//        teamsSet.connect(types.getId(), ConstraintSet.TOP, teamsLayout.getId(), ConstraintSet.TOP, 170);
//
        teamsSet.connect(team1Names.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  -300);
        teamsSet.connect(team1Names.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 250);
//
        teamsSet.connect(team2Names.getId(), ConstraintSet.START, teamsLayout.getId(), ConstraintSet.END,  -280);
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

//        teamsSet.connect(showWebViewButton.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(showWebViewButton.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2150);

        teamsSet.connect(webView.getId(), ConstraintSet.END, teamsLayout.getId(), ConstraintSet.START,  0);
        teamsSet.connect(webView.getId(), ConstraintSet.TOP, timeLayout.getId(), ConstraintSet.TOP, 2300);
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
            try {
                URL imageUrl = new URL(imageUrlString);
                HttpURLConnection imageConnection = (HttpURLConnection) imageUrl.openConnection();
                imageConnection.connect();
                InputStream imageStream = imageConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                runOnUiThread(() -> {
                    imageView.setImageBitmap(bitmap);
                });

                imageStream.close();
                imageConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                });
            }
        }).start();
    }







}
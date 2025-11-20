package com.example.app_goalrush.json;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class MainActivityaa extends AppCompatActivity {
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchJsonFromUrl("https://goal-rush.netlify.app/json/items_today.json");
        mainLayout = findViewById(R.id.main_linear_layout);

    }


    private void fetchJsonFromUrl(String urlString) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

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

                parseAndDisplayJson(jsonString);

            } catch (IOException e) {
                Log.e(TAG, "Error fetching JSON data", e);
                runOnUiThread(() -> {
                    TextView errorTextView = new TextView(this);
                    errorTextView.setText("حدث خطأ في الاتصال بالشبكة. 😞");
                    errorTextView.setGravity(Gravity.CENTER);
                    mainLayout.addView(errorTextView);
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
            }
        }).start();
    }

    private void parseAndDisplayJson(String jsonString) {
        try {
            JSONObject mainJsonObject = new JSONObject(jsonString);
            JSONArray videosArray = mainJsonObject.getJSONArray("videos");

            runOnUiThread(() -> {
                for (int i = 0; i < videosArray.length(); i++) {
                    try {
                        JSONObject videoObject = videosArray.getJSONObject(i);
                        String league = videoObject.getString("side_bar_league");
                        String teamNameFirst = videoObject.getString("name_team_First");
                        String teamImgFirst = videoObject.getString("First_img");
                        String teamNameSecond = videoObject.getString("name_team_Second");
                        String teamImgSecond = videoObject.getString("Second_img");
                        String anotherActivityFile = videoObject.getString("another_activity_file");

                        // Create and add a new layout for each match
                        LinearLayout matchLayout = createMatchLayout(this, league, teamNameFirst, teamImgFirst, teamNameSecond, teamImgSecond, anotherActivityFile);
                        mainLayout.addView(matchLayout);

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON object", e);
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing main JSON array", e);
            runOnUiThread(() -> {
                TextView errorTextView = new TextView(this);
                errorTextView.setText("حدث خطأ أثناء تحليل بيانات JSON.");
                errorTextView.setGravity(Gravity.CENTER);
                mainLayout.addView(errorTextView);
            });
        }
    }

    private LinearLayout createMatchLayout(Context context, String league, String team1Name, String team1ImgUrl, String team2Name, String team2ImgUrl, String activityFileName) {
        // Main container for the match
        LinearLayout matchContainer = new LinearLayout(context);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, 32, 0, 32); // Add some vertical spacing
        matchContainer.setLayoutParams(containerParams);
        matchContainer.setOrientation(LinearLayout.VERTICAL);
        matchContainer.setPadding(16, 16, 16, 16);
        matchContainer.setBackgroundColor(0xFFDDDDDD); // Gray background

        // League TextView
        TextView leagueTextView = new TextView(context);
        leagueTextView.setText(league);
        leagueTextView.setTextSize(18);
        leagueTextView.setTypeface(null, Typeface.BOLD);
        leagueTextView.setGravity(Gravity.END);
        leagueTextView.setTextColor(android.graphics.Color.BLACK);
        matchContainer.addView(leagueTextView);

        // Teams Layout
        LinearLayout teamsLayout = new LinearLayout(context);
        teamsLayout.setOrientation(LinearLayout.HORIZONTAL);
        teamsLayout.setGravity(Gravity.CENTER);
        teamsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Team 1
        LinearLayout team1Layout = createTeamView(context, team1Name, team1ImgUrl);
        teamsLayout.addView(team1Layout);

        // "vs" TextView
        TextView vsTextView = new TextView(context);
        vsTextView.setText("vs");
        vsTextView.setTextSize(24);
        vsTextView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams vsParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        vsParams.setMargins(16, 0, 16, 0);
        vsTextView.setLayoutParams(vsParams);
        teamsLayout.addView(vsTextView);

        // Team 2
        LinearLayout team2Layout = createTeamView(context, team2Name, team2ImgUrl);
        teamsLayout.addView(team2Layout);

        matchContainer.addView(teamsLayout);

        // Set up click listener to handle navigation
        matchContainer.setOnClickListener(v -> {
            try {
                // Get the class from the file name and remove ".java" if it exists
                String className = activityFileName.replace(".java", "");
                Class<?> anotherActivityClass = Class.forName(getPackageName() + "." + className);
                Intent intent = new Intent(context, anotherActivityClass);
                intent.putExtra("LEAGUE_NAME", league);
                intent.putExtra("TEAM1_NAME", team1Name);
                intent.putExtra("TEAM2_NAME", team2Name);
                intent.putExtra("TEAM1_IMG", team1ImgUrl);
                intent.putExtra("TEAM2_IMG", team2ImgUrl);
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Error: AnotherActivity.java class not found. Make sure the file name in JSON is correct.", e);
            }
        });

        return matchContainer;
    }

    private LinearLayout createTeamView(Context context, String teamName, String teamImgUrl) {
        LinearLayout teamLayout = new LinearLayout(context);
        teamLayout.setOrientation(LinearLayout.VERTICAL);
        teamLayout.setGravity(Gravity.CENTER);

        // ImageView for team logo
        ImageView teamImageView = new ImageView(context);
        teamImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200)); // Fixed size for logo
        loadAndSetImage(teamImgUrl, teamImageView);
        teamLayout.addView(teamImageView);

        // TextView for team name
        TextView teamNameView = new TextView(context);
        teamNameView.setText(teamName);
        teamNameView.setTextSize(16);
        teamNameView.setTypeface(null, Typeface.BOLD);
        teamNameView.setGravity(Gravity.CENTER);
        teamNameView.setPadding(0, 8, 0, 0);
        teamLayout.addView(teamNameView);

        return teamLayout;
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
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    }
                });
                imageStream.close();
                imageConnection.disconnect();
            } catch (IOException e) {
                Log.e(TAG, "Error loading image from URL", e);
                runOnUiThread(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                });
            }
        }).start();
    }



}


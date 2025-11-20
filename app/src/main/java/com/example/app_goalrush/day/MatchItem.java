package com.example.app_goalrush.day;

import java.io.Serializable;

/**
 * نموذج البيانات (Model) لتمثيل معلومات المباراة الواحدة.
 * هذا الكلاس يسهل تمرير البيانات بين الكود الخلفي والواجهة (Adapter).
 */
public class MatchItem implements Serializable {
    public final String league;
    public final String team1Name;
    public final String team1ImgUrl;
    public final String team2Name;
    public final String team2ImgUrl;
    public final String teamNamethird3;
    public final String teamImgthird3;
    public final String teamNamefourth4;
    public final String teamImgfourth4;
    public final String teamNameFive5;
    public final String teamImgFive5;
    public final String teamNamesixth6;
    public final String teamImgsixth6;
    public final String teamNameseven7;
    public final String teamImgseven7;
    public final String teamNameeighth8;
    public final String teamImgeighth8;
    public final String teamNameNinth9;
    public final String teamImgNinth9;
    public final String teamNametenth10;
    public final String teamImgtenth10;
    public final String activityFileName;
    public final String activityFile2Name;
    public final String activityFile3Name;
    public final String activityFile4Name;
    public final String activityFile5Name;



    // مُنشئ الكلاس (Constructor)
    public MatchItem(String league, String team1Name, String team1ImgUrl, String team2Name, String team2ImgUrl,
                     String teamNamethird3, String teamImgthird3, String teamNamefourth4, String teamImgfourth4,
                     String teamNameFive5, String teamImgFive5, String teamNamesixth6, String teamImgsixth6,
                     String teamNameseven7, String teamImgseven7, String teamNameeighth8, String teamImgeighth8,
                     String teamNameNinth9, String teamImgNinth9, String teamNametenth10, String teamImgtenth10,
                     String activityFileName, String activityFile2Name , String activityFile3Name , String activityFile4Name , String activityFile5Name) {
        this.league = league;
        this.team1Name = team1Name;
        this.team1ImgUrl = team1ImgUrl;
        this.team2Name = team2Name;
        this.team2ImgUrl = team2ImgUrl;
        this.teamNamethird3 = teamNamethird3;
        this.teamImgthird3 = teamImgthird3;
        this.teamNamefourth4 = teamNamefourth4;
        this.teamImgfourth4 = teamImgfourth4;
        this.teamNameFive5 = teamNameFive5;
        this.teamImgFive5 = teamImgFive5;
        this.teamNamesixth6 = teamNamesixth6;
        this.teamImgsixth6 = teamImgsixth6;
        this.teamNameseven7 = teamNameseven7;
        this.teamImgseven7 = teamImgseven7;
        this.teamNameeighth8 = teamNameeighth8;
        this.teamImgeighth8 = teamImgeighth8;
        this.teamNameNinth9 = teamNameNinth9;
        this.teamImgNinth9 = teamImgNinth9;
        this.teamNametenth10 = teamNametenth10;
        this.teamImgtenth10 = teamImgtenth10;
        this.activityFileName = activityFileName;
        this.activityFile2Name = activityFile2Name;
        this.activityFile3Name = activityFile2Name;
        this.activityFile4Name = activityFile2Name;
        this.activityFile5Name = activityFile2Name;

    }
}

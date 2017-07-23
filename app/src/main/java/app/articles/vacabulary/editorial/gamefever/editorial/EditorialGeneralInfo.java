package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;

import java.util.Calendar;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfo {
    private String editorialHeading, editorialDate, editorialSource, editorialID, editorialSubHeading, editorialTag, editorialCategory;
    private int editorialSourceIndex;
    private long timeInMillis;

    public EditorialGeneralInfo() {
    }

    public EditorialGeneralInfo(String editorialHeading, String editorialTag, String editorialSubHeading, String editorialID, String editorialSource, String editorialDate) {
        this.editorialHeading = editorialHeading;
        this.editorialTag = editorialTag;
        this.editorialSubHeading = editorialSubHeading;
        this.editorialID = editorialID;
        this.editorialSource = editorialSource;
        this.editorialDate = editorialDate;
    }


    public String getEditorialHeading() {
        return editorialHeading;
    }

    public void setEditorialHeading(String editorialHeading) {
        this.editorialHeading = editorialHeading;
    }

    public String getEditorialDate() {
        return editorialDate;
    }

    public void setEditorialDate(String editorialDate) {
        this.editorialDate = editorialDate;
    }

    public String getEditorialSource() {
        return editorialSource;
    }

    public void setEditorialSource(String editorialSource) {
        this.editorialSource = editorialSource;
    }

    public String getEditorialID() {
        return editorialID;
    }

    public void setEditorialID(String editorialID) {
        this.editorialID = editorialID;
    }

    public String getEditorialSubHeading() {
        return editorialSubHeading;
    }

    public void setEditorialSubHeading(String editorialSubHeading) {
        this.editorialSubHeading = editorialSubHeading;
    }

    public String getEditorialTag() {
        return editorialTag;
    }

    public void setEditorialTag(String editorialTag) {
        this.editorialTag = editorialTag;
    }

    public String getEditorialCategory() {
        return editorialCategory;
    }

    public void setEditorialCategory(String editorialCategory) {
        this.editorialCategory = editorialCategory;
    }

    public int getEditorialSourceIndex() {
        return editorialSourceIndex;
    }

    public void setEditorialSourceIndex(int editorialSourceIndex) {
        this.editorialSourceIndex = editorialSourceIndex;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }


    public static String resolveDate(long editorialTime) {

        Calendar calendar = Calendar.getInstance();


        long currenttime = calendar.getTimeInMillis();


        //calculate difference in time
        //long timeDifference = (currenttime - newsTime);

        if ((currenttime - editorialTime) <= 0 || editorialTime <= 1493013649175l) {
            return "";
        }

        long numberOfHour = (currenttime - editorialTime) / 3600000;
        if (numberOfHour == 0) {
            return "hour ago";
        } else if (numberOfHour < 24) {
            return String.valueOf(numberOfHour) + " hour ago";
        } else {

            long numberOfDays = numberOfHour / 24;

            if (numberOfDays < 7) {
                return String.valueOf(numberOfDays) + " day ago";
            } else {

                long numberOfWeek = numberOfDays / 7;
                if (numberOfWeek <= 4) {
                    return String.valueOf(numberOfWeek) + " week ago";
                } else {

                    long numberOfMonth = numberOfWeek / 4;
                    if (numberOfMonth <= 12) {
                        return String.valueOf(numberOfMonth) + " month ago";
                    } else {

                        long numberOfYear = numberOfMonth / 12;

                        return String.valueOf(numberOfYear) + " year ago";

                    }

                }

            }

        }


    }


}

package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfo implements Serializable {
    private String editorialHeading, editorialDate, editorialSource, editorialID, editorialSubHeading, editorialTag, editorialCategory ,editorialSourceLink;
    private int editorialSourceIndex ,editorialLike ,editorialCategoryIndex;
    private long timeInMillis;
    private String editorialImageUrl;
    boolean mustRead, readStatus;

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

    public String getEditorialSourceLink() {
        return editorialSourceLink;
    }

    public void setEditorialSourceLink(String editorialSourceLink) {
        this.editorialSourceLink = editorialSourceLink;
    }

    public int getEditorialLike() {
        return editorialLike;
    }

    public void setEditorialLike(int editorialLike) {
        this.editorialLike = editorialLike;
    }

    public int getEditorialCategoryIndex() {
        return editorialCategoryIndex;
    }

    public void setEditorialCategoryIndex(int editorialCategoryIndex) {
        this.editorialCategoryIndex = editorialCategoryIndex;
    }

    public String getEditorialImageUrl() {
        return editorialImageUrl;
    }

    public void setEditorialImageUrl(String editorialImageUrl) {
        this.editorialImageUrl = editorialImageUrl;
    }

    public boolean isMustRead() {
        return mustRead;
    }

    public void setMustRead(boolean mustRead) {
        this.mustRead = mustRead;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public void rectifySubHeading(){
        try {
            int nextLineIndex = editorialSubHeading.indexOf("\n");
            if (nextLineIndex < 0 || nextLineIndex > 150) {
                return;
            } else {
                editorialSubHeading = editorialSubHeading.substring(0, nextLineIndex)+"...";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
            return "just now";
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

package utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bunny on 06/04/18.
 */

public class CurrentAffairs implements Serializable {

    String title, content, link, date, category, subHeading;
    int id, categoryIndex;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        resolveSubHeading();
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

        resolveDate();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
        resolveCategory();
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public void resolveCategory() {


        switch (categoryIndex) {

            case 2:
                setCategory("Editorial Analysis");
                break;
            case 3:
                setCategory("Current Affairs");
                break;
            case 5:
                setCategory("The Hindu");
                break;
            case 6:
                setCategory("Indian Express");
                break;
            case 7:
                setCategory("The Hindu");
                break;
            case 8:
                setCategory("Indian Express");
                break;
            case 9:
                setCategory("Live Mint");
                break;
            case 10:
                setCategory("PIB");
                break;
            case 11:
                setCategory("Times of India");
                break;
            case 12:
                setCategory("Economic Times");
                break;
            case 13:
                setCategory("Important Dates");
                break;
            case 14:
                setCategory("News");
                break;
            case 15:
                setCategory("Live Mint");
                break;

            default:
                setCategory("Others");
                break;


        }

    }

    public void resolveSubHeading() {
        int endIndex = content.indexOf(".");

        if (endIndex < 1) {
            return;
        } else if (endIndex > 150) {
            endIndex = 150;
        }

        subHeading = content.substring(0, endIndex);

        subHeading = subHeading.replaceAll("<p>", "\n");
        subHeading = subHeading.replaceAll("</p>", "\n");
        subHeading = subHeading.replaceAll("<br>", "\n");
        subHeading = subHeading + "..";


    }

    private void resolveDate() {
        int endIndex = date.indexOf("T");
        try {


            date = date.substring(0, endIndex);

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date customDate = (Date) formatter.parse(getDate());
            System.out.println("Today is " + customDate.getTime());

            DateFormat dateformatter = new SimpleDateFormat("dd MMM, yyyy");

            setDate(dateformatter.format(customDate.getTime()));

        } catch (Exception e) {

            if (endIndex > 0) {
                date = date.substring(0, endIndex);
            }
            e.printStackTrace();

        }


    }

    public void resolveContent() {
        String str = content;
        str = str.replaceAll("<p>", "\n");
        str = str.replaceAll("</p>", "");
        str = str.replaceAll("<br>", "");

        setContent(str);


    }

}

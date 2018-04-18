package utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bunny on 06/04/18.
 */

public class CurrentAffairs implements Serializable {

    String title, content, link, date, category, subHeading, articleType,tag;
    int id, categoryIndex, tagIndex;

    boolean readStatus;

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

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTagIndex() {
        return tagIndex;
    }

    public void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
        resolveTag();
    }

    private void resolveTag() {

        switch (tagIndex) {

            case 22:
                setTag("International Relation");
                break;
            case 21:
                setTag("Economics");
                break;
            case 23:
                setTag("Polity");
                break;
            case 24:
                setTag("Science & Tech");
                break;
            case 25:
                setTag("Environment");
                break;
            case 26:
                setTag("National");
                break;
            case 27:
                setTag("Awards");
                break;

            default:
                setCategory("");
                break;


        }
    }

    public void resolveCategory() {


        switch (categoryIndex) {

            case 2:
                setCategory("Editorial Analysis");
                setArticleType("Editorial Analysis");
                break;
            case 3:
                setCategory("Current Affairs");
                setArticleType("Current Affairs");
                break;
            case 5:
                setCategory("The Hindu");
                setArticleType("Editorial Analysis");
                break;
            case 6:
                setCategory("Indian Express");
                setArticleType("Editorial Analysis");
                break;
            case 7:
                setCategory("The Hindu");
                setArticleType("Current Affairs");

                break;
            case 8:
                setCategory("Indian Express");
                setArticleType("Current Affairs");
                break;
            case 9:
                setCategory("Live Mint");
                setArticleType("Current Affairs");
                break;
            case 10:
                setCategory("PIB");
                setArticleType("Current Affairs");
                break;
            case 11:
                setCategory("Times of India");
                setArticleType("Current Affairs");
                break;
            case 12:
                setCategory("Economic Times");
                setArticleType("Current Affairs");
                break;
            case 13:
                setCategory("Important Dates");
                setArticleType("Current Affairs");
                break;
            case 14:
                setCategory("News");
                setArticleType("Current Affairs");
                break;
            case 15:
                setCategory("Live Mint");
                setArticleType("Editorial Analysis");
                break;

            default:
                setCategory("Others");
                setArticleType("Editorial Analysis");
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

        subHeading = subHeading.replaceAll("<p>", "");
        subHeading = subHeading.replaceAll("</p>", "\n");
        subHeading = subHeading.replaceAll("<br>", "");
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
        str = str.replaceAll("<p>", "");
        str = str.replaceAll("</p>", "\n");
        str = str.replaceAll("<br>", "\n");

        setContent(str);


    }




}

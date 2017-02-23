package app.articles.vacabulary.editorial.gamefever.editorial;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfo {
    private String editorialHeading ,editorialDate ,editorialSource ,editorialID ,editorialSubHeading ,editorialTag ;

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
}

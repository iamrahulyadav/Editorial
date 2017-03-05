package app.articles.vacabulary.editorial.gamefever.editorial;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialExtraInfo {
    private String editorialText ,editorialId ;

    public HashMap<String, Comment> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

    HashMap <String ,Comment> comments;



    public EditorialExtraInfo(String editorialId, String editorialText) {
        this.editorialId = editorialId;
        this.editorialText = editorialText;
    }

    public EditorialExtraInfo() {
    }


    public String getEditorialText() {
        return editorialText;
    }

    public void setEditorialText(String editorialText) {
        this.editorialText = editorialText;
    }

    public String getEditorialId() {
        return editorialId;
    }

    public void setEditorialId(String editorialId) {
        this.editorialId = editorialId;
    }
}

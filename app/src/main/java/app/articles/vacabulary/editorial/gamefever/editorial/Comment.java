package app.articles.vacabulary.editorial.gamefever.editorial;

/**
 * Created by gamef on 05-03-2017.
 */

public class Comment {
    private String eMailID ,commentText ;

    public Comment() {
    }

    public String geteMailID() {
        return eMailID;
    }

    public void seteMailID(String eMailID) {
        this.eMailID = eMailID;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "eMailID='" + eMailID + '\'' +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}

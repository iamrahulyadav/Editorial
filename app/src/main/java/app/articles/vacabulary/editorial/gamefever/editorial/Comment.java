package app.articles.vacabulary.editorial.gamefever.editorial;

/**
 * Created by gamef on 05-03-2017.
 */

public class Comment {
    private String eMailID;
    private String commentText;

    public Comment(String commentDate, String eMailID) {
        this.commentDate = commentDate;
        this.eMailID = eMailID;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    private String commentDate ;

    public Comment() {
        this.commentDate="";
        this.commentText="";
        this.eMailID="";
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

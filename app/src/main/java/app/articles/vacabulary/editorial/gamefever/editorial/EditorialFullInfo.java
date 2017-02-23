package app.articles.vacabulary.editorial.gamefever.editorial;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialFullInfo {
    private String editorialText ,editorialId ;
    private EditorialGeneralInfo editorialGeneralInfo;

    public EditorialFullInfo() {

    }


    public EditorialFullInfo(String editorialId) {
        this.editorialId = editorialId;
    }

    public EditorialFullInfo(String editorialText, String editorialId, EditorialGeneralInfo EditorialGeneralInfo) {
        this.editorialText = editorialText;
        this.editorialId = editorialId;
        this.editorialGeneralInfo = EditorialGeneralInfo;
    }


    public String getEditorialId() {
        return editorialId;
    }

    public void setEditorialId(String editorialId) {
        this.editorialId = editorialId;
    }

    public String getEditorialText() {
        return editorialText;
    }

    public void setEditorialText(String editorialText) {
        this.editorialText = editorialText;
    }

    public EditorialGeneralInfo getEditorialGeneralInfo() {
        return editorialGeneralInfo;
    }

    public void setEditorialGeneralInfo(EditorialGeneralInfo EditorialGeneralInfo) {
        this.editorialGeneralInfo = EditorialGeneralInfo;
    }

    public boolean checkEditorialText(){
      /*check wether the fetched data is of the same id or not or the hext belong to this heading or noy
      * true mean correct data
      * false mean incorrect*/

        if (editorialId.equalsIgnoreCase(editorialGeneralInfo.getEditorialID())){
            return true;
        }else{
            return false;
        }


    }
}

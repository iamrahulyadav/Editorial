package app.articles.vacabulary.editorial.gamefever.editorial;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialFullInfo {
    private EditorialGeneralInfo editorialGeneralInfo;
    private EditorialExtraInfo editorialExtraInfo;

    public EditorialFullInfo() {
        this.setEditorialExtraInfo(new EditorialExtraInfo());
        this.setEditorialGeneralInfo(new EditorialGeneralInfo());

    }



    public EditorialFullInfo(EditorialGeneralInfo editorialGeneralInfo, EditorialExtraInfo editorialExtraInfo) {
        this.editorialGeneralInfo = editorialGeneralInfo;
        this.editorialExtraInfo = editorialExtraInfo;
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

        if (editorialExtraInfo.getEditorialId().equalsIgnoreCase(editorialGeneralInfo.getEditorialID())){
            return true;
        }else{
            return false;
        }


    }

    public EditorialExtraInfo getEditorialExtraInfo() {
        return editorialExtraInfo;
    }

    public void setEditorialExtraInfo(EditorialExtraInfo editorialExtraInfo) {
        this.editorialExtraInfo = editorialExtraInfo;
    }
}

package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bunny on 06/04/18.
 */

public class JsonParser {

    public ArrayList<CurrentAffairs> parseCurrentAffairsList(JSONArray response){


        ArrayList<CurrentAffairs> currentAffairsArrayList = new ArrayList<>();

        try {

            CurrentAffairs currentAffairs;

        for(int i = 0 ; i<response.length();i++){

            currentAffairs = new CurrentAffairs();

            JSONObject jsonObject = response.getJSONObject(i);

            currentAffairs.setDate(jsonObject.getString("date"));
            currentAffairs.setLink(jsonObject.getString("link"));

            currentAffairs.setTitle(jsonObject.getJSONObject("title").getString("rendered"));

            currentAffairs.setContent(jsonObject.getJSONObject("content").getString("rendered"));

            currentAffairs.setId(jsonObject.getInt("id"));

            currentAffairs.setCategory(jsonObject.getJSONArray("categories").getInt(0));


            currentAffairsArrayList.add(currentAffairs);

        }

        }catch (Exception e){
            e.printStackTrace();
        }

        return currentAffairsArrayList;

    }

}

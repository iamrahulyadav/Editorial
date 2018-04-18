package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bunny on 06/04/18.
 */

public class JsonParser {

    public ArrayList<CurrentAffairs> parseCurrentAffairsList(JSONArray response) {


        ArrayList<CurrentAffairs> currentAffairsArrayList = new ArrayList<>();

        try {

            CurrentAffairs currentAffairs;

            for (int i = 0; i < response.length(); i++) {

                currentAffairs = new CurrentAffairs();

                JSONObject jsonObject = response.getJSONObject(i);

                currentAffairs.setDate(jsonObject.getString("date"));
                currentAffairs.setLink(jsonObject.getString("link"));

                currentAffairs.setTitle(jsonObject.getJSONObject("title").getString("rendered"));

                currentAffairs.setContent(jsonObject.getJSONObject("content").getString("rendered"));

                currentAffairs.setId(jsonObject.getInt("id"));

                if (jsonObject.getJSONArray("categories").length() > 1) {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(1));
                } else {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(0));
                }

                if (jsonObject.getJSONArray("tags").length() != 0) {
                    currentAffairs.setTagIndex(jsonObject.getJSONArray("tags").getInt(0));
                }



                currentAffairsArrayList.add(currentAffairs);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentAffairsArrayList;

    }

    public ArrayList<CurrentAffairs> parseCurrentAffairsList(String string) {



        ArrayList<CurrentAffairs> currentAffairsArrayList = new ArrayList<>();

        try {

            JSONArray response = new JSONArray(string);


            CurrentAffairs currentAffairs;

            for (int i = 0; i < response.length(); i++) {

                currentAffairs = new CurrentAffairs();

                JSONObject jsonObject = response.getJSONObject(i);

                currentAffairs.setDate(jsonObject.getString("date"));
                currentAffairs.setLink(jsonObject.getString("link"));

                currentAffairs.setTitle(jsonObject.getJSONObject("title").getString("rendered"));

                currentAffairs.setContent(jsonObject.getJSONObject("content").getString("rendered"));

                currentAffairs.setId(jsonObject.getInt("id"));

                if (jsonObject.getJSONArray("categories").length() > 1) {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(1));
                } else {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(0));
                }



                currentAffairsArrayList.add(currentAffairs);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentAffairsArrayList;

    }

    public CurrentAffairs parseCurrentAffairs(JSONObject jsonObject) {


        CurrentAffairs currentAffairs=null;

        try {




                currentAffairs = new CurrentAffairs();


                currentAffairs.setDate(jsonObject.getString("date"));
                currentAffairs.setLink(jsonObject.getString("link"));

                currentAffairs.setTitle(jsonObject.getJSONObject("title").getString("rendered"));

                currentAffairs.setContent(jsonObject.getJSONObject("content").getString("rendered"));

                currentAffairs.setId(jsonObject.getInt("id"));

                if (jsonObject.getJSONArray("categories").length() > 1) {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(1));
                } else {
                    currentAffairs.setCategoryIndex(jsonObject.getJSONArray("categories").getInt(0));
                }






        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentAffairs;

    }


}

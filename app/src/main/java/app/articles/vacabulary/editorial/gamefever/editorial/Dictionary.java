package app.articles.vacabulary.editorial.gamefever.editorial;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class Dictionary {
    private String word;
    private String wordMeaning;
    private String wordUsage;
    private String wordPartOfSpeech;
    private String[] wordsynonym;
    private boolean meaningFetched;
    private boolean synonymsFetched;
    private EditorialFeedActivity editorialFeedActivity;


    public Dictionary(String word) {
        intializemeaning();
        intializeSynonms();
        this.word = word;

    }

    public Dictionary() {
        intializemeaning();
        intializeSynonms();
    }

    public Dictionary(String word, String wordMeaning, String wordUsage, String wordPartOfSpeech, String[] wordsynonym) {
        this.word = word;
        this.wordMeaning = wordMeaning;
        this.wordUsage = wordUsage;
        this.wordPartOfSpeech = wordPartOfSpeech;
        this.wordsynonym = wordsynonym;
    }


    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordUsage() {
        return wordUsage;
    }

    public void setWordUsage(String wordUsage) {
        this.wordUsage = wordUsage;
    }

    public String getWordPartOfSpeech() {
        return wordPartOfSpeech;
    }

    public void setWordPartOfSpeech(String wordPartOfSpeech) {
        this.wordPartOfSpeech = wordPartOfSpeech;
    }

    public String[] getWordsynonym() {
        return wordsynonym;
    }

    public void setWordsynonym(String[] wordsynonym) {
        this.wordsynonym = wordsynonym;
    }

    public boolean isMeaningFetched() {
        return meaningFetched;
    }

    public void setMeaningFetched(boolean meaningFetched) {
        this.meaningFetched = meaningFetched;
        if (!meaningFetched) {
            intializemeaning();
        }

    }


    public boolean isSynonymsFetched() {
        return synonymsFetched;
    }

    public void setSynonymsFetched(boolean synonymsFetched) {
        this.synonymsFetched = synonymsFetched;
        if (!synonymsFetched) {
            intializeSynonms();
        }
    }

    private void intializeSynonms() {

        setWordsynonym(new String[]{"null"});

    }

    private void intializemeaning() {
        setWord(word);
        setWordMeaning("null");
        setWordPartOfSpeech("null");
        setWordUsage("null");


    }


    @Override
    public String toString() {
        return "Dictionary{" +
                "word='" + word + '\'' +
                ", wordMeaning='" + wordMeaning + '\'' +
                ", wordUsage='" + wordUsage + '\'' +
                ", wordPartOfSpeech='" + wordPartOfSpeech + '\'' +
                ", wordsynonyms='" + wordsynonym + '\'' +
                '}';
    }


    public void fetchWordMeaning(String mword, EditorialFeedActivity activity) {
        this.setWord(mword.trim());
        editorialFeedActivity = activity;
        new GetWordMeaning().execute();
        //fetchWordMeaning();
        Log.d("My TAg", "after Getwordmeaning call");
        new GetRelatedWord().execute();
        Log.d("My TAg", "after GetWordSynonym call");

    }


    public void processWordMeaning() {
    }


    public void completeFetching() {

        if (isMeaningFetched() && isSynonymsFetched()) {
            Log.d("Tag", "completeFetching: " + toString());

            editorialFeedActivity.updateDictionaryText(this);
            /*call activity method and inform dictionary mening done fetching*/
        }

    }

/*


    private void fetchWordMeaning() {
        String url = "https://owlbot.info/api/v1/dictionary/offered?format=json";
        RequestQueue requestQueue = Volley.newRequestQueue(editorialFeedActivity.getApplicationContext());
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Json", "onResponse: "+response.toString());




                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });


// Access the RequestQueue through your singleton class.
        requestQueue.add(jsObjRequest);
    }
*/

    private class GetWordMeaning extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);


                if (n > 0) out.append(new String(b, 0, n));
            }


            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {


            Log.d("My TAg", "doInBackground: calling rest");
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            // HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");


            HttpGet httpGet = new HttpGet("https://api.pearson.com/v2/dictionaries/lasde/entries?headword="+getWord()+"&apikey=nYfSDm43esItstvE1jH4BNa4ox6xFzWa&limit=2");

            String text = null;

            try {
                Log.d("My TAg", "doInBackground: going to call rest");
                HttpResponse response = httpClient.execute(httpGet, localContext);

                Log.d("My TAg", "doInBackground: done calling rest");
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }


        protected void onPostExecute(String results) {
            if (results != null) {
                try {
                    Log.d("Tag", "onPostExecute: " + results);

                    JSONObject jsonObj = new JSONObject(results);
                    int count = jsonObj.getInt("count");

                    JSONArray jsonArray = jsonObj.getJSONArray("results");

                    if (count>0){


                        JSONObject jsonObjectMeaning1 = jsonArray.getJSONObject(0);
                        setWordPartOfSpeech(jsonObjectMeaning1.getString("part_of_speech"));
                        JSONArray jsonArrayMeaning1 =jsonObjectMeaning1.getJSONArray("senses");


                        JSONObject jsonObjectsenses = jsonArrayMeaning1.getJSONObject(0);


                        setWordMeaning(jsonObjectsenses.getJSONArray("definition").getString(0));



                    }

                    if (count>1){


                        JSONObject jsonObjectMeaning1 = jsonArray.getJSONObject(1);
                        //setWordPartOfSpeech(jsonObjectMeaning1.getString("part_of_speech"));
                        JSONArray jsonArrayMeaning1 =jsonObjectMeaning1.getJSONArray("senses");


                        JSONObject jsonObjectsenses = jsonArrayMeaning1.getJSONObject(0);


                        setWordMeaning(getWordMeaning() +" /\n"+jsonObjectsenses.getJSONArray("definition").getString(0));



                    }




                    //setWord(jsonObj.getString("word"));
                    //setWordMeaning(jsonObj.getString("defenition"));
                    //setWordPartOfSpeech(jsonObj.getString("type"));
                    processWordMeaning();


                } catch (JSONException je) {
                    je.printStackTrace();
                }
                //et.setText(allKey);
                setMeaningFetched(true);
                completeFetching();
                Log.d("my text", "onPostExecute meaning: Executed");
            } else {
                setMeaningFetched(false);
                completeFetching();
            }


        }


    }


  /* private class GetWordMeaning extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);


                if (n > 0) out.append(new String(b, 0, n));
            }


            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {


            Log.d("My TAg", "doInBackground: calling rest");
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            // HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");


            HttpGet httpGet = new HttpGet("http://api.wordnik.com/v4/word.json/"+getWord()+"/definitions?limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");

            String text = null;

            try {
                Log.d("My TAg", "doInBackground: going to call rest");
                HttpResponse response = httpClient.execute(httpGet, localContext);

                Log.d("My TAg", "doInBackground: done calling rest");
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }


        protected void onPostExecute(String results) {
            if (results != null) {
                try {
                    Log.d("Tag", "onPostExecute: "+results);
                    JSONArray jsonArray = new JSONArray(results);
                    JSONObject jsonObj = jsonArray.getJSONObject(0);


                    setWord(jsonObj.getString("word"));
                    setWordMeaning(jsonObj.getString("text"));
                    setWordPartOfSpeech(jsonObj.getString("partOfSpeech"));
                    processWordMeaning();


                } catch (JSONException je) {
                    je.printStackTrace();
                }
                //et.setText(allKey);
                setMeaningFetched(true);
                completeFetching();
                Log.d("my text", "onPostExecute meaning: Executed");
            }else{
                setMeaningFetched(false);
                completeFetching();
            }


        }


    }
*/

    private class GetRelatedWord extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);


                if (n > 0) out.append(new String(b, 0, n));
            }


            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {


            Log.d("My TAg", "doInBackground: calling rest");
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            // HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");

            HttpGet httpGet = new HttpGet("http://api.wordnik.com/v4/word.json/" + getWord() + "/relatedWords?useCanonical=false&relationshipTypes=synonym&limitPerRelationshipType=3&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");

            String text = null;

            try {
                Log.d("My TAg", "doInBackground: going to call rest");
                HttpResponse response = httpClient.execute(httpGet, localContext);

                Log.d("My TAg", "doInBackground: done calling rest");
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }


        protected void onPostExecute(String results) {
            if (results != null) {
                try {
                    Log.d("Tag", "onPostExecute: " + results);
                    JSONArray jsonArray = new JSONArray(results);
                    JSONObject jsonObj = jsonArray.getJSONObject(0);
                    jsonArray = jsonObj.getJSONArray("words");
                    String[] stringarr = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringarr[i] = jsonArray.getString(i);
                    }
                    setWordsynonym(stringarr);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
                //et.setText(allKey);
                setSynonymsFetched(true);
                completeFetching();
                Log.d("my text", "onPostExecute synonyms: Executed");
            } else {
                setSynonymsFetched(false);
                completeFetching();
                Log.d("my text", "onPostExecute synonyms: failed to fetch synonym");
            }

        }


    }


}

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
    private String wordExtra;

    public Dictionary(String word) {
        this.word = word;
    }

    public Dictionary() {
    }

    public Dictionary(String word, String wordMeaning, String wordUsage, String wordPartOfSpeech, String wordExtra) {
        this.word = word;
        this.wordMeaning = wordMeaning;
        this.wordUsage = wordUsage;
        this.wordPartOfSpeech = wordPartOfSpeech;
        this.wordExtra = wordExtra;
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

    public String getWordExtra() {
        return wordExtra;
    }

    public void setWordExtra(String wordExtra) {
        this.wordExtra = wordExtra;
    }


    @Override
    public String toString() {
        return "Dictionary{" +
                "word='" + word + '\'' +
                ", wordMeaning='" + wordMeaning + '\'' +
                ", wordUsage='" + wordUsage + '\'' +
                ", wordPartOfSpeech='" + wordPartOfSpeech + '\'' +
                ", wordExtra='" + wordExtra + '\'' +
                '}';
    }


    public void getWordMeaning(String mword) {

    }

    public void getMeaningFromJSON(String jsonString) {

    }

    public void processWordMeaning() {
    }


    public  void completeFetching()
    {

    }




    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

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

            HttpGet httpGet = new HttpGet("http://api.wordnik.com/v4/word.json/group/definitions?limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");

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
                    JSONArray jsonArray = new JSONArray(results);
                    JSONObject jsonObj = jsonArray.getJSONObject(0);

                    Dictionary dictionary = new Dictionary(jsonObj.getString("word"));
                    dictionary.setWordMeaning(jsonObj.getString("text"));
                    dictionary.processWordMeaning();
                    dictionary.setWordPartOfSpeech(jsonObj.getString("partOfSpeech"));




                } catch (JSONException je) {
                    je.printStackTrace();
                }
                //et.setText(allKey);
                completeFetching();
                Log.d("my text", "onPostExecute: Executed");
            }


        }


    }


}

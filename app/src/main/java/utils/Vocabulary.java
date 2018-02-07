package utils;

import java.io.Serializable;


public class Vocabulary implements Serializable {

    private String mWord, mSynonyms, mAntonyms, mForms, mExample, mRelated, mWordMeaning, mHindiMeaning, mPartOfSpeech, mImageURL, wordID;
    private long timeInMillis;


    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }

    public String getmSynonyms() {
        return mSynonyms;
    }

    public void setmSynonyms(String mSynonyms) {
        this.mSynonyms = mSynonyms;
    }

    public String getmAntonyms() {
        return mAntonyms;
    }

    public void setmAntonyms(String mAntonyms) {
        this.mAntonyms = mAntonyms;
    }

    public String getmForms() {
        return mForms;
    }

    public void setmForms(String mForms) {
        this.mForms = mForms;
    }

    public String getmExample() {
        return mExample;
    }

    public void setmExample(String mExample) {
        this.mExample = mExample;
    }

    public String getmRelated() {
        return mRelated;
    }

    public void setmRelated(String mRelated) {
        this.mRelated = mRelated;
    }

    public String getmWordMeaning() {
        return mWordMeaning;
    }

    public void setmWordMeaning(String mWordMeaning) {
        this.mWordMeaning = mWordMeaning;
    }

    public String getmHindiMeaning() {
        return mHindiMeaning;
    }

    public void setmHindiMeaning(String mHindiMeaning) {
        this.mHindiMeaning = mHindiMeaning;
    }

    public String getmPartOfSpeech() {
        return mPartOfSpeech;
    }

    public void setmPartOfSpeech(String mPartOfSpeech) {
        this.mPartOfSpeech = mPartOfSpeech;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    public String getWordID() {
        return wordID;
    }

    public void setWordID(String wordID) {
        this.wordID = wordID;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}

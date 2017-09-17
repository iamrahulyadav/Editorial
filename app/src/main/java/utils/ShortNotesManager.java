package utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bunny on 15/09/17.
 */

public class ShortNotesManager implements Serializable{
    private String shortNoteHeading ,noteArticleID ,noteArticleSource, noteArticleDate;
    private long shortNoteEditTimeInMillis;

    HashMap<String ,String> shortNotePointList;

    public ShortNotesManager() {
    }

    public ShortNotesManager(HashMap<String ,String> shortNotePointList) {
        this.shortNotePointList =shortNotePointList;
    }

    public String getShortNoteHeading() {
        return shortNoteHeading;
    }

    public void setShortNoteHeading(String shortNoteHeading) {
        this.shortNoteHeading = shortNoteHeading;
    }

    public String getNoteArticleID() {
        return noteArticleID;
    }

    public void setNoteArticleID(String noteArticleID) {
        this.noteArticleID = noteArticleID;
    }

    public String getNoteArticleSource() {
        return noteArticleSource;
    }

    public void setNoteArticleSource(String noteArticleSource) {
        this.noteArticleSource = noteArticleSource;
    }

    public String getNoteArticleDate() {
        return noteArticleDate;
    }

    public void setNoteArticleDate(String noteArticleDate) {
        this.noteArticleDate = noteArticleDate;
    }

    public long getShortNoteEditTimeInMillis() {
        return shortNoteEditTimeInMillis;
    }

    public void setShortNoteEditTimeInMillis(long shortNoteEditTimeInMillis) {
        this.shortNoteEditTimeInMillis = shortNoteEditTimeInMillis;
    }

    public HashMap<String, String> getShortNotePointList() {
        return shortNotePointList;
    }

    public void setShortNotePointList(HashMap<String, String> shortNotePointList) {
        this.shortNotePointList = shortNotePointList;
    }

    public void uploadShortNote(String userUID){
         DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("shortNote/"+userUID+"/"+getNoteArticleID());
        database.setValue(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("uploaded", "onSuccess: ");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }




}

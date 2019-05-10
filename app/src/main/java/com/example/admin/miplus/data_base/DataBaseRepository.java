package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.CheckPoint;
import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.GeoSettings;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.SleepData;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Profile profile;
    private SleepData sleepData;
    private GeoSettings mapType;
    private GeoSettings markerColor;
    private GeoSettings polylineColor;
    private CheckPoint checkPoint;
    private GeoData geoData;

    public void setProfile(Profile profile){
        if(mAuth.getUid() != null){
            db.collection("profiles").document(mAuth.getUid()).set(profile);
        }
    }

    public Task<DocumentSnapshot> getProfileTask() {
        if(mAuth.getUid()!=null){
            final Task<DocumentSnapshot> task = db.collection("profiles").document(mAuth.getUid()).get();
            task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, Profile>(){
                @NonNull
                @Override
                public Task<Profile> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                    if (documentSnapshot != null) {
                        profile = documentSnapshot.toObject(Profile.class);
                    }
                    return null;
                }
            });
            return task;
        } else {
            return null;
        }
    }

    public Profile getProfile(){
        return profile;
    }

    public void setStepsData(StepsData stepsData){
        if (mAuth.getUid() != null){
            db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").document().set(stepsData);
        }

    }

    public Task<QuerySnapshot> getStepsDataListOrderedDate(){
        if (mAuth.getUid() != null){
            return db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").orderBy("date").get();
        } else {
            return null;
        }

    }

    public void setStepsDataByDay(StepsData stepsData){
        if (mAuth.getUid() != null){
            db.collection("stepsData").document(mAuth.getUid()).collection("stepsDay").document().set(stepsData);
        }

    }

    public Task<QuerySnapshot> getStepsDataByDay(){
        if (mAuth.getUid() != null){
            return db.collection("stepsData").document(mAuth.getUid()).collection("stepsDay").orderBy("date").get();
        } else {
            return null;
        }
    }

    public void setSleepData(SleepData sleepData){
        if (mAuth.getUid() != null){
            db.collection("sleepData").document(mAuth.getUid()).collection("sleepDayData").document().set(sleepData);
        }

    }

    public Task<QuerySnapshot> getSleepData(){
        if (mAuth.getUid() != null){
            return db.collection("sleepData").document(mAuth.getUid()).collection("sleepDayData").orderBy("date").get();
        } else {
            return null;
        }
    }

    public void setGeoData(GeoData geoData){
        if (mAuth.getUid() != null){
            db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").document().set(geoData);
        }
    }

    public Task<QuerySnapshot> getGeoDataTask() {
        if (mAuth.getUid() != null){
            final Task<QuerySnapshot> task = db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").get();
            task.onSuccessTask(new SuccessContinuation<QuerySnapshot, GeoData>() {
                @NonNull
                @Override
                public Task<GeoData> then(@Nullable QuerySnapshot querySnapshot) throws Exception {
                    if (querySnapshot != null) {
                        geoData = (GeoData) querySnapshot.toObjects(GeoData.class);
                    }
                    return null;
                }
            });
            return task;
        } else{
            return null;
        }
    }

    public GeoData getGeoData(){
        return geoData;
    }

    public void setMapSettings(GeoSettings mapType){
        if (mAuth.getUid() != null) {
            db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MapType").set(mapType);
        }
    }

    public Task<DocumentSnapshot> getMapSettingsTask() {
        if (mAuth.getUid() != null) {
            final Task<DocumentSnapshot> task = db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MapType").get();
            task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, GeoSettings>() {

                @NonNull
                @Override
                public Task<GeoSettings> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                    if (documentSnapshot != null) {
                        mapType = documentSnapshot.toObject(GeoSettings.class);
                    }
                    return null;
                }
            });
            return task;
        } else {
            return null;
        }
    }

    public GeoSettings getMapSettings(){
        return mapType;
    }

    public void setMarkerColorFS(GeoSettings markerColor){
        if(mAuth.getUid() != null){

        }
        db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MarkerColor").set(markerColor);
    }

    public Task<DocumentSnapshot> getMarkerColorFSTask() {
        if(mAuth.getUid() != null){
            final Task<DocumentSnapshot> task = db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MarkerColor").get();
            task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, GeoSettings>(){

                @NonNull
                @Override
                public Task<GeoSettings> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                    if (documentSnapshot != null) {
                        markerColor = documentSnapshot.toObject(GeoSettings.class);
                    }
                    return null;
                }
            });
            return task;
        } else {
            return null;
        }

    }

    public GeoSettings getMarkerColorFS(){
        return markerColor;
    }

    public void setCheckPoint(CheckPoint checkPoint){
        if(mAuth.getUid() != null){
            db.collection("geopositions").document(mAuth.getUid()).collection("CheckPoint").document(String.valueOf(checkPoint.getNum())).set(checkPoint);
        }

    }

    public Task<QuerySnapshot> getCheckPointTask() {
        if(mAuth.getUid() != null){
            return db.collection("geopositions").document(mAuth.getUid()).collection("CheckPoint").orderBy("date").get();
        } else {
            return null;
        }

    }

    public CheckPoint getCheckPoint(){
        return checkPoint;
    }
}
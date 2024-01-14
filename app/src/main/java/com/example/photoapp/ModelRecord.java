package com.example.photoapp;

//Model calss for RecyclerView
public class ModelRecord {

    //variables
    String id, title, image, description, keyWords, addedTime,updatedTime,address;

    public ModelRecord(String id, String title, String image, String description, String keyWords, String addedTime, String updatedTime, String address) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.keyWords = keyWords;
        this.addedTime = addedTime;
        this.updatedTime = updatedTime;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

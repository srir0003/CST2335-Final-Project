package com.example.finalproject.news;

import java.io.Serializable;

/**
 * This Article class helps to stores data
 * used to get articles from web and displaying on the listview
 * */
public class Article implements Serializable {
    public String author, title, description, urlToImage, url;
    Long Id;
    boolean isSaved = false;

    public Article(Long Id, String title, String description, String url, String urlImage, boolean isSavedValue) {
        this.Id = Id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlImage;
        this.isSaved = isSavedValue;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public boolean IsSaved(){
        return isSaved;
    }

    public void setSaved(boolean saved){
        isSaved = saved;
    }
}

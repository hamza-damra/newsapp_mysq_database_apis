package com.hamza.newsapp.model;

import androidx.annotation.NonNull;

public class Source {
    private int id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String author;
    private String publishDate;

    public Source(String name, String description, String url, String category, String author, String publishDate) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
        this.author = author;
        this.publishDate = publishDate;
    }

    public Source(){}

    // Getters and setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }



    public String getDescription() {
        return this.description;
    }



    public String getUrl() {
        return this.url;
    }





    public String getAuthor() {
        return author;
    }



    public String getPublishDate() {
        return publishDate;
    }



    @NonNull
    @Override
    public String toString() {
        return "Source{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", publishDate=" + publishDate +
                '}';
    }
}

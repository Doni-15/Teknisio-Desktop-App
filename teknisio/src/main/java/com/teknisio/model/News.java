package com.teknisio.model;

/**
 * Model class representing a News Article.
 */
public class News {
    private String title;
    private String date;
    private String thumbnailPath;
    private String readTime;

    public News(String title, String date, String thumbnailPath, String readTime) {
        this.title = title;
        this.date = date;
        this.thumbnailPath = thumbnailPath;
        this.readTime = readTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }
}

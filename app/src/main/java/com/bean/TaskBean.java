package com.bean;

public class TaskBean {
    private int ID = -1;
    private String Title = "";
    private String Words = "";
    private String Color = "2";
    private String Time = "";
    private String BackgroundUri = "-1";
    private int Alpha = 255;
    private String ImageUri = "-1";
    private String MP3Uri = "-1";
    private String VideoUri = "-1";

    public TaskBean() {}

    public TaskBean(String title, String words, String time) {
        this.Title = title;
        this.Words = words;
        this.Time = time;
    }

    public TaskBean(String title, String words, String time, String imageUri) {
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
    }

    public TaskBean(String title, String words, String time, String imageUri, String mp3Uri) {
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
        this.MP3Uri = mp3Uri;
    }

    public TaskBean(String title, String words, String time, String imageUri, String mp3Uri, String videoUri) {
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
        this.MP3Uri = mp3Uri;
        this.VideoUri = videoUri;
    }

    public TaskBean(int id, String title, String words, String time) {
        this.ID = id;
        this.Title = title;
        this.Words = words;
        this.Time = time;
    }

    public TaskBean(int id, String title, String words, String time, String imageUri) {
        this.ID = id;
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
    }

    public TaskBean(int id, String title, String words, String time, String imageUri, String mp3Uri) {
        this.ID = id;
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
        this.MP3Uri = mp3Uri;
    }

    public TaskBean(int id, String title, String words, String time, String imageUri, String mp3Uri, String videoUri) {
        this.ID = id;
        this.Title = title;
        this.Words = words;
        this.Time = time;
        this.ImageUri = imageUri;
        this.MP3Uri = mp3Uri;
        this.VideoUri = videoUri;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {this.ID = id;}

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getWords() {
        return Words;
    }

    public void setWords(String words) {
        this.Words = words;
    }

    public String getColor() { return Color; }

    public void setColor(String color) { this.Color = color; }

    public String getTime() { return Time; }

    public void setTime(String time) { this.Time = time; }

    public String getBackgroundUri() { return BackgroundUri;}

    public void setBackgroundUri(String backgroundUri) { this.BackgroundUri = backgroundUri;}

    public int getAlpha() { return Alpha;}

    public void setAlpha(int alpha) { this.Alpha = alpha;}

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        this.ImageUri = imageUri;
    }

    public String getMP3Uri() {
        return MP3Uri;
    }

    public void setMP3Uri(String mp3Uri) {
        this.MP3Uri = mp3Uri;
    }

    public String getVideoUri() {
        return VideoUri;
    }

    public void setVideoUri(String videoUriUri) {
        this.VideoUri = videoUriUri;
    }

}

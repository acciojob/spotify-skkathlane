package com.driver;

import java.util.List;

public class Playlist {
    private String title;
    private List<Song> songs;

    private User creator;

    public User getListener() {
        return Listener;
    }

    public void setListener(User listener) {
        Listener = listener;
    }

    private User Listener;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Playlist(){

    }

    public Playlist(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

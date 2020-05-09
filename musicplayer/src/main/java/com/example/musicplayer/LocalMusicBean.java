package com.example.musicplayer;


public class LocalMusicBean {
    private String id;//歌曲ID
    private String song;//歌名
    private String singer;//歌手名
    private String duration;//时长
    private String path;//歌曲路径

//    构造函数(两个，一个空参，一个全参
    public LocalMusicBean() {
    }

    public LocalMusicBean(String id, String song, String singer, String duration, String path) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.duration = duration;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

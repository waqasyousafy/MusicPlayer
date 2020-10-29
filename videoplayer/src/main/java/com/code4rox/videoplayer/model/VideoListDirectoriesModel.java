package com.code4rox.videoplayer.model;

public class VideoListDirectoriesModel  {
    private String Videoname;
    private String Videoaddress;
    private String Duration;
    private boolean isSelected = false;

    public VideoListDirectoriesModel(String videoname, String videoaddress, String duration, boolean isSelected) {
        Videoname = videoname;
        Videoaddress = videoaddress;
        Duration = duration;
        this.isSelected = isSelected;
    }



    public String getVideoname() {
        return Videoname;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getVideoaddress() {
        return Videoaddress;
    }

    public void setVideoname(String videoname) {
        Videoname = videoname;
    }

    public void setVideoaddress(String videoaddress) {
        Videoaddress = videoaddress;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

}

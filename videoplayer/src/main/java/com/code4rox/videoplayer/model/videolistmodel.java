package com.code4rox.videoplayer.model;

public class videolistmodel {
    private String Videoname;
    private String Videoaddress;
    private boolean isSelected = false;

    public videolistmodel() {

    }

    public String getVideoname() {
        return Videoname;
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

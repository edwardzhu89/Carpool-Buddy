package com.example.carpoolbuddy;

public class Upload {

    private String url;
    private String name;

    public Upload(String url, String name) {

        if(name.trim().equals("")){
            name = "N/A";
        }

        this.url = url;
        this.name = name;
    }

    public Upload() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

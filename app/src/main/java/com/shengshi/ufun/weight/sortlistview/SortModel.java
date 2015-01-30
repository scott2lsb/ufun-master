package com.shengshi.ufun.weight.sortlistview;

public class SortModel {

    private int id;
    private int cityid;
    private String name; //显示的数据
    private String firstchar;
    private int ifopen;
    private int ishot;
    private String letters; //显示数据上级字母
    private String sortLetters; //显示数据拼音的首字母

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public String getFirstchar() {
        return firstchar;
    }

    public void setFirstchar(String firstchar) {
        this.firstchar = firstchar;
    }

    public int getIfopen() {
        return ifopen;
    }

    public void setIfopen(int ifopen) {
        this.ifopen = ifopen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIshot() {
        return ishot;
    }

    public void setIshot(int ishot) {
        this.ishot = ishot;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }


}

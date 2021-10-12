package com.mocomp.developer.medicbooks.models.content;

public class Item {


    private String maintitle;
    private String smalldesc;
    private String buttondesc;
    private String photoUrl;
    private String desc;
    private String inertitle;

    public Item() {
    }

    public Item(String maintitle , String smalldesc , String buttondesc , String photoUri , String desc ,String inertitle) {
        this.maintitle = maintitle;
        this.smalldesc = smalldesc;
        this.buttondesc = buttondesc;
        this.photoUrl = photoUri;
        this.desc=desc;
        this.inertitle = inertitle;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public String getMaintitle() {
        return maintitle;
    }
    public void setMaintitle(String maintitle) {
        this.maintitle = maintitle;
    }

    public String getSmalldesc() {
        return smalldesc;
    }
    public void setSmalldesc(String smalldesc) {
        this.smalldesc = smalldesc;
    }

    public String getButtondesc() {
        return buttondesc;
    }
    public void setButtondesc(String buttondesc) {
        this.buttondesc = buttondesc;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInertitle() {
        return inertitle;
    }
    public void setInertitle(String inertitle) {
        this.inertitle = inertitle;
    }

}
